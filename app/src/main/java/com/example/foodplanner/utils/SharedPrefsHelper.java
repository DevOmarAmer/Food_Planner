package com.example.foodplanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {
    
    private static final String PREF_NAME = "FoodPlannerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_GUEST = "isGuest";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    private static SharedPrefsHelper instance;
    
    private SharedPrefsHelper(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsHelper(context);
        }
        return instance;
    }
    

    public void saveUserLogin(String userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_GUEST, false);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }
    

    public void setGuestMode() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putBoolean(KEY_IS_GUEST, true);
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_USER_EMAIL, null);
        editor.putString(KEY_USER_NAME, "Guest");
        editor.apply();
    }
    

    public void logout() {
        editor.clear();
        editor.apply();
    }
    

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public boolean isGuest() {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false);
    }
    
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }
    
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }
    

    public boolean hasActiveSession() {
        return isLoggedIn() || isGuest();
    }
}
