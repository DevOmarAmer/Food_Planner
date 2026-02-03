package com.example.foodplanner.data.Auth.dataSource.local;

import com.example.foodplanner.utils.SharedPrefsHelper;

public class AuthLocalDataSourceImpl implements AuthLocalDataSource {
    
    private final SharedPrefsHelper sharedPrefsHelper;
    
    public AuthLocalDataSourceImpl(SharedPrefsHelper sharedPrefsHelper) {
        this.sharedPrefsHelper = sharedPrefsHelper;
    }
    
    @Override
    public void saveUserSession(String userId, String email, String displayName) {
        sharedPrefsHelper.saveUserLogin(userId, email, displayName);
    }
    
    @Override
    public void setGuestMode() {
        sharedPrefsHelper.setGuestMode();
    }
    
    @Override
    public boolean isGuestMode() {
        return sharedPrefsHelper.isGuest();
    }
    
    @Override
    public boolean hasActiveSession() {
        return sharedPrefsHelper.hasActiveSession();
    }
    
    @Override
    public String getUserId() {
        return sharedPrefsHelper.getUserId();
    }
    
    @Override
    public String getUserEmail() {
        return sharedPrefsHelper.getUserEmail();
    }
    
    @Override
    public String getUserName() {
        return sharedPrefsHelper.getUserName();
    }
    
    @Override
    public void clearUserData() {
        sharedPrefsHelper.logout();
    }
}
