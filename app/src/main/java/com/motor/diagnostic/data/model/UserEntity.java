package com.motor.diagnostic.data.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.motor.diagnostic.domain.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase data model for User entity
 */
@IgnoreExtraProperties
public class UserEntity {
    private String id;
    private String fullName;
    private String nickName;
    private String email;
    private String phoneNumber;
    private Long dateOfBirth; // Timestamp for Firebase
    private String country;
    private String sex;
    private String address;
    private String profileImageUrl;
    private Long createdAt;
    private Long updatedAt;
    
    // Default constructor required for Firebase
    public UserEntity() {}
    
    public UserEntity(String id, String fullName, String nickName, String email, String phoneNumber,
                     Long dateOfBirth, String country, String sex, String address, 
                     String profileImageUrl, Long createdAt, Long updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.nickName = nickName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.sex = sex;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Convert domain model to data entity
    public static UserEntity fromDomain(User user) {
        return new UserEntity(
                user.getId(),
                user.getFullName(),
                user.getNickName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth() != null ? user.getDateOfBirth().getTime() : null,
                user.getCountry(),
                user.getSex(),
                user.getAddress(),
                user.getProfileImageUrl(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }
    
    // Convert data entity to domain model
    public User toDomain() {
        Date dob = dateOfBirth != null ? new Date(dateOfBirth) : null;
        return new User(
                id,
                fullName,
                nickName,
                email,
                phoneNumber,
                dob,
                country,
                sex,
                address,
                profileImageUrl
        );
    }
    
    // Convert to Firebase Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("fullName", fullName);
        result.put("nickName", nickName);
        result.put("email", email);
        result.put("phoneNumber", phoneNumber);
        result.put("dateOfBirth", dateOfBirth);
        result.put("country", country);
        result.put("sex", sex);
        result.put("address", address);
        result.put("profileImageUrl", profileImageUrl);
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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