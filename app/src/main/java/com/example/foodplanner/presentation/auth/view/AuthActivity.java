package com.example.foodplanner.presentation.auth.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.presenter.AuthPresenter;
import com.example.foodplanner.presentation.auth.presenter.AuthPresenterImpl;
import com.example.foodplanner.presentation.home.MainActivity;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executors;

public class AuthActivity extends AppCompatActivity 
        implements AuthView,
        OnLoginInteractionListener,
        OnSignUpInteractionListener {

    private static final String TAG = "AuthActivity";
    private static final String WEB_CLIENT_ID = "342496974443-78ucj259vcqopas5hrnvg9k50ou46dt9.apps.googleusercontent.com";
    
    private AuthPresenter presenter;
    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private CredentialManager credentialManager;
    private CancellationSignal cancellationSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        

        presenter = new AuthPresenterImpl(this, this, WEB_CLIENT_ID);
        
        credentialManager = CredentialManager.create(this);
        cancellationSignal = new CancellationSignal();

        if (savedInstanceState == null) {
            showLoginFragment();
        }
    }
    
    // frag nav ======================================================
    private void showLoginFragment() {
        loginFragment = new LoginFragment();
        replaceFragment(loginFragment, false);
    }
    
    private void showSignUpFragment() {
        signUpFragment = new SignUpFragment();
        replaceFragment(signUpFragment, true);
    }
    
    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragmentContainer, fragment);
        
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        
        transaction.commit();
    }
    
    // Login frag Callbackes ===============================================
    
    @Override
    public void onLoginClicked(String email, String password) {
        presenter.login(email, password);
    }
    
    @Override
    public void onGoogleSignInClicked() {
        presenter.onGoogleSignInClicked();
    }
    
    @Override
    public void onGuestClicked() {
        presenter.continueAsGuest();
    }
    
    @Override
    public void onNavigateToSignUp() {
        showSignUpFragment();
    }
    
    // signup frag callbacks ===============================================
    
    @Override
    public void onSignUpClicked(String name, String email, String password) {
        presenter.signUp(name, email, password);
    }
    
    @Override
    public void onNavigateToLogin() {
        getSupportFragmentManager().popBackStack();
    }
    

    @Override
    public void showLoading() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof LoginFragment) {
            ((LoginFragment) currentFragment).showLoading();
        } else if (currentFragment instanceof SignUpFragment) {
            ((SignUpFragment) currentFragment).showLoading();
        }
    }
    
    @Override
    public void hideLoading() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof LoginFragment) {
            ((LoginFragment) currentFragment).hideLoading();
        } else if (currentFragment instanceof SignUpFragment) {
            ((SignUpFragment) currentFragment).hideLoading();
        }
    }
    
    @Override
    public void showError(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
    }
    
    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "Login successful");
    }
    
    @Override
    public void onSignUpSuccess() {
        Log.d(TAG, "Sign up successful");
    }
    
    @Override
    public void onGoogleSignInSuccess() {
        Log.d(TAG, "Google sign-in successful");
    }

    @Override
    public void launchGoogleSignIn(GetCredentialRequest request) {
        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleResult(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        runOnUiThread(() -> {
                            hideLoading();
                            showError(mapGoogleError(e));
                        });
                    }
                }
        );

    }
    /**
     * MVP: View handles the credential response from Google Sign-In
     * but delegates the actual authentication to the Presenter
     */
    private void handleGoogleResult(GetCredentialResponse response) {
        try {
            GoogleIdTokenCredential credential =
                    GoogleIdTokenCredential.createFrom(
                            response.getCredential().getData()
                    );
            
            // MVP: Pass the token to Presenter for Firebase authentication
            presenter.onGoogleIdTokenReceived(credential.getIdToken());

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse Google credential", e);
            hideLoading();
            showError("Google Sign-In failed. Please try again.");
        }
    }
    
    /**
     * MVP: Helper method to map Google credential errors to user-friendly messages
     * This is View-layer responsibility as it's about displaying messages
     */
    private String mapGoogleError(GetCredentialException e) {
        Log.e(TAG, "Google Sign-In error", e);
        
        if (e instanceof NoCredentialException) {
            return "No Google account found. Please add a Google account to your device.";
        }
        
        String errorType = e.getType();
        if (errorType != null) {
            if (errorType.contains("USER_CANCELED")) {
                return "Sign-in cancelled";
            } else if (errorType.contains("INTERRUPTED")) {
                return "Sign-in was interrupted. Please try again.";
            }
        }
        
        return "Google Sign-In failed. Please try again or use email/password.";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MVP: Clean up presenter reference to avoid memory leaks
        if (presenter != null) {
            presenter.onDestroy();
        }
        // Cancel any pending Google Sign-In operations
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }
}
