package com.example.foodplanner.presentation.auth.sign_up.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.R;
import com.example.foodplanner.data.Auth.dataSource.local.AuthLocalDataSource;
import com.example.foodplanner.data.Auth.dataSource.local.AuthLocalDataSourceImpl;
import com.example.foodplanner.data.Auth.dataSource.remote.AuthRemoteDataSource;
import com.example.foodplanner.data.Auth.dataSource.remote.AuthRemoteDataSourceImpl;
import com.example.foodplanner.data.Auth.repository.AuthRepository;
import com.example.foodplanner.data.Auth.repository.AuthRepositoryImpl;
import com.example.foodplanner.presentation.auth.sign_up.presenter.SignUpPresenter;
import com.example.foodplanner.presentation.auth.sign_up.presenter.SignUpPresenterImpl;
import com.example.foodplanner.presentation.home.view.MainActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class SignUpFragment extends Fragment implements SignUpView{
    
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private SignUpPresenter presenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(requireContext());
        AuthLocalDataSource localDataSource = new AuthLocalDataSourceImpl(sharedPrefsHelper);
        AuthRemoteDataSource remoteDataSource = new AuthRemoteDataSourceImpl();
        AuthRepository authRepository = new AuthRepositoryImpl(remoteDataSource, localDataSource);
        presenter = new SignUpPresenterImpl(this, authRepository );
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
            
            if (isValid) {

                presenter.signUp(name, email, password);
            }
        });
        
        tvLogin.setOnClickListener(v -> {
          Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_loginFragment);
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
    public void showSuccess(String message) {
        navigateToHome();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }
    public void navigateToHome() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onSignUpSuccess() {

    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}
