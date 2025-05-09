package com.motor.diagnostic.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.motor.diagnostic.data.model.DiagnosticDataEntity;
import com.motor.diagnostic.data.model.NotificationEntity;
import com.motor.diagnostic.domain.model.DiagnosticData;
import com.motor.diagnostic.domain.model.Notification;
import com.motor.diagnostic.domain.repository.DiagnosticRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of DiagnosticRepository that uses Firebase
 */
public class DiagnosticRepositoryImpl implements DiagnosticRepository {
    
    private static final String DIAGNOSTICS_PATH = "diagnostics";
    private static final String ALERTS_PATH = "alerts";
    private static final String SESSIONS_PATH = "sessions";
    private static final String DEVICE_ID_ESP32 = "yamaha_001"; // Default ESP32 device ID
    
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    
    // Maps to keep track of listeners for each motorcycle
    private final Map<String, ValueEventListener> diagnosticListeners;
    private final Map<String, ChildEventListener> alertListeners;
    
    public DiagnosticRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.diagnosticListeners = new HashMap<>();
        this.alertListeners = new HashMap<>();
    }
    
    @Override
    public CompletableFuture<DiagnosticData> getCurrentDiagnosticData(String motorcycleId) {
        CompletableFuture<DiagnosticData> future = new CompletableFuture<>();
        
        // Get the current diagnostic data for the motorcycle
        databaseReference.child(DIAGNOSTICS_PATH).child(motorcycleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DiagnosticDataEntity entity = snapshot.getValue(DiagnosticDataEntity.class);
                            if (entity != null) {
                                future.complete(entity.toDomain());
                            } else {
                                future.completeExceptionally(new Exception("Failed to parse diagnostic data"));
                            }
                        } else {
                            // If not found, return an empty diagnostic data
                            DiagnosticData emptyData = new DiagnosticData();
                            emptyData.setMotorcycleId(motorcycleId);
                            emptyData.setTimestamp(new Date());
                            future.complete(emptyData);
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });
        
        return future;
    }
    
    @Override
    public CompletableFuture<List<DiagnosticData>> getHistoricalDiagnosticData(
            String motorcycleId, Date startDate, Date endDate) {
        CompletableFuture<List<DiagnosticData>> future = new CompletableFuture<>();
        
        Long startTimestamp = startDate != null ? startDate.getTime() : null;
        Long endTimestamp = endDate != null ? endDate.getTime() : null;
        
        // Query based on timestamp range
        Query query = databaseReference.child(DIAGNOSTICS_PATH)
                .orderByChild("motorcycleId")
                .equalTo(motorcycleId);
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DiagnosticData> dataList = new ArrayList<>();
                
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DiagnosticDataEntity entity = dataSnapshot.getValue(DiagnosticDataEntity.class);
                    if (entity != null) {
                        // Filter by timestamp if provided
                        if (startTimestamp != null && (entity.getTimestamp() == null || entity.getTimestamp() < startTimestamp)) {
                            continue;
                        }
                        if (endTimestamp != null && (entity.getTimestamp() == null || entity.getTimestamp() > endTimestamp)) {
                            continue;
                        }
                        
                        dataList.add(entity.toDomain());
                    }
                }
                
                future.complete(dataList);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> startDiagnosticSession(String motorcycleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Create a new session record
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("motorcycleId", motorcycleId);
        sessionData.put("startTime", System.currentTimeMillis());
        sessionData.put("isActive", true);
        sessionData.put("deviceId", DEVICE_ID_ESP32); // Default to ESP32 device
        
        // Save the session to Firebase
        databaseReference.child(SESSIONS_PATH).child(sessionId).setValue(sessionData)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(e -> future.completeExceptionally(e));
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> endDiagnosticSession(String motorcycleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Find active sessions for this motorcycle
        databaseReference.child(SESSIONS_PATH)
                .orderByChild("motorcycleId")
                .equalTo(motorcycleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        
                        for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                            Boolean isActive = sessionSnapshot.child("isActive").getValue(Boolean.class);
                            if (isActive != null && isActive) {
                                // Update the session
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("endTime", System.currentTimeMillis());
                                updates.put("isActive", false);
                                
                                String sessionId = sessionSnapshot.getKey();
                                databaseReference.child(SESSIONS_PATH).child(sessionId).updateChildren(updates);
                                found = true;
                            }
                        }
                        
                        future.complete(found);
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> saveDiagnosticData(DiagnosticData diagnosticData) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        try {
            // Generate an ID if not provided
            if (diagnosticData.getId() == null) {
                diagnosticData.setId(UUID.randomUUID().toString());
            }
            
            // Set timestamp if not set
            if (diagnosticData.getTimestamp() == null) {
                diagnosticData.setTimestamp(new Date());
            }
            
            // Ensure motorcycleId is not null
            if (diagnosticData.getMotorcycleId() == null) {
                diagnosticData.setMotorcycleId(DEVICE_ID_ESP32);
            }
            
            // Convert to data entity
            DiagnosticDataEntity entity = DiagnosticDataEntity.fromDomain(diagnosticData);
            
            // Save to Firebase
            databaseReference.child(DIAGNOSTICS_PATH).child(diagnosticData.getMotorcycleId())
                    .setValue(entity.toMap())
                    .addOnSuccessListener(aVoid -> future.complete(true))
                    .addOnFailureListener(e -> future.completeExceptionally(e));
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    @Override
    public CompletableFuture<List<Notification>> getDiagnosticNotifications(String motorcycleId, int limit) {
        CompletableFuture<List<Notification>> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Query alerts for this motorcycle
        Query query = databaseReference.child(ALERTS_PATH).child(motorcycleId)
                .orderByChild("timestamp")
                .limitToLast(limit);
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                
                for (DataSnapshot alertSnapshot : snapshot.getChildren()) {
                    NotificationEntity entity = alertSnapshot.getValue(NotificationEntity.class);
                    if (entity != null) {
                        Notification notification = entity.toDomain();
                        
                        // Set the user ID to the current user
                        if (notification.getUserId() == null) {
                            notification.setUserId(currentUser.getUid());
                        }
                        
                        notifications.add(notification);
                    }
                }
                
                future.complete(notifications);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public void subscribeToDiagnosticUpdates(String motorcycleId, DiagnosticUpdateListener listener) {
        // Remove any existing listener for this motorcycle
        unsubscribeFromDiagnosticUpdates(motorcycleId);
        
        // Create a new listener
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DiagnosticDataEntity entity = snapshot.getValue(DiagnosticDataEntity.class);
                    if (entity != null) {
                        listener.onDiagnosticUpdate(entity.toDomain());
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onDiagnosticError(error.toException());
            }
        };
        
        // Add the listener
        databaseReference.child(DIAGNOSTICS_PATH).child(motorcycleId).addValueEventListener(valueListener);
        
        // Store the listener for later removal
        diagnosticListeners.put(motorcycleId, valueListener);
    }
    
    @Override
    public void unsubscribeFromDiagnosticUpdates(String motorcycleId) {
        ValueEventListener listener = diagnosticListeners.get(motorcycleId);
        if (listener != null) {
            databaseReference.child(DIAGNOSTICS_PATH).child(motorcycleId).removeEventListener(listener);
            diagnosticListeners.remove(motorcycleId);
        }
    }
    
    @Override
    public void startAlertMonitoring(String motorcycleId, AlertListener listener) {
        // Remove any existing listener for this motorcycle
        stopAlertMonitoring(motorcycleId);
        
        // Create a new listener
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NotificationEntity entity = snapshot.getValue(NotificationEntity.class);
                if (entity != null) {
                    Notification notification = entity.toDomain();
                    
                    // Set the user ID to the current user if not already set
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (notification.getUserId() == null && currentUser != null) {
                        notification.setUserId(currentUser.getUid());
                    }
                    
                    listener.onAlert(notification);
                }
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not needed for alerts
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Not needed for alerts
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not needed for alerts
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No specific error handler for alerts
            }
        };
        
        // Add the listener
        databaseReference.child(ALERTS_PATH).child(motorcycleId).addChildEventListener(childListener);
        
        // Store the listener for later removal
        alertListeners.put(motorcycleId, childListener);
    }
    
    @Override
    public void stopAlertMonitoring(String motorcycleId) {
        ChildEventListener listener = alertListeners.get(motorcycleId);
        if (listener != null) {
            databaseReference.child(ALERTS_PATH).child(motorcycleId).removeEventListener(listener);
            alertListeners.remove(motorcycleId);
        }
    }
} 