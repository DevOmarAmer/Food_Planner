package com.example.foodplanner.presentation.onboarding.model;

public class OnboardingItem {
    
    private final String title;
    private final String description;
    private final int lottieRes;
    private final float maxProgress;

    public OnboardingItem(String title, String description, int lottieRes) {
        this(title, description, lottieRes, 1.0f);
    }

    public OnboardingItem(String title, String description, int lottieRes, float maxProgress) {
        this.title = title;
        this.description = description;
        this.lottieRes = lottieRes;
        this.maxProgress = maxProgress;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getLottieRes() {
        return lottieRes;
    }

    public float getMaxProgress() {
        return maxProgress;
    }
}
