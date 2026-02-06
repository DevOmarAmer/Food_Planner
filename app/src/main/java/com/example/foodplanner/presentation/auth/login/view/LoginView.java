package com.example.foodplanner.presentation.auth.login.view;


import androidx.credentials.GetCredentialRequest;


public interface LoginView {
      void showLoading();
      void hideLoading();
      void showError(String message);
      void showSuccess(String message);

      void onLoginSuccess();
      void onGoogleSignInSuccess();
      void launchGoogleSignIn(GetCredentialRequest request);
  }
    

