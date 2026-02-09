package com.example.foodplanner.data.Auth.dataSource.local;

public interface AuthLocalDataSource {
    
    void saveUserSession(String userId, String email, String displayName, String imageUrl);
    
    void setGuestMode();
    
    boolean isGuestMode();
    
    boolean hasActiveSession();
    
    String getUserId();
    
    String getUserEmail();
    
    String getUserName();
    
    void clearUserData();
}
