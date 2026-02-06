package com.example.foodplanner.presentation.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileAvatar;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private MaterialButton btnLogout;
    private SharedPrefsHelper sharedPrefsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefsHelper = SharedPrefsHelper.getInstance(requireContext());
        initViews(view);
        setupUserInfo();
        setupListeners();
    }

    private void initViews(View view) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupUserInfo() {
        String userName = sharedPrefsHelper.getUserName();
        String userEmail = sharedPrefsHelper.getUserEmail();

        if (sharedPrefsHelper.isGuest()) {
            tvUserName.setText(R.string.guest_user);
            tvUserEmail.setVisibility(View.GONE);
        } else {
            tvUserName.setText(userName != null ? userName : getString(R.string.unknown_user));
            if (userEmail != null) {
                tvUserEmail.setText(userEmail);
                tvUserEmail.setVisibility(View.VISIBLE);
            } else {
                tvUserEmail.setVisibility(View.GONE);
            }
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> logout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPrefsHelper.logout();

        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
