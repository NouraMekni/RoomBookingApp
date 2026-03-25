package com.example.roombookingapp.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 * Now includes role for admin vs user navigation.
 */
public class LoggedInUserView {
    private String displayName;
    private String role; // added role

    public LoggedInUserView(String displayName, String role) {
        this.displayName = displayName;
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }
}