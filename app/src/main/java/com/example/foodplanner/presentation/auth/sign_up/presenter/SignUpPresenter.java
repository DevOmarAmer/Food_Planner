package com.example.foodplanner.presentation.auth.sign_up.presenter;


public interface SignUpPresenter {
    void signUp(String name, String email, String password);
    void onDestroy();
}


