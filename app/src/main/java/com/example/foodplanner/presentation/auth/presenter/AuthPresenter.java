package com.example.foodplanner.presentation.auth.presenter;


public interface AuthPresenter {
    void login(String email, String password);
    void signUp(String name, String email, String password);
//    void signInWithGoogle(String idToken);
    void continueAsGuest();
    void onDestroy();
    void onGoogleSignInClicked();
    void onGoogleIdTokenReceived(String idToken);

}


