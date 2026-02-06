package com.example.foodplanner.presentation.auth.sign_up.view;


public interface SignUpView {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showSuccess(String message);
        void onSignUpSuccess();

    }
    

