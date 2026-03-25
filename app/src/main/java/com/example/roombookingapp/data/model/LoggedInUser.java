package com.example.roombookingapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String email;

    private String userId;
    private String displayName;
    private String role;

    public LoggedInUser(String userId, String displayName, String email, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.role = role != null ? role : "user";
    }
    public LoggedInUser(String userId, String displayName, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }
}