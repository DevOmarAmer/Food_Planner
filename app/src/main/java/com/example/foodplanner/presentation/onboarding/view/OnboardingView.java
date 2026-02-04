package com.example.foodplanner.presentation.onboarding.view;

import java.util.List;
import com.example.foodplanner.presentation.onboarding.model.OnboardingItem;

public interface OnboardingView {
    void showOnboardingPages(List<OnboardingItem> items);
    void updateIndicators(int position, int totalCount);
    void updateButtons(int position, int totalCount);
    void navigateToNextPage();
    void navigateToPreviousPage();
    void navigateToAuth();
    void animateInitialEntry();
}
