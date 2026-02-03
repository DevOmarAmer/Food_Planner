package com.example.foodplanner.presentation.auth.presenter;

import android.content.Context;
import androidx.credentials.GetCredentialRequest;

import com.example.foodplanner.presentation.auth.view.AuthView;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public class AuthPresenterImpl implements AuthPresenter {
    
    private AuthView view;
    private final FirebaseAuth firebaseAuth;
    private final SharedPrefsHelper sharedPrefsHelper;
    private String  webClientId;
    public AuthPresenterImpl(AuthView view, Context context, String webClientId) {
        this.view = view;
        this.webClientId = webClientId;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.sharedPrefsHelper = SharedPrefsHelper.getInstance(context);
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
        
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    view.hideLoading();
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            
                            sharedPrefsHelper.saveUserLogin(
                                    user.getUid(),
                                    user.getEmail(),
                                    user.getDisplayName() != null ? user.getDisplayName() : "User"
                            );
                            view.showSuccess("Welcome back!");
                            view.onLoginSuccess();
                            view.navigateToHome();
                        }
                    } else {
                        String errorMessage = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Login failed";
                        view.showError(errorMessage);
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
        
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.trim())
                                    .build();
                            
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        view.hideLoading();
                                        
                                        sharedPrefsHelper.saveUserLogin(
                                                user.getUid(),
                                                user.getEmail(),
                                                name.trim()
                                        );
                                        view.showSuccess("Account created successfully!");
                                        view.onSignUpSuccess();
                                        view.navigateToHome();
                                    });
                        }
                    } else {
                        view.hideLoading();
                        String errorMessage = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Sign up failed";
                        view.showError(errorMessage);
                    }
                });
    }
    

    
    @Override
    public void continueAsGuest() {
        sharedPrefsHelper.setGuestMode();
        view.showSuccess("Continuing as guest");
        view.navigateToHome();
    }
    


    @Override
    public void onGoogleSignInClicked() {
        view.showLoading();

        GetSignInWithGoogleOption option =
                new GetSignInWithGoogleOption.Builder(webClientId)
                        .setNonce(generateNonce())
                        .build();

        GetCredentialRequest request =
                new GetCredentialRequest.Builder()
                        .addCredentialOption(option)
                        .build();

        view.launchGoogleSignIn(request);
    }


    @Override

    public void onGoogleIdTokenReceived(String idToken) {
        view.showLoading();

        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    view.hideLoading();

                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            sharedPrefsHelper.saveUserLogin(
                                    user.getUid(),
                                    user.getEmail(),
                                    user.getDisplayName() != null
                                            ? user.getDisplayName()
                                            : "User"
                            );
                            view.showSuccess("Signed in with Google");
                            view.navigateToHome();
                        }
                    } else {
                        view.showError(
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Google sign-in failed"
                        );
                    }
                });
    }


    private String generateNonce() {
        try {
            String rawNonce = UUID.randomUUID().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawNonce.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple UUID if SHA-256 not available
            return UUID.randomUUID().toString();
        }
    }


    @Override
    public void onDestroy() {
        view = null;
    }
}
