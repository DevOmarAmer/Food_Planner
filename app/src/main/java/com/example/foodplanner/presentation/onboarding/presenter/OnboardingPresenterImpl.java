package com.example.foodplanner.presentation.onboarding.presenter;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.onboarding.model.OnboardingItem;
import com.example.foodplanner.presentation.onboarding.view.OnboardingView;
import com.example.foodplanner.utils.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.List;

public class OnboardingPresenterImpl implements OnboardingPresenter {

    private OnboardingView view;
    private final SharedPrefsHelper sharedPrefsHelper;
    private List<OnboardingItem> onboardingItems;

    public OnboardingPresenterImpl(OnboardingView view, SharedPrefsHelper sharedPrefsHelper) {
        this.view = view;
        this.sharedPrefsHelper = sharedPrefsHelper;
        this.onboardingItems = new ArrayList<>();
    }

    @Override
    public void loadOnboardingData() {
        onboardingItems.clear();
        
        onboardingItems.add(new OnboardingItem(
                "Discover Delicious Recipes",
                "Explore thousands of mouth-watering recipes from around the world. Find your next favorite dish with our curated collection.",
                R.raw.on_boarding_animation_1
        ));
        
        onboardingItems.add(new OnboardingItem(
                "Plan Your Weekly Meals",
                "Organize your meals for the entire week with our smart planner. Save time and eat healthier with structured meal planning.",
                R.raw.on_boarding_animation_2,
                0.9f
        ));
        
        onboardingItems.add(new OnboardingItem(
                "Save Your Favorites",
                "Bookmark recipes you love and access them anytime, even offline. Build your personal cookbook with a single tap.",
                R.raw.on_boarding_animation_3
        ));

        if (view != null) {
            view.showOnboardingPages(onboardingItems);
            view.animateInitialEntry();
        }
    }

    @Override
    public void onNextClicked(int currentPosition) {
        if (view == null) return;
        
        if (currentPosition < onboardingItems.size() - 1) {
            view.navigateToNextPage();
        } else {
            completeOnboarding();
        }
    }

    @Override
    public void onPreviousClicked(int currentPosition) {
        if (view != null && currentPosition > 0) {
            view.navigateToPreviousPage();
        }
    }

    @Override
    public void onSkipClicked() {
        completeOnboarding();
    }

    @Override
    public void onPageSelected(int position) {
        if (view != null) {
            view.updateIndicators(position, onboardingItems.size());
            view.updateButtons(position, onboardingItems.size());
        }
    }

    @Override
    public void onGetStartedClicked() {
        completeOnboarding();
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    private void completeOnboarding() {
        sharedPrefsHelper.setOnboardingCompleted(true);
        if (view != null) {
            view.navigateToAuth();
        }
    }
}
