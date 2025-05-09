package com.motor.diagnostic.domain.usecase;

import com.motor.diagnostic.domain.model.Motorcycle;
import com.motor.diagnostic.domain.model.Notification;
import com.motor.diagnostic.domain.repository.DiagnosticRepository;
import com.motor.diagnostic.domain.repository.MotorcycleRepository;
import com.motor.diagnostic.domain.repository.NotificationRepository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Use case for connecting to a motorcycle
 */
public class ConnectMotorcycleUseCase {
    private final MotorcycleRepository motorcycleRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final NotificationRepository notificationRepository;
    
    public ConnectMotorcycleUseCase(
            MotorcycleRepository motorcycleRepository,
            DiagnosticRepository diagnosticRepository,
            NotificationRepository notificationRepository) {
        this.motorcycleRepository = motorcycleRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.notificationRepository = notificationRepository;
    }
    
    /**
     * Connect to a motorcycle
     * @param motorcycleId ID of the motorcycle to connect
     * @param deviceId ID of the device to connect to (optional, defaults to ESP32 device)
     * @return CompletableFuture<Boolean> indicating connection success
     */
    public CompletableFuture<Boolean> execute(String motorcycleId, String deviceId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        
        // Input validation
        if (motorcycleId == null || motorcycleId.trim().isEmpty()) {
            result.completeExceptionally(new IllegalArgumentException("Motorcycle ID cannot be empty"));
            return result;
        }
        
        // First get the motorcycle to verify it exists
        motorcycleRepository.getMotorcycle(motorcycleId)
                .thenCompose(motorcycle -> {
                    // Start a diagnostic session
                    return diagnosticRepository.startDiagnosticSession(motorcycleId)
                            .thenCompose(sessionStarted -> {
                                if (sessionStarted) {
                                    // Connect to the motorcycle
                                    return motorcycleRepository.connectMotorcycle(motorcycleId, deviceId);
                                } else {
                                    CompletableFuture<Boolean> future = new CompletableFuture<>();
                                    future.completeExceptionally(new Exception("Failed to start diagnostic session"));
                                    return future;
                                }
                            })
                            .thenCompose(connected -> {
                                if (connected) {
                                    // Start alert monitoring
                                    diagnosticRepository.startAlertMonitoring(motorcycleId, alert -> {
                                        // Save the alert as a notification
                                        notificationRepository.saveNotification(alert);
                                    });
                                    
                                    // Subscribe to diagnostic updates
                                    diagnosticRepository.subscribeToDiagnosticUpdates(motorcycleId, new DiagnosticRepository.DiagnosticUpdateListener() {
                                        @Override
                                        public void onDiagnosticUpdate(com.motor.diagnostic.domain.model.DiagnosticData diagnosticData) {
                                            // Handle diagnostic updates if needed
                                        }
                                        
                                        @Override
                                        public void onDiagnosticError(Exception e) {
                                            // Create an error notification
                                            Notification errorNotification = new Notification();
                                            errorNotification.setType(Notification.NotificationType.ERROR);
                                            errorNotification.setTitle("Diagnostic Error");
                                            errorNotification.setMessage("Failed to get diagnostic data: " + e.getMessage());
                                            errorNotification.setTimestamp(new Date());
                                            errorNotification.setMotorcycleId(motorcycleId);
                                            
                                            notificationRepository.saveNotification(errorNotification);
                                        }
                                    });
                                    
                                    // Create a success notification
                                    Notification successNotification = new Notification();
                                    successNotification.setType(Notification.NotificationType.SUCCESS);
                                    successNotification.setTitle("Connection Successful");
                                    successNotification.setMessage("Connected successfully to your motorcycle.");
                                    successNotification.setTimestamp(new Date());
                                    successNotification.setMotorcycleId(motorcycleId);
                                    
                                    return notificationRepository.saveNotification(successNotification)
                                            .thenApply(notification -> true);
                                } else {
                                    CompletableFuture<Boolean> future = new CompletableFuture<>();
                                    future.completeExceptionally(new Exception("Failed to connect to motorcycle"));
                                    return future;
                                }
                            });
                })
                .thenAccept(result::complete)
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
        
        return result;
    }
    
    /**
     * Connect to a motorcycle with default device ID
     * @param motorcycleId ID of the motorcycle to connect
     * @return CompletableFuture<Boolean> indicating connection success
     */
    public CompletableFuture<Boolean> execute(String motorcycleId) {
        return execute(motorcycleId, null);
    }
    
    /**
     * Disconnect from a motorcycle
     * @param motorcycleId ID of the motorcycle to disconnect
     * @return CompletableFuture<Boolean> indicating disconnection success
     */
    public CompletableFuture<Boolean> disconnect(String motorcycleId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        
        // Input validation
        if (motorcycleId == null || motorcycleId.trim().isEmpty()) {
            result.completeExceptionally(new IllegalArgumentException("Motorcycle ID cannot be empty"));
            return result;
        }
        
        // Unsubscribe from diagnostic updates and alerts
        diagnosticRepository.unsubscribeFromDiagnosticUpdates(motorcycleId);
        diagnosticRepository.stopAlertMonitoring(motorcycleId);
        
        // End the diagnostic session
        diagnosticRepository.endDiagnosticSession(motorcycleId)
                .thenCompose(sessionEnded -> motorcycleRepository.disconnectMotorcycle(motorcycleId))
                .thenAccept(result::complete)
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
        
        return result;
    }
} 