package com.motor.diagnostic.data.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.motor.diagnostic.domain.model.Motorcycle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase data model for Motorcycle entity
 */
@IgnoreExtraProperties
public class MotorcycleEntity {
    private String id;
    private String userId;
    private String nickname;
    private String make;
    private String model;
    private String imageUrl;
    private Long registrationDate; // Timestamp for Firebase
    private Boolean isConnected;
    private String deviceId;
    private Long createdAt;
    private Long updatedAt;
    
    // Default constructor required for Firebase
    public MotorcycleEntity() {}
    
    public MotorcycleEntity(String id, String userId, String nickname, String make, String model,
                           String imageUrl, Long registrationDate, Boolean isConnected, 
                           String deviceId, Long createdAt, Long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.make = make;
        this.model = model;
        this.imageUrl = imageUrl;
        this.registrationDate = registrationDate;
        this.isConnected = isConnected;
        this.deviceId = deviceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Convert domain model to data entity
    public static MotorcycleEntity fromDomain(Motorcycle motorcycle) {
        return new MotorcycleEntity(
                motorcycle.getId(),
                motorcycle.getUserId(),
                motorcycle.getNickname(),
                motorcycle.getMake(),
                motorcycle.getModel(),
                motorcycle.getImageUrl(),
                motorcycle.getRegistrationDate() != null ? motorcycle.getRegistrationDate().getTime() : null,
                motorcycle.isConnected(),
                motorcycle.getDeviceId(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }
    
    // Convert data entity to domain model
    public Motorcycle toDomain() {
        Date regDate = registrationDate != null ? new Date(registrationDate) : null;
        return new Motorcycle(
                id,
                userId,
                nickname,
                make,
                model,
                imageUrl,
                regDate,
                isConnected != null && isConnected,
                deviceId
        );
    }
    
    // Convert to Firebase Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userId", userId);
        result.put("nickname", nickname);
        result.put("make", make);
        result.put("model", model);
        result.put("imageUrl", imageUrl);
        result.put("registrationDate", registrationDate);
        result.put("isConnected", isConnected);
        result.put("deviceId", deviceId);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
} 