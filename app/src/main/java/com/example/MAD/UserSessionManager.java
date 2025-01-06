package com.example.MAD;

public class UserSessionManager {
    private static UserSessionManager instance;
    private String userEmail;

    private UserSessionManager() {
        // Private constructor to enforce singleton pattern
    }

    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void clearSession() {
        userEmail = null;
    }
}