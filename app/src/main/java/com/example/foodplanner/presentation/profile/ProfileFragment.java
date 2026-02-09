package com.example.foodplanner.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class ProfileFragment extends Fragment implements ProfileView {

    private ImageView ivProfileAvatar;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvFavoritesCount;
    private TextView tvPlannedCount;
    private TextView tvCurrentLanguage;
    private TextView tvAppVersion;
    private MaterialButton btnSignIn;
    private MaterialButton btnLogout;
    private SwitchMaterial switchDarkMode;

    // Account section
    private CardView cardAccount;
    private TextView tvAccountHeader;
    private LinearLayout layoutUpdateProfile;
    private LinearLayout layoutChangePassword;

    // App Settings
    private LinearLayout layoutLanguage;

    // Cloud Sync
    private LinearLayout layoutUploadToCloud;
    private LinearLayout layoutRestoreFromCloud;
    private ProgressBar progressSync;

    // Data Management
    private LinearLayout layoutClearFavorites;
    private LinearLayout layoutClearPlan;
    private LinearLayout layoutClearAllData;

    // About
    private LinearLayout layoutRateApp;
    private LinearLayout layoutShareApp;
    private LinearLayout layoutPrivacyPolicy;

    // Developer Credit
    private TextView tvDeveloperLink;

    private ProfilePresenter presenter;
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
        presenter = new ProfilePresenterImpl(this, requireContext());
        setupListeners();
        loadSettings();
        presenter.loadUserInfo();
        presenter.loadStatistics();
    }

    private void initViews(View view) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);
        tvPlannedCount = view.findViewById(R.id.tvPlannedCount);
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Account section
        cardAccount = view.findViewById(R.id.cardAccount);
        tvAccountHeader = view.findViewById(R.id.tvAccountHeader);
        layoutUpdateProfile = view.findViewById(R.id.layoutUpdateProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        // App Settings
        layoutLanguage = view.findViewById(R.id.layoutLanguage);

        // Cloud Sync
        layoutUploadToCloud = view.findViewById(R.id.layoutUploadToCloud);
        layoutRestoreFromCloud = view.findViewById(R.id.layoutRestoreFromCloud);
        progressSync = view.findViewById(R.id.progressSync);

        // Data Management
        layoutClearFavorites = view.findViewById(R.id.layoutClearFavorites);
        layoutClearPlan = view.findViewById(R.id.layoutClearPlan);
        layoutClearAllData = view.findViewById(R.id.layoutClearAllData);

        // About
        layoutRateApp = view.findViewById(R.id.layoutRateApp);
        layoutShareApp = view.findViewById(R.id.layoutShareApp);
        layoutPrivacyPolicy = view.findViewById(R.id.layoutPrivacyPolicy);

        // Developer Credit
        tvDeveloperLink = view.findViewById(R.id.tvDeveloperLink);

        // Set app version
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(),
                    0);
            tvAppVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText("1.0.0");
        }
    }

    private void loadSettings() {
        // Dark mode
        switchDarkMode.setChecked(sharedPrefsHelper.isDarkMode());

        // Language
        String currentLang = sharedPrefsHelper.getLanguage();
        if (SharedPrefsHelper.LANGUAGE_ARABIC.equals(currentLang)) {
            tvCurrentLanguage.setText(R.string.arabic);
        } else {
            tvCurrentLanguage.setText(R.string.english);
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> presenter.onLogoutClicked());
        btnSignIn.setOnClickListener(v -> navigateToAuth());

        // Account Section
        layoutUpdateProfile.setOnClickListener(v -> showUpdateProfileDialog());
        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Dark Mode Toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefsHelper.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Language
        layoutLanguage.setOnClickListener(v -> showLanguageDialog());

        // Cloud Sync
        layoutUploadToCloud.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.backup_to_cloud)
                    .setMessage(R.string.backup_confirmation)
                    .setPositiveButton(R.string.backup, (dialog, which) -> presenter.syncToCloud())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        layoutRestoreFromCloud.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.restore_from_cloud)
                    .setMessage(R.string.restore_confirmation)
                    .setPositiveButton(R.string.restore, (dialog, which) -> presenter.syncFromCloud())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // Data Management
        layoutClearFavorites.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.clear_favorites)
                    .setMessage(R.string.clear_favorites_confirmation)
                    .setPositiveButton(R.string.clear, (dialog, which) -> presenter.clearFavorites())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        layoutClearPlan.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.clear_plan)
                    .setMessage(R.string.clear_plan_confirmation)
                    .setPositiveButton(R.string.clear, (dialog, which) -> presenter.clearPlan())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        layoutClearAllData.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.clear_all_data)
                    .setMessage(R.string.clear_all_confirmation)
                    .setPositiveButton(R.string.clear, (dialog, which) -> presenter.clearAllData())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // About Section
        layoutRateApp.setOnClickListener(v -> openPlayStore());
        layoutShareApp.setOnClickListener(v -> shareApp());
        layoutPrivacyPolicy.setOnClickListener(v -> openPrivacyPolicy());

        // Developer Credit
        tvDeveloperLink.setOnClickListener(v -> openDeveloperPortfolio());
    }

    private void showUpdateProfileDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint(R.string.enter_new_name);
        input.setText(sharedPrefsHelper.getUserName());

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.update_profile)
                .setView(input)
                .setPositiveButton(R.string.update_profile, (dialog, which) -> {
                    String newName = input.getText().toString();
                    presenter.updateUserName(newName);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showChangePasswordDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.change_password)
                .setMessage("A password reset link will be sent to your email address.")
                .setPositiveButton("Send Reset Email", (dialog, which) -> presenter.sendPasswordResetEmail())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languages = { getString(R.string.english), getString(R.string.arabic) };
        String currentLang = sharedPrefsHelper.getLanguage();
        int checkedItem = SharedPrefsHelper.LANGUAGE_ARABIC.equals(currentLang) ? 1 : 0;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.language)
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    String selectedLang = (which == 0) ? SharedPrefsHelper.LANGUAGE_ENGLISH
                            : SharedPrefsHelper.LANGUAGE_ARABIC;
                    if (!selectedLang.equals(currentLang)) {
                        sharedPrefsHelper.setLanguage(selectedLang);
                        dialog.dismiss();
                        Toast.makeText(requireContext(), R.string.language_changed, Toast.LENGTH_SHORT).show();

                        // Apply locale change and restart
                        setLocale(selectedLang);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config,
                requireContext().getResources().getDisplayMetrics());

        // Restart the activity to apply changes
        requireActivity().recreate();
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + requireContext().getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://devomaramer.github.io/Food_Planner/PRIVACY_POLICY.html"
                            + requireContext().getPackageName())));
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)));
    }

    private void openPrivacyPolicy() {
        String url = getString(R.string.privacy_policy_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openDeveloperPortfolio() {
        String url = getString(R.string.developer_website);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void showUserInfo(String name, String email, String photoUrl, boolean isGuest) {
        if (isGuest) {
            tvUserName.setText(R.string.guest_user);
            tvUserEmail.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);

            // Hide account section for guests
            cardAccount.setVisibility(View.GONE);
            tvAccountHeader.setVisibility(View.GONE);

            // Disable sync options for guests
            layoutUploadToCloud.setAlpha(0.5f);
            layoutRestoreFromCloud.setAlpha(0.5f);
            layoutUploadToCloud.setEnabled(false);
            layoutRestoreFromCloud.setEnabled(false);

            // Disable data management options for guests
            layoutClearFavorites.setAlpha(0.5f);
            layoutClearPlan.setAlpha(0.5f);
            layoutClearAllData.setAlpha(0.5f);
            layoutClearFavorites.setEnabled(false);
            layoutClearPlan.setEnabled(false);
            layoutClearAllData.setEnabled(false);
        } else {
            tvUserName.setText(name != null ? name : getString(R.string.unknown_user));
            if (email != null) {
                tvUserEmail.setText(email);
                tvUserEmail.setVisibility(View.VISIBLE);
            } else {
                tvUserEmail.setVisibility(View.GONE);
            }
            // Load profile image if available
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivProfileAvatar);
            } else {
                ivProfileAvatar.setImageResource(R.drawable.ic_person);
            }

            btnSignIn.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            // Show account section
            cardAccount.setVisibility(View.VISIBLE);
            tvAccountHeader.setVisibility(View.VISIBLE);

            layoutUploadToCloud.setAlpha(1f);
            layoutRestoreFromCloud.setAlpha(1f);
            layoutUploadToCloud.setEnabled(true);
            layoutRestoreFromCloud.setEnabled(true);
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
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> presenter.confirmLogout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showProfileUpdateSuccess() {
        Toast.makeText(getContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPasswordResetSent() {
        Toast.makeText(getContext(), R.string.password_reset_email_sent, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDataCleared(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.dispose();
        }
    }
}
