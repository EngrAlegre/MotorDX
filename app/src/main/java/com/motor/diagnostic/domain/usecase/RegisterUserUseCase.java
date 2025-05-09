package com.motor.diagnostic.domain.usecase;

import com.motor.diagnostic.domain.model.User;
import com.motor.diagnostic.domain.repository.UserRepository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Use case for user registration
 */
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    
    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param fullName User's full name
     * @param nickName User's nickname
     * @param phoneNumber User's phone number
     * @param dateOfBirth User's date of birth
     * @param country User's country
     * @param sex User's sex
     * @param address User's address
     * @return CompletableFuture with the registered User
     */
    public CompletableFuture<User> execute(String email, String password, String fullName, 
                                           String nickName, String phoneNumber, Date dateOfBirth,
                                           String country, String sex, String address) {
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
        
        if (fullName == null || fullName.trim().isEmpty()) {
            CompletableFuture<User> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Full name cannot be empty"));
            return future;
        }
        
        // Create user object
        User user = new User();
        user.setFullName(fullName);
        user.setNickName(nickName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setDateOfBirth(dateOfBirth);
        user.setCountry(country);
        user.setSex(sex);
        user.setAddress(address);
        
        // Forward to repository
        return userRepository.registerUser(email, password, user);
    }
    
    /**
     * Simplified registration method with just essential fields
     * @param email User email
     * @param password User password
     * @param fullName User's full name
     * @return CompletableFuture with the registered User
     */
    public CompletableFuture<User> execute(String email, String password, String fullName) {
        return execute(email, password, fullName, null, null, null, null, null, null);
    }
} 