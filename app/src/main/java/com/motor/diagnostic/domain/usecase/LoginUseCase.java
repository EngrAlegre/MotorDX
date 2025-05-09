package com.motor.diagnostic.domain.usecase;

import com.motor.diagnostic.domain.model.User;
import com.motor.diagnostic.domain.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

/**
 * Use case for user login
 */
public class LoginUseCase {
    private final UserRepository userRepository;
    
    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     * @return CompletableFuture with the logged-in User
     */
    public CompletableFuture<User> execute(String email, String password) {
        // Input validation
        if (email == null || email.trim().isEmpty()) {
            CompletableFuture<User> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Email cannot be empty"));
            return future;
        }
        
        if (password == null || password.trim().isEmpty()) {
            CompletableFuture<User> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Password cannot be empty"));
            return future;
        }
        
        // Forward to repository
        return userRepository.loginUser(email, password);
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }
} 