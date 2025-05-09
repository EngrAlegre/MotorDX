package com.motor.diagnostic.domain.repository;

import com.motor.diagnostic.domain.model.Motorcycle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for Motorcycle entity operations in the domain layer
 */
public interface MotorcycleRepository {
    
    /**
     * Add a new motorcycle
     * @param motorcycle Motorcycle object to add
     * @return CompletableFuture with the added Motorcycle
     */
    CompletableFuture<Motorcycle> addMotorcycle(Motorcycle motorcycle);
    
    /**
     * Get a specific motorcycle by ID
     * @param motorcycleId ID of the motorcycle to retrieve
     * @return CompletableFuture with the Motorcycle
     */
    CompletableFuture<Motorcycle> getMotorcycle(String motorcycleId);
    
    /**
     * Get all motorcycles for the current user
     * @return CompletableFuture with a List of Motorcycles
     */
    CompletableFuture<List<Motorcycle>> getUserMotorcycles();
    
    /**
     * Update a motorcycle's information
     * @param motorcycle Updated Motorcycle object
     * @return CompletableFuture with the updated Motorcycle
     */
    CompletableFuture<Motorcycle> updateMotorcycle(Motorcycle motorcycle);
    
    /**
     * Delete a motorcycle
     * @param motorcycleId ID of the motorcycle to delete
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> deleteMotorcycle(String motorcycleId);
    
    /**
     * Connect to a motorcycle device
     * @param motorcycleId ID of the motorcycle to connect
     * @param deviceId ID of the ESP32 device
     * @return CompletableFuture<Boolean> indicating connection success
     */
    CompletableFuture<Boolean> connectMotorcycle(String motorcycleId, String deviceId);
    
    /**
     * Disconnect from a motorcycle device
     * @param motorcycleId ID of the motorcycle to disconnect
     * @return CompletableFuture<Boolean> indicating disconnection success
     */
    CompletableFuture<Boolean> disconnectMotorcycle(String motorcycleId);
    
    /**
     * Check if a motorcycle is currently connected
     * @param motorcycleId ID of the motorcycle to check
     * @return CompletableFuture<Boolean> indicating connection status
     */
    CompletableFuture<Boolean> isMotorcycleConnected(String motorcycleId);
    
    /**
     * Update motorcycle image
     * @param motorcycleId ID of the motorcycle
     * @param imagePath Local path to the image file
     * @return CompletableFuture with the updated image URL
     */
    CompletableFuture<String> updateMotorcycleImage(String motorcycleId, String imagePath);
} 