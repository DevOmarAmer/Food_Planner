package com.example.foodplanner.presentation.auth.view;

public interface OnLoginInteractionListener {
    void onLoginClicked(String email, String password);
    void onGoogleSignInClicked();
    void onGuestClicked();
    void onNavigateToSignUp();
}
