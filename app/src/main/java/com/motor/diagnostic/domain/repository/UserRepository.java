package com.motor.diagnostic.domain.repository;

import com.motor.diagnostic.domain.model.User;

import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for User entity operations in the domain layer
 */
public interface UserRepository {
    
    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param user User object with profile information
     * @return CompletableFuture with the registered User
     */
    CompletableFuture<User> registerUser(String email, String password, User user);
    
    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     * @return CompletableFuture with the logged-in User
     */
    CompletableFuture<User> loginUser(String email, String password);
    
    /**
     * Get current user information
     * @return CompletableFuture with the current User
     */
    CompletableFuture<User> getCurrentUser();
    
    /**
     * Update user profile
     * @param user Updated User object
     * @return CompletableFuture with the updated User
     */
    CompletableFuture<User> updateUser(User user);
    
    /**
     * Change user password
     * @param oldPassword Current password
     * @param newPassword New password
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> changePassword(String oldPassword, String newPassword);
    
    /**
     * Reset password
     * @param email User email
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> resetPassword(String email);
    
    /**
     * Sign out the current user
     */
    void signOut();
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    boolean isUserLoggedIn();
    
    /**
     * Update user profile image
     * @param imagePath Local path to the image file
     * @return CompletableFuture with the updated image URL
     */
    CompletableFuture<String> updateProfileImage(String imagePath);
} 