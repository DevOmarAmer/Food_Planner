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


public class SignUpFragment extends Fragment {
    
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;
    
    private OnSignUpInteractionListener listener;
    


    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpInteractionListener) {
            listener = (OnSignUpInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnSignUpInteractionListener");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tilName = view.findViewById(R.id.tilName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        tvLogin = view.findViewById(R.id.tvLogin);
        progressBar = view.findViewById(R.id.progressBar);
    }
    
    private void setupClickListeners() {
        btnSignUp.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";
            

            tilName.setError(null);
            tilEmail.setError(null);
            tilPassword.setError(null);
            tilConfirmPassword.setError(null);


            boolean isValid = true;
            
            if (name.trim().isEmpty()) {
                tilName.setError("Name is required");
                isValid = false;
            }
            if (email.trim().isEmpty()) {
                tilEmail.setError("Email is required");
                isValid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Please enter a valid email");
                isValid = false;
            }
            if (password.length() < 6) {
                tilPassword.setError("Password must be at least 6 characters");
                isValid = false;
            }
            if (!password.equals(confirmPassword)) {
                tilConfirmPassword.setError("Passwords do not match");
                isValid = false;
            }
            
            if (isValid && listener != null) {
                listener.onSignUpClicked(name, email, password);
            }
        });
        
        tvLogin.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNavigateToLogin();
            }
        });
    }
    
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);
    }
    
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnSignUp.setEnabled(true);
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
