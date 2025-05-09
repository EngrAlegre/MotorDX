package com.motor.diagnostic.domain.model;

import java.util.Date;

/**
 * Domain model for Notification entity
 */
public class Notification {
    
    public enum NotificationType {
        SUCCESS,
        ERROR,
        WARNING,
        INFO
    }
    
    private String id;
    private String userId;
    private String motorcycleId;
    private NotificationType type;
    private String title;
    private String message;
    private Date timestamp;
    private boolean isRead;
    
    // Default constructor required for Firebase
    public Notification() {}
    
    public Notification(String id, String userId, String motorcycleId, NotificationType type, 
                        String title, String message, Date timestamp, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.motorcycleId = motorcycleId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMotorcycleId() {
        return motorcycleId;
    }

    public void setMotorcycleId(String motorcycleId) {
        this.motorcycleId = motorcycleId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
    
    /**
     * Map status from ESP32 to notification type
     */
    public static NotificationType mapStatusToNotificationType(String status) {
        if (status == null) {
            return NotificationType.INFO;
        }
        
        switch (status.toUpperCase()) {
            case "CRITICAL":
                return NotificationType.ERROR;
            case "WARNING":
                return NotificationType.WARNING;
            case "NORMAL":
            case "GOOD":
                return NotificationType.SUCCESS;
            default:
                return NotificationType.INFO;
        }
    }
} 