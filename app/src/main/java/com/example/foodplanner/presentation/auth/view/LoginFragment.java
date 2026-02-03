package com.example.foodplanner.presentation.auth.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodplanner.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class LoginFragment extends Fragment {
    
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnGoogle, btnGuest;
    private TextView tvSignUp;
    private ProgressBar progressBar;
    
    private OnLoginInteractionListener listener;
    


    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginInteractionListener) {
            listener = (OnLoginInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnLoginInteractionListener");
        }
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
            
            if (isValid && listener != null) {
                listener.onLoginClicked(email, password);
            }
        });
        
        btnGoogle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoogleSignInClicked();
            }
        });
        
        btnGuest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGuestClicked();
            }
        });
        
        tvSignUp.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNavigateToSignUp();
            }
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
    
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
