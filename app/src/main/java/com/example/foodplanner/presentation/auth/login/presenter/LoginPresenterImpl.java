package com.example.foodplanner.presentation.auth.login.presenter;

import com.example.foodplanner.data.Auth.repository.AuthCallback;
import com.example.foodplanner.data.Auth.repository.AuthRepository;
import com.example.foodplanner.presentation.auth.login.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView view;
    private final AuthRepository authRepository;
    private final String webClientId;

    public LoginPresenterImpl(LoginView view, AuthRepository authRepository, String webClientId) {
        this.view = view;
        this.authRepository = authRepository;
        this.webClientId = webClientId;
    }
    
    @Override
    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            view.showError("Please enter your email");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            view.showError("Please enter your password");
            return;
        }
        
        view.showLoading();
        
        authRepository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName) {
                if (view != null) {
                    view.hideLoading();
                    view.showSuccess("Welcome back!");
                    view.onLoginSuccess();

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
    public void continueAsGuest() {
        authRepository.continueAsGuest();
        view.showSuccess("Continuing as guest");

    }
    
    @Override
    public void onGoogleSignInClicked() {
        view.showLoading();
        view.launchGoogleSignIn(authRepository.buildGoogleSignInRequest(webClientId));
    }
    
    @Override
    public void onGoogleIdTokenReceived(String idToken) {
        view.showLoading();
        
        authRepository.signInWithGoogle(idToken, new AuthCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName) {
                if (view != null) {
                    view.hideLoading();
                    view.showSuccess("Signed in with Google");
                    view.onLoginSuccess();
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
