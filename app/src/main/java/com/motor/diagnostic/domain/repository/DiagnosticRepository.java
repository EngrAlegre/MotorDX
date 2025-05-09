package com.motor.diagnostic.domain.repository;

import com.motor.diagnostic.domain.model.DiagnosticData;
import com.motor.diagnostic.domain.model.Notification;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for Diagnostic operations in the domain layer
 */
public interface DiagnosticRepository {
    
    /**
     * Get current diagnostic data for a motorcycle
     * @param motorcycleId ID of the motorcycle
     * @return CompletableFuture with the DiagnosticData
     */
    CompletableFuture<DiagnosticData> getCurrentDiagnosticData(String motorcycleId);
    
    /**
     * Get historical diagnostic data for a motorcycle within a date range
     * @param motorcycleId ID of the motorcycle
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return CompletableFuture with a List of DiagnosticData
     */
    CompletableFuture<List<DiagnosticData>> getHistoricalDiagnosticData(
            String motorcycleId, Date startDate, Date endDate);
    
    /**
     * Start diagnostic session for a motorcycle
     * @param motorcycleId ID of the motorcycle
     * @return CompletableFuture<Boolean> indicating session start success
     */
    CompletableFuture<Boolean> startDiagnosticSession(String motorcycleId);
    
    /**
     * End diagnostic session for a motorcycle
     * @param motorcycleId ID of the motorcycle
     * @return CompletableFuture<Boolean> indicating session end success
     */
    CompletableFuture<Boolean> endDiagnosticSession(String motorcycleId);
    
    /**
     * Save diagnostic data
     * @param diagnosticData DiagnosticData to save
     * @return CompletableFuture<Boolean> indicating save success
     */
    CompletableFuture<Boolean> saveDiagnosticData(DiagnosticData diagnosticData);
    
    /**
     * Get notifications related to diagnostic data
     * @param motorcycleId ID of the motorcycle
     * @param limit Maximum number of notifications to retrieve
     * @return CompletableFuture with a List of Notifications
     */
    CompletableFuture<List<Notification>> getDiagnosticNotifications(String motorcycleId, int limit);
    
    /**
     * Subscribe to real-time diagnostic updates
     * @param motorcycleId ID of the motorcycle
     * @param listener Callback for diagnostic updates
     */
    void subscribeToDiagnosticUpdates(String motorcycleId, DiagnosticUpdateListener listener);
    
    /**
     * Unsubscribe from real-time diagnostic updates
     * @param motorcycleId ID of the motorcycle
     */
    void unsubscribeFromDiagnosticUpdates(String motorcycleId);
    
    /**
     * Start monitoring for alerts
     * @param motorcycleId ID of the motorcycle
     * @param listener Callback for alert notifications
     */
    void startAlertMonitoring(String motorcycleId, AlertListener listener);
    
    /**
     * Stop monitoring for alerts
     * @param motorcycleId ID of the motorcycle
     */
    void stopAlertMonitoring(String motorcycleId);
    
    /**
     * Interface for diagnostic update listeners
     */
    interface DiagnosticUpdateListener {
        void onDiagnosticUpdate(DiagnosticData diagnosticData);
        void onDiagnosticError(Exception e);
    }
    
    /**
     * Interface for alert listeners
     */
    interface AlertListener {
        void onAlert(Notification alert);
    }
} 