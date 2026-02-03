package com.example.foodplanner.presentation.auth.view;


import androidx.credentials.GetCredentialRequest;

/**
     * View interface - implemented by AuthActivity/Fragments
     * Handles UI updates and user feedback
     */
  public interface AuthView {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showSuccess(String message);
        void navigateToHome();
        void onLoginSuccess();
        void onSignUpSuccess();
        void onGoogleSignInSuccess();
        void launchGoogleSignIn(GetCredentialRequest request);
    }
    

