package com.example.foodplanner.data.Auth.repository;

public interface AuthCallback {
    void onSuccess(String userId, String email, String displayName, String photoUrl);

    void onError(String errorMessage);
}
