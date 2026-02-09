package com.example.foodplanner.presentation.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment implements ProfileView {

    private ImageView ivProfileAvatar;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvFavoritesCount;
    private TextView tvPlannedCount;
    private MaterialButton btnSignIn;
    private MaterialButton btnLogout;
    private LinearLayout layoutUploadToCloud;
    private LinearLayout layoutRestoreFromCloud;
    private ProgressBar progressSync;
    
    private ProfilePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        presenter = new ProfilePresenterImpl(this, requireContext());
        setupListeners();
        presenter.loadUserInfo();
        presenter.loadStatistics();
    }

    private void initViews(View view) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);
        tvPlannedCount = view.findViewById(R.id.tvPlannedCount);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnLogout = view.findViewById(R.id.btnLogout);
        layoutUploadToCloud = view.findViewById(R.id.layoutUploadToCloud);
        layoutRestoreFromCloud = view.findViewById(R.id.layoutRestoreFromCloud);
        progressSync = view.findViewById(R.id.progressSync);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> presenter.onLogoutClicked());
        
        btnSignIn.setOnClickListener(v -> navigateToAuth());
        
        layoutUploadToCloud.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle(R.string.backup_to_cloud)
                .setMessage(R.string.backup_confirmation)
                .setPositiveButton(R.string.backup, (dialog, which) -> presenter.syncToCloud())
                .setNegativeButton(R.string.cancel, null)
                .show();
        });
        
        layoutRestoreFromCloud.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle(R.string.restore_from_cloud)
                .setMessage(R.string.restore_confirmation)
                .setPositiveButton(R.string.restore, (dialog, which) -> presenter.syncFromCloud())
                .setNegativeButton(R.string.cancel, null)
                .show();
        });
    }

    @Override
    public void showUserInfo(String name, String email, boolean isGuest) {
        if (isGuest) {
            tvUserName.setText(R.string.guest_user);
            tvUserEmail.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            // Disable sync options for guests
            layoutUploadToCloud.setAlpha(0.5f);
            layoutRestoreFromCloud.setAlpha(0.5f);
        } else {
            tvUserName.setText(name != null ? name : getString(R.string.unknown_user));
            if (email != null) {
                tvUserEmail.setText(email);
                tvUserEmail.setVisibility(View.VISIBLE);
            } else {
                tvUserEmail.setVisibility(View.GONE);
            }
            btnSignIn.setVisibility(View.GONE);
            layoutUploadToCloud.setAlpha(1f);
            layoutRestoreFromCloud.setAlpha(1f);
        }
    }

    @Override
    public void showStatistics(int favoritesCount, int plannedMealsCount) {
        tvFavoritesCount.setText(String.valueOf(favoritesCount));
        tvPlannedCount.setText(String.valueOf(plannedMealsCount));
    }

    @Override
    public void showSyncSuccess(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showSyncError(String error) {
        if (getView() != null) {
            Snackbar.make(getView(), error, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error, null))
                .show();
        }
    }

    @Override
    public void showSyncProgress(boolean show) {
        progressSync.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutUploadToCloud.setEnabled(!show);
        layoutRestoreFromCloud.setEnabled(!show);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToAuth() {
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> presenter.confirmLogout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.dispose();
        }
    }
}
