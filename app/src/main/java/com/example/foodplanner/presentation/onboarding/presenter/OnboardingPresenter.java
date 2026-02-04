package com.example.foodplanner.presentation.onboarding.presenter;

public interface OnboardingPresenter {
    void loadOnboardingData();
    void onNextClicked(int currentPosition);
    void onPreviousClicked(int currentPosition);
    void onSkipClicked();
    void onPageSelected(int position);
    void onGetStartedClicked();
    int getItemCount();
    void onDestroy();
}
