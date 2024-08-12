package com.example.doctari.models;

import com.google.firebase.Timestamp;

public class NotificationModel {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private long  timestamp;
    private boolean isRead;

    public NotificationModel() {
        // Default constructor required for Firebase
    }

    public NotificationModel(String notificationId, String userId, String title, String message, long  timestamp, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;

    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long  getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long  timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
