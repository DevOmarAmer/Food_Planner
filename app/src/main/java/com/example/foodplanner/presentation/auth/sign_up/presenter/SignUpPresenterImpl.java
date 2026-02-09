package com.example.foodplanner.presentation.auth.sign_up.presenter;

import com.example.foodplanner.data.Auth.repository.AuthCallback;
import com.example.foodplanner.data.Auth.repository.AuthRepository;
import com.example.foodplanner.presentation.auth.sign_up.view.SignUpView;

public class SignUpPresenterImpl implements SignUpPresenter {

    private SignUpView view;
    private final AuthRepository authRepository;

    public SignUpPresenterImpl(SignUpView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    @Override
    public void signUp(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            view.showError("Please enter your name");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            view.showError("Please enter your email");
            return;
        }
        if (password == null || password.length() < 6) {
            view.showError("Password must be at least 6 characters");
            return;
        }

        view.showLoading();

        authRepository.signUp(name, email, password, new AuthCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName, String photoUrl) {
                if (view != null) {
                    view.hideLoading();
                    view.showSuccess("Account created successfully!");
                    view.onSignUpSuccess();

                }
            }

            @Override
            public void onError(String errorMessage) {
                if (view != null) {
                    view.hideLoading();
                    view.showError(errorMessage);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
