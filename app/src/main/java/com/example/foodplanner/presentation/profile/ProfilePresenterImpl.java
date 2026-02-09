package com.example.foodplanner.presentation.profile;

import android.content.Context;

import com.example.foodplanner.data.repository.CloudSyncRepository;
import com.example.foodplanner.data.repository.MealRepository;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImpl implements ProfilePresenter {

    private final ProfileView view;
    private final SharedPrefsHelper sharedPrefsHelper;
    private final MealRepository mealRepository;
    private final CloudSyncRepository cloudSyncRepository;
    private final CompositeDisposable disposables;

    public ProfilePresenterImpl(ProfileView view, Context context) {
        this.view = view;
        this.sharedPrefsHelper = SharedPrefsHelper.getInstance(context);
        this.mealRepository = MealRepository.getInstance(context);
        this.cloudSyncRepository = CloudSyncRepository.getInstance(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadUserInfo() {
        String userName = sharedPrefsHelper.getUserName();
        String userEmail = sharedPrefsHelper.getUserEmail();
        boolean isGuest = sharedPrefsHelper.isGuest();

        view.showUserInfo(userName, userEmail, isGuest);
    }

    @Override
    public void loadStatistics() {
        // Load favorites count
        disposables.add(
                mealRepository.getFavorites()
                        .firstOrError()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                favorites -> {
                                    // Continue loading planned meals count
                                    loadPlannedMealsCount(favorites.size());
                                },
                                error -> {
                                    // Still try to load planned meals even if favorites fail
                                    loadPlannedMealsCount(0);
                                }));
    }

    private void loadPlannedMealsCount(int favoritesCount) {
        disposables.add(
                mealRepository.getAllPlannedMeals()
                        .firstOrError()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                plannedMeals -> view.showStatistics(favoritesCount, plannedMeals.size()),
                                error -> view.showStatistics(favoritesCount, 0)));
    }

    @Override
    public void syncToCloud() {
        // Check if user is logged in (not guest)
        if (sharedPrefsHelper.isGuest() || !sharedPrefsHelper.isLoggedIn()) {
            view.showError("Please sign in to sync your data to the cloud");
            return;
        }

        view.showSyncProgress(true);

        disposables.add(
                cloudSyncRepository.syncToCloud()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showSyncProgress(false);
                                    view.showSyncSuccess("Data uploaded to cloud successfully!");
                                },
                                error -> {
                                    view.showSyncProgress(false);
                                    view.showSyncError("Failed to sync: " + error.getMessage());
                                }));
    }

    @Override
    public void syncFromCloud() {
        // Check if user is logged in (not guest)
        if (sharedPrefsHelper.isGuest() || !sharedPrefsHelper.isLoggedIn()) {
            view.showError("Please sign in to restore your data from the cloud");
            return;
        }

        view.showSyncProgress(true);

        disposables.add(
                cloudSyncRepository.syncFromCloud()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showSyncProgress(false);
                                    view.showSyncSuccess("Data restored from cloud successfully!");
                                    // Reload statistics after sync
                                    loadStatistics();
                                },
                                error -> {
                                    view.showSyncProgress(false);
                                    view.showSyncError("Failed to restore: " + error.getMessage());
                                }));
    }

    @Override
    public void onLogoutClicked() {
        view.showLogoutConfirmation();
    }

    @Override
    public void confirmLogout() {
        FirebaseAuth.getInstance().signOut();
        sharedPrefsHelper.logout();
        view.navigateToAuth();
    }

    @Override
    public void updateUserName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            view.showError("Name cannot be empty");
            return;
        }
        sharedPrefsHelper.setUserName(newName.trim());
        view.showUserInfo(newName.trim(), sharedPrefsHelper.getUserEmail(), sharedPrefsHelper.isGuest());
        view.showProfileUpdateSuccess();
    }

    @Override
    public void sendPasswordResetEmail() {
        String email = sharedPrefsHelper.getUserEmail();
        if (email == null || email.isEmpty()) {
            view.showError("No email associated with this account");
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> view.showPasswordResetSent())
                .addOnFailureListener(e -> view.showError("Failed to send reset email: " + e.getMessage()));
    }

    @Override
    public void clearFavorites() {
        disposables.add(
                mealRepository.clearAllFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showDataCleared("Favorites cleared");
                                    loadStatistics();
                                },
                                error -> view.showError("Failed to clear favorites")));
    }

    @Override
    public void clearPlan() {
        disposables.add(
                mealRepository.clearAllPlannedMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showDataCleared("Meal plan cleared");
                                    loadStatistics();
                                },
                                error -> view.showError("Failed to clear meal plan")));
    }

    @Override
    public void clearAllData() {
        disposables.add(
                mealRepository.clearAllFavorites()
                        .andThen(mealRepository.clearAllPlannedMeals())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.showDataCleared("All data cleared");
                                    loadStatistics();
                                },
                                error -> view.showError("Failed to clear data")));
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
