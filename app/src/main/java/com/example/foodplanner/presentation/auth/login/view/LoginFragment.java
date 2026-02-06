package com.example.foodplanner.presentation.auth.login.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.R;
import com.example.foodplanner.data.Auth.dataSource.local.AuthLocalDataSource;
import com.example.foodplanner.data.Auth.dataSource.local.AuthLocalDataSourceImpl;
import com.example.foodplanner.data.Auth.dataSource.remote.AuthRemoteDataSource;
import com.example.foodplanner.data.Auth.dataSource.remote.AuthRemoteDataSourceImpl;
import com.example.foodplanner.data.Auth.repository.AuthRepository;
import com.example.foodplanner.data.Auth.repository.AuthRepositoryImpl;
import com.example.foodplanner.presentation.auth.login.presenter.LoginPresenter;
import com.example.foodplanner.presentation.auth.login.presenter.LoginPresenterImpl;
import com.example.foodplanner.presentation.home.view.MainActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;


public class LoginFragment extends Fragment implements LoginView{
    
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnGoogle, btnGuest;
    private TextView tvSignUp;
    private ProgressBar progressBar;
    private CredentialManager credentialManager;
    private CancellationSignal cancellationSignal;
    private LoginPresenter presenter;
    private static final String WEB_CLIENT_ID = "342496974443-78ucj259vcqopas5hrnvg9k50ou46dt9.apps.googleusercontent.com";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        credentialManager = CredentialManager.create(requireActivity());
        cancellationSignal = new CancellationSignal();
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(requireContext());
        AuthLocalDataSource localDataSource = new AuthLocalDataSourceImpl(sharedPrefsHelper);
        AuthRemoteDataSource remoteDataSource = new AuthRemoteDataSourceImpl();
        AuthRepository authRepository = new AuthRepositoryImpl(remoteDataSource, localDataSource);
        presenter = new LoginPresenterImpl(this, authRepository, WEB_CLIENT_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogle = view.findViewById(R.id.btnGoogle);
        btnGuest = view.findViewById(R.id.btnGuest);
        tvSignUp = view.findViewById(R.id.tvSignUp);
        progressBar = view.findViewById(R.id.progressBar);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            

            tilEmail.setError(null);
            tilPassword.setError(null);
            

            boolean isValid = true;
            if (email.trim().isEmpty()) {
                tilEmail.setError("Email is required");
                isValid = false;
            }
            if (password.isEmpty()) {
                tilPassword.setError("Password is required");
                isValid = false;
            }
            
            if (isValid ) {
                presenter.login(email, password);
            }
        });
        
        btnGoogle.setOnClickListener(v -> {
            presenter.onGoogleSignInClicked();
        });
        
        btnGuest.setOnClickListener(v -> {
           presenter.continueAsGuest();
        });
        
        tvSignUp.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onNavigateToSignUp();
//            }
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_signUpFragment);
        });
    }
    
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnGuest.setEnabled(false);
    }
    
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogle.setEnabled(true);
        btnGuest.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    @Override
    public void onLoginSuccess() {
        navigateToHome();
    }



    @Override
    public void onGoogleSignInSuccess() {
        Log.d("TAG", "Google sign-in successful");
    }

    @Override
    public void launchGoogleSignIn(GetCredentialRequest request) {
        credentialManager.getCredentialAsync(
                requireActivity(),
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
                        requireActivity().runOnUiThread(() -> {
                            hideLoading();
                            showError(mapGoogleError(e));
                        });
                    }
                }
        );

    }
    private void handleGoogleResult(GetCredentialResponse response) {
        try {
            GoogleIdTokenCredential credential =
                    GoogleIdTokenCredential.createFrom(
                            response.getCredential().getData()
                    );

            // MVP: Pass the token to Presenter for Firebase authentication
            presenter.onGoogleIdTokenReceived(credential.getIdToken());

        } catch (Exception e) {
            Log.e("TAG", "Failed to parse Google credential", e);
            requireActivity().runOnUiThread(() -> {
                hideLoading();
                showError("Google Sign-In failed. Please try again.");
            });
        }
    }

    private String mapGoogleError(@NonNull GetCredentialException e) {
        Log.e("TAG", "Google Sign-In error", e);

        if (e instanceof NoCredentialException) {
            return "No Google account found. Please add a Google account to your device.";
        }

        @SuppressLint("RestrictedApi") String errorType = e.getType();
        if (errorType != null) {
            if (errorType.contains("USER_CANCELED")) {
                return "Sign-in cancelled";
            } else if (errorType.contains("INTERRUPTED")) {
                return "Sign-in was interrupted. Please try again.";
            }
        }

        return "Google Sign-In failed. Please try again or use email/password.";
    }


    public void navigateToHome() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        presenter.onDestroy();
    }
}
