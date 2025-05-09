package com.motor.diagnostic.domain.repository;

import com.motor.diagnostic.domain.model.Notification;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for Notification operations in the domain layer
 */
public interface NotificationRepository {
    
    /**
     * Get all notifications for the current user
     * @param limit Maximum number of notifications to retrieve
     * @return CompletableFuture with a List of Notifications
     */
    CompletableFuture<List<Notification>> getUserNotifications(int limit);
    
    /**
     * Mark a notification as read
     * @param notificationId ID of the notification
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> markNotificationAsRead(String notificationId);
    
    /**
     * Mark all notifications as read
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> markAllNotificationsAsRead();
    
    /**
     * Delete a notification
     * @param notificationId ID of the notification
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> deleteNotification(String notificationId);
    
    /**
     * Delete all notifications
     * @return CompletableFuture<Boolean> indicating success
     */
    CompletableFuture<Boolean> deleteAllNotifications();
    
    /**
     * Get unread notification count
     * @return CompletableFuture with the count of unread notifications
     */
    CompletableFuture<Integer> getUnreadNotificationCount();
    
    /**
     * Save a new notification
     * @param notification Notification to save
     * @return CompletableFuture with the saved Notification
     */
    CompletableFuture<Notification> saveNotification(Notification notification);
    
    /**
     * Subscribe to notification updates
     * @param listener Callback for notification updates
     */
    void subscribeToNotifications(NotificationListener listener);
    
    /**
     * Unsubscribe from notification updates
     */
    void unsubscribeFromNotifications();
    
    /**
     * Interface for notification listeners
     */
    interface NotificationListener {
        void onNewNotification(Notification notification);
    }
} 