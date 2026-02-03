package com.example.foodplanner.data.Auth.dataSource.remote;

import androidx.credentials.GetCredentialRequest;

public interface AuthRemoteDataSource {
    
    interface AuthResultCallback {
        void onSuccess(String userId, String email, String displayName);
        void onError(String errorMessage);
    }
    
    void login(String email, String password, AuthResultCallback callback);
    
    void signUp(String name, String email, String password, AuthResultCallback callback);
    
    void signInWithGoogle(String idToken, AuthResultCallback callback);
    
    GetCredentialRequest buildGoogleSignInRequest(String webClientId);
    
    boolean isAuthenticated();
    
    void signOut();
    
    String getCurrentUserId();
    
    String getCurrentUserEmail();
    
    String getCurrentUserDisplayName();
}
