package com.example.foodplanner.presentation.auth.login.presenter;


public interface LoginPresenter {
    void login(String email, String password);
    void continueAsGuest();
    void onDestroy();
    void onGoogleSignInClicked();
    void onGoogleIdTokenReceived(String idToken);

}


