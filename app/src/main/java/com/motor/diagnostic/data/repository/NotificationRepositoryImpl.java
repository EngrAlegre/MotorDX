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
import com.motor.diagnostic.data.model.NotificationEntity;
import com.motor.diagnostic.domain.model.Notification;
import com.motor.diagnostic.domain.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of NotificationRepository that uses Firebase
 */
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private static final String NOTIFICATIONS_PATH = "notifications";
    
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    
    private ChildEventListener notificationListener;
    private NotificationListener userListener;
    
    public NotificationRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    
    @Override
    public CompletableFuture<List<Notification>> getUserNotifications(int limit) {
        CompletableFuture<List<Notification>> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Query notifications for the current user
        Query query = databaseReference.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(currentUser.getUid())
                .limitToLast(limit);
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    NotificationEntity entity = notificationSnapshot.getValue(NotificationEntity.class);
                    if (entity != null) {
                        notifications.add(entity.toDomain());
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
    public CompletableFuture<Boolean> markNotificationAsRead(String notificationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Update the notification's read status
        databaseReference.child(NOTIFICATIONS_PATH).child(notificationId).child("isRead")
                .setValue(true)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(e -> future.completeExceptionally(e));
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> markAllNotificationsAsRead() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Query notifications for the current user
        Query query = databaseReference.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(currentUser.getUid());
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    String notificationId = notificationSnapshot.getKey();
                    if (notificationId != null) {
                        databaseReference.child(NOTIFICATIONS_PATH).child(notificationId).child("isRead").setValue(true);
                    }
                }
                
                future.complete(true);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> deleteNotification(String notificationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Verify the notification belongs to the current user
        databaseReference.child(NOTIFICATIONS_PATH).child(notificationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            future.completeExceptionally(new Exception("Notification not found"));
                            return;
                        }
                        
                        NotificationEntity entity = snapshot.getValue(NotificationEntity.class);
                        if (entity == null) {
                            future.completeExceptionally(new Exception("Failed to parse notification data"));
                            return;
                        }
                        
                        if (!entity.getUserId().equals(currentUser.getUid())) {
                            future.completeExceptionally(new Exception("You can only delete your own notifications"));
                            return;
                        }
                        
                        // Delete the notification
                        databaseReference.child(NOTIFICATIONS_PATH).child(notificationId).removeValue()
                                .addOnSuccessListener(aVoid -> future.complete(true))
                                .addOnFailureListener(e -> future.completeExceptionally(e));
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> deleteAllNotifications() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Query notifications for the current user
        Query query = databaseReference.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(currentUser.getUid());
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    String notificationId = notificationSnapshot.getKey();
                    if (notificationId != null) {
                        databaseReference.child(NOTIFICATIONS_PATH).child(notificationId).removeValue();
                    }
                }
                
                future.complete(true);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Integer> getUnreadNotificationCount() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        // Query unread notifications for the current user
        Query query = databaseReference.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(currentUser.getUid());
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    Boolean isRead = notificationSnapshot.child("isRead").getValue(Boolean.class);
                    if (isRead == null || !isRead) {
                        count++;
                    }
                }
                
                future.complete(count);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Notification> saveNotification(Notification notification) {
        CompletableFuture<Notification> future = new CompletableFuture<>();
        
        // Generate an ID if not provided
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID().toString());
        }
        
        // Set timestamp if not set
        if (notification.getTimestamp() == null) {
            notification.setTimestamp(new Date());
        }
        
        // Convert to data entity
        NotificationEntity entity = NotificationEntity.fromDomain(notification);
        
        // Save to Firebase
        databaseReference.child(NOTIFICATIONS_PATH).child(notification.getId()).setValue(entity.toMap())
                .addOnSuccessListener(aVoid -> future.complete(notification))
                .addOnFailureListener(e -> future.completeExceptionally(e));
        
        return future;
    }
    
    @Override
    public void subscribeToNotifications(NotificationListener listener) {
        // Unsubscribe from any existing listener
        unsubscribeFromNotifications();
        
        this.userListener = listener;
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Query notifications for the current user
        Query query = databaseReference.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(currentUser.getUid());
        
        // Create a new listener
        notificationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NotificationEntity entity = snapshot.getValue(NotificationEntity.class);
                if (entity != null && userListener != null) {
                    userListener.onNewNotification(entity.toDomain());
                }
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not needed for notifications
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Not needed for notifications
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not needed for notifications
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No specific error handler for notifications
            }
        };
        
        // Add the listener
        query.addChildEventListener(notificationListener);
    }
    
    @Override
    public void unsubscribeFromNotifications() {
        if (notificationListener != null) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                databaseReference.child(NOTIFICATIONS_PATH)
                        .orderByChild("userId")
                        .equalTo(currentUser.getUid())
                        .removeEventListener(notificationListener);
            }
            
            notificationListener = null;
            userListener = null;
        }
    }
} 