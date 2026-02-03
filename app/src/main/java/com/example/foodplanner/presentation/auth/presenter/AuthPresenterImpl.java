package com.example.foodplanner.presentation.auth.presenter;

import com.example.foodplanner.data.Auth.repository.AuthCallback;
import com.example.foodplanner.data.Auth.repository.AuthRepository;
import com.example.foodplanner.presentation.auth.view.AuthView;

public class AuthPresenterImpl implements AuthPresenter {
    
    private AuthView view;
    private final AuthRepository authRepository;
    private final String webClientId;
    
    public AuthPresenterImpl(AuthView view, AuthRepository authRepository, String webClientId) {
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
                    view.navigateToHome();
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
            public void onSuccess(String userId, String email, String displayName) {
                if (view != null) {
                    view.hideLoading();
                    view.showSuccess("Account created successfully!");
                    view.onSignUpSuccess();
                    view.navigateToHome();
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
        view.navigateToHome();
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
                    view.navigateToHome();
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
