package com.example.foodplanner.presentation.profile;

public interface ProfilePresenter {
    void loadUserInfo();

    void loadStatistics();

    void syncToCloud();

    void syncFromCloud();

    void onLogoutClicked();

    void confirmLogout();

    void updateUserName(String newName);

    void sendPasswordResetEmail();

    void clearFavorites();

    void clearPlan();

    void clearAllData();

    void dispose();
}
