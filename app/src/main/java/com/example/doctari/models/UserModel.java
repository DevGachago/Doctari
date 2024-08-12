package com.example.doctari.models;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String role;
    private String profilePictureUrl;
    private int bluecoins;


    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId, String role, String profilePictureUrl) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.role = role;
        this.profilePictureUrl = profilePictureUrl;
        this.bluecoins = 3; // Initialize bluecoins to 3
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getBluecoins() {
        return bluecoins;
    }

    public void setBluecoins(int bluecoins) {
        this.bluecoins = bluecoins;
    }
}
