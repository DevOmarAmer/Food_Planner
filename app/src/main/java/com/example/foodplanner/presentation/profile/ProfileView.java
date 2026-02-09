package com.example.foodplanner.presentation.profile;

public interface ProfileView {
    void showUserInfo(String name, String email, boolean isGuest);
    void showStatistics(int favoritesCount, int plannedMealsCount);
    void showSyncSuccess(String message);
    void showSyncError(String error);
    void showSyncProgress(boolean show);
    void showError(String message);
    void navigateToAuth();
    void showLogoutConfirmation();
}
