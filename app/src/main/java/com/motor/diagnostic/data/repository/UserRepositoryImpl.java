package com.motor.diagnostic.data.repository;

import androidx.annotation.NonNull;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.motor.diagnostic.data.model.UserEntity;
import com.motor.diagnostic.domain.model.User;
import com.motor.diagnostic.domain.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of UserRepository that uses Firebase
 */
public class UserRepositoryImpl implements UserRepository {
    
    private static final String USERS_PATH = "users";
    private static final String PROFILE_IMAGES_PATH = "profile_images";
    
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    
    public UserRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }
    
    @Override
    public CompletableFuture<User> registerUser(String email, String password, User user) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        user.setId(firebaseUser.getUid());
                        user.setEmail(email);
                        
                        // Update Firebase Auth profile
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getFullName())
                                .build();
                        
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnSuccessListener(aVoid -> saveUserToDatabase(user, future))
                                .addOnFailureListener(future::completeExceptionally);
                    } else {
                        future.completeExceptionally(new Exception("Failed to create user"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    private void saveUserToDatabase(User user, CompletableFuture<User> future) {
        String userId = user.getId();
        UserEntity userEntity = UserEntity.fromDomain(user);
        
        databaseReference.child(USERS_PATH).child(userId).setValue(userEntity.toMap())
                .addOnSuccessListener(aVoid -> future.complete(user))
                .addOnFailureListener(future::completeExceptionally);
    }
    
    @Override
    public CompletableFuture<User> loginUser(String email, String password) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        // For ESP32 hardcoded credentials
        if (email.equals("user@motor.com") && password.equals("motor0303")) {
            // Special handling for ESP32 device
            User espUser = new User();
            espUser.setId("esp32");
            espUser.setFullName("ESP32 Device");
            espUser.setEmail(email);
            future.complete(espUser);
            return future;
        }
        
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        getUserFromDatabase(firebaseUser.getUid(), future);
                    } else {
                        future.completeExceptionally(new Exception("Failed to get user after login"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<User> getCurrentUser() {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            getUserFromDatabase(firebaseUser.getUid(), future);
        } else {
            future.completeExceptionally(new Exception("No user is currently logged in"));
        }
        
        return future;
    }
    
    private void getUserFromDatabase(String userId, CompletableFuture<User> future) {
        databaseReference.child(USERS_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserEntity userEntity = snapshot.getValue(UserEntity.class);
                    if (userEntity != null) {
                        future.complete(userEntity.toDomain());
                    } else {
                        future.completeExceptionally(new Exception("Failed to parse user data"));
                    }
                } else {
                    // User exists in Auth but not in Database, create a basic record
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User();
                        user.setId(firebaseUser.getUid());
                        user.setEmail(firebaseUser.getEmail());
                        user.setFullName(firebaseUser.getDisplayName());
                        
                        saveUserToDatabase(user, future);
                    } else {
                        future.completeExceptionally(new Exception("User not found in database"));
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
    }
    
    @Override
    public CompletableFuture<User> updateUser(User user) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        if (user.getId() == null) {
            future.completeExceptionally(new Exception("User ID cannot be null"));
            return future;
        }
        
        UserEntity userEntity = UserEntity.fromDomain(user);
        
        databaseReference.child(USERS_PATH).child(user.getId()).updateChildren(userEntity.toMap())
                .addOnSuccessListener(aVoid -> {
                    // Update display name in Firebase Auth
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getFullName())
                                .build();
                        
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnSuccessListener(v -> future.complete(user))
                                .addOnFailureListener(future::completeExceptionally);
                    } else {
                        future.complete(user);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> changePassword(String oldPassword, String newPassword) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.getEmail() != null) {
            // Re-authenticate the user first
            firebaseAuth.signInWithEmailAndPassword(firebaseUser.getEmail(), oldPassword)
                    .addOnSuccessListener(authResult -> 
                            firebaseUser.updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid -> future.complete(true))
                                    .addOnFailureListener(e -> future.completeExceptionally(e)))
                    .addOnFailureListener(future::completeExceptionally);
        } else {
            future.completeExceptionally(new Exception("No user is currently logged in"));
        }
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> resetPassword(String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }
    
    @Override
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
    
    @Override
    public CompletableFuture<String> updateProfileImage(String imagePath) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            future.completeExceptionally(new Exception("No user is currently logged in"));
            return future;
        }
        
        String userId = firebaseUser.getUid();
        StorageReference imageRef = storageReference.child(PROFILE_IMAGES_PATH + "/" + userId + ".jpg");
        
        // Convert local file path to Uri
        Uri fileUri = Uri.parse("file://" + imagePath);
        
        imageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            
                            // Update user's profile image URL in database
                            databaseReference.child(USERS_PATH).child(userId).child("profileImageUrl")
                                    .setValue(imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update Firebase Auth profile
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();
                                        
                                        firebaseUser.updateProfile(profileUpdates)
                                                .addOnSuccessListener(v -> future.complete(imageUrl))
                                                .addOnFailureListener(future::completeExceptionally);
                                    })
                                    .addOnFailureListener(future::completeExceptionally);
                        })
                        .addOnFailureListener(future::completeExceptionally))
                .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
} 