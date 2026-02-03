package com.example.foodplanner.presentation.auth.view;

public interface OnSignUpInteractionListener {
    void onSignUpClicked(String name, String email, String password);
    void onNavigateToLogin();
}
