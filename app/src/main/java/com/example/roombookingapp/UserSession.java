package com.example.roombookingapp;

import com.example.roombookingapp.data.model.LoggedInUser;

/**
 * Singleton class to manage the current logged-in user session.
 */
public class UserSession {

    // Single instance
    private static volatile UserSession instance;

    // Logged-in user
    private LoggedInUser currentUser;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Thread-safe singleton getter
    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    // Set the current user
    public void setCurrentUser(LoggedInUser user) {
        this.currentUser = user;
    }

    // Get the current user
    public LoggedInUser getCurrentUser() {
        return currentUser;
    }

    // Clear the session (logout)
    public void clear() {
        this.currentUser = null;
    }

    // Optional helper: check if a user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}