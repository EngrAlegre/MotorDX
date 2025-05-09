package com.motor.diagnostic.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.motor.diagnostic.data.model.MotorcycleEntity;
import com.motor.diagnostic.domain.model.Motorcycle;
import com.motor.diagnostic.domain.repository.MotorcycleRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of MotorcycleRepository that uses Firebase
 */
public class MotorcycleRepositoryImpl implements MotorcycleRepository {
    
    private static final String MOTORCYCLES_PATH = "motorcycles";
    private static final String MOTORCYCLE_IMAGES_PATH = "motorcycle_images";
    private static final String CONNECTIONS_PATH = "connections";
    private static final String DEVICE_ID_ESP32 = "yamaha_001"; // Default ESP32 device ID
    
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    
    public MotorcycleRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }
    
    @Override
    public CompletableFuture<Motorcycle> addMotorcycle(Motorcycle motorcycle) {
        CompletableFuture<Motorcycle> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Generate a unique ID if one is not provided
        if (motorcycle.getId() == null) {
            motorcycle.setId(UUID.randomUUID().toString());
        }
        
        // Set the current user as the owner
        motorcycle.setUserId(currentUser.getUid());
        
        // Set the registration date if not set
        if (motorcycle.getRegistrationDate() == null) {
            motorcycle.setRegistrationDate(new Date());
        }
        
        // Convert to data entity
        MotorcycleEntity entity = MotorcycleEntity.fromDomain(motorcycle);
        
        // Save to Firebase
        databaseReference.child(MOTORCYCLES_PATH).child(motorcycle.getId()).setValue(entity.toMap())
                .addOnSuccessListener(aVoid -> future.complete(motorcycle))
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<Motorcycle> getMotorcycle(String motorcycleId) {
        CompletableFuture<Motorcycle> future = new CompletableFuture<>();
        
        databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            MotorcycleEntity entity = snapshot.getValue(MotorcycleEntity.class);
                            if (entity != null) {
                                future.complete(entity.toDomain());
                            } else {
                                future.completeExceptionally(new Exception("Failed to parse motorcycle data"));
                            }
                        } else {
                            future.completeExceptionally(new Exception("Motorcycle not found"));
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
    public CompletableFuture<List<Motorcycle>> getUserMotorcycles() {
        CompletableFuture<List<Motorcycle>> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        String userId = currentUser.getUid();
        
        // Query motorcycles by userId
        Query query = databaseReference.child(MOTORCYCLES_PATH).orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Motorcycle> motorcycles = new ArrayList<>();
                
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MotorcycleEntity entity = dataSnapshot.getValue(MotorcycleEntity.class);
                    if (entity != null) {
                        motorcycles.add(entity.toDomain());
                    }
                }
                
                future.complete(motorcycles);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Motorcycle> updateMotorcycle(Motorcycle motorcycle) {
        CompletableFuture<Motorcycle> future = new CompletableFuture<>();
        
        if (motorcycle.getId() == null) {
            future.completeExceptionally(new Exception("Motorcycle ID cannot be null"));
            return future;
        }
        
        // Verify the motorcycle belongs to the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        if (!motorcycle.getUserId().equals(currentUser.getUid())) {
            future.completeExceptionally(new Exception("You can only update your own motorcycles"));
            return future;
        }
        
        // Convert to data entity
        MotorcycleEntity entity = MotorcycleEntity.fromDomain(motorcycle);
        
        // Update in Firebase
        databaseReference.child(MOTORCYCLES_PATH).child(motorcycle.getId()).updateChildren(entity.toMap())
                .addOnSuccessListener(aVoid -> future.complete(motorcycle))
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> deleteMotorcycle(String motorcycleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Verify the motorcycle belongs to the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // First, check if the motorcycle exists and belongs to the user
        databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            future.completeExceptionally(new Exception("Motorcycle not found"));
                            return;
                        }
                        
                        MotorcycleEntity entity = snapshot.getValue(MotorcycleEntity.class);
                        if (entity == null) {
                            future.completeExceptionally(new Exception("Failed to parse motorcycle data"));
                            return;
                        }
                        
                        if (!entity.getUserId().equals(currentUser.getUid())) {
                            future.completeExceptionally(new Exception("You can only delete your own motorcycles"));
                            return;
                        }
                        
                        // Now we can safely delete
                        databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // Also clean up any connections
                                    databaseReference.child(CONNECTIONS_PATH).child(motorcycleId).removeValue();
                                    
                                    // And attempt to delete the image if exists
                                    if (entity.getImageUrl() != null && !entity.getImageUrl().isEmpty()) {
                                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(entity.getImageUrl());
                                        imageRef.delete();
                                    }
                                    
                                    future.complete(true);
                                })
                                .addOnFailureListener(future::completeExceptionally);
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> connectMotorcycle(String motorcycleId, String deviceId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // If no device ID provided, use the default ESP32 device ID
        final String finalDeviceId = (deviceId == null || deviceId.isEmpty()) ? DEVICE_ID_ESP32 : deviceId;
        
        // Create a connection record
        Map<String, Object> connectionData = new HashMap<>();
        connectionData.put("motorcycleId", motorcycleId);
        connectionData.put("deviceId", finalDeviceId);
        connectionData.put("connectedAt", System.currentTimeMillis());
        connectionData.put("isActive", true);
        
        // Update the connection status in Firebase
        databaseReference.child(CONNECTIONS_PATH).child(motorcycleId).setValue(connectionData)
                .addOnSuccessListener(aVoid -> {
                    // Also update the motorcycle's connection status
                    databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId).child("isConnected").setValue(true)
                            .addOnSuccessListener(aVoid2 -> {
                                // And update the deviceId
                                databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId).child("deviceId").setValue(finalDeviceId)
                                        .addOnSuccessListener(aVoid3 -> future.complete(true))
                                        .addOnFailureListener(future::completeExceptionally);
                            })
                            .addOnFailureListener(future::completeExceptionally);
                })
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> disconnectMotorcycle(String motorcycleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Update the connection status in Firebase
        databaseReference.child(CONNECTIONS_PATH).child(motorcycleId).child("isActive").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    // Also update the motorcycle's connection status
                    databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId).child("isConnected").setValue(false)
                            .addOnSuccessListener(aVoid2 -> future.complete(true))
                            .addOnFailureListener(future::completeExceptionally);
                })
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> isMotorcycleConnected(String motorcycleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Check the connection status
        databaseReference.child(CONNECTIONS_PATH).child(motorcycleId).child("isActive")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Boolean isActive = snapshot.getValue(Boolean.class);
                            future.complete(isActive != null && isActive);
                        } else {
                            future.complete(false);
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
    public CompletableFuture<String> updateMotorcycleImage(String motorcycleId, String imagePath) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        // Verify the motorcycle exists and belongs to the current user
        getMotorcycle(motorcycleId)
                .thenAccept(motorcycle -> {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser == null) {
                        future.completeExceptionally(new Exception("No user is currently logged in"));
                        return;
                    }
                    
                    if (!motorcycle.getUserId().equals(currentUser.getUid())) {
                        future.completeExceptionally(new Exception("You can only update your own motorcycles"));
                        return;
                    }
                    
                    // Upload the image
                    StorageReference imageRef = storageReference.child(MOTORCYCLE_IMAGES_PATH + "/" + motorcycleId + ".jpg");
                    
                    // Convert local file path to Uri
                    Uri fileUri = Uri.parse("file://" + imagePath);
                    
                    imageRef.putFile(fileUri)
                            .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        
                                        // Update motorcycle's image URL in database
                                        databaseReference.child(MOTORCYCLES_PATH).child(motorcycleId).child("imageUrl")
                                                .setValue(imageUrl)
                                                .addOnSuccessListener(aVoid -> future.complete(imageUrl))
                                                .addOnFailureListener(future::completeExceptionally);
                                    })
                                    .addOnFailureListener(future::completeExceptionally))
                            .addOnFailureListener(future::completeExceptionally);
                })
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });
        
        return future;
    }
} 