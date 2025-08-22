package com.qppd.plastech.Classes;

public class ActivityLog {
    private String id;
    private String userId;
    private String action;
    private String description;
    private String timestamp;
    private String status;

    public ActivityLog() {
        // Default constructor required for Firebase
    }

    public ActivityLog(String id, String userId, String action, String description, String timestamp, String status) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
