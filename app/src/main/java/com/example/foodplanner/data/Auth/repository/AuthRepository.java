package com.example.foodplanner.data.Auth.repository;

import androidx.credentials.GetCredentialRequest;

public interface AuthRepository {
    
    void login(String email, String password, AuthCallback callback);
    
    void signUp(String name, String email, String password, AuthCallback callback);
    
    void signInWithGoogle(String idToken, AuthCallback callback);
    
    void continueAsGuest();
    
    GetCredentialRequest buildGoogleSignInRequest(String webClientId);
    
    void saveUserSession(String userId, String email, String displayName, String imageUrl);
    
    boolean isUserLoggedIn();
    
    boolean isGuestMode();
    
    void logout();
}
