package com.motor.diagnostic.data.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.motor.diagnostic.domain.model.Notification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase data model for Notification entity
 */
@IgnoreExtraProperties
public class NotificationEntity {
    private String id;
    private String userId;
    private String motorcycleId;
    private String type; // "SUCCESS", "ERROR", "WARNING", "INFO"
    private String title;
    private String message;
    private Long timestamp;
    private Boolean isRead;
    private Long createdAt;
    
    // Default constructor required for Firebase
    public NotificationEntity() {}
    
    public NotificationEntity(String id, String userId, String motorcycleId, String type,
                             String title, String message, Long timestamp, Boolean isRead,
                             Long createdAt) {
        this.id = id;
        this.userId = userId;
        this.motorcycleId = motorcycleId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    // Convert domain model to data entity
    public static NotificationEntity fromDomain(Notification notification) {
        return new NotificationEntity(
                notification.getId(),
                notification.getUserId(),
                notification.getMotorcycleId(),
                notification.getType().name(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getTimestamp() != null ? notification.getTimestamp().getTime() : null,
                notification.isRead(),
                System.currentTimeMillis()
        );
    }
    
    // Convert data entity to domain model
    public Notification toDomain() {
        Date timestampDate = timestamp != null ? new Date(timestamp) : null;
        Notification.NotificationType notificationType;
        try {
            notificationType = Notification.NotificationType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            notificationType = Notification.NotificationType.INFO;
        }
        
        return new Notification(
                id,
                userId,
                motorcycleId,
                notificationType,
                title,
                message,
                timestampDate,
                isRead != null && isRead
        );
    }
    
    // Convert to Firebase Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userId", userId);
        result.put("motorcycleId", motorcycleId);
        result.put("type", type);
        result.put("title", title);
        result.put("message", message);
        result.put("timestamp", timestamp);
        result.put("isRead", isRead);
        result.put("createdAt", createdAt);
        return result;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
} 