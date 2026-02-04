package com.example.foodplanner.presentation.onboarding.model;

public class OnboardingItem {
    
    private final String title;
    private final String description;
    private final int lottieRes;

    public OnboardingItem(String title, String description, int lottieRes) {
        this.title = title;
        this.description = description;
        this.lottieRes = lottieRes;
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
}
