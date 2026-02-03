package com.example.foodplanner.presentation.onboarding

import androidx.annotation.RawRes

/**
 * Data class representing a single onboarding page.
 * 
 * @param title The main title text displayed on the page
 * @param description The description text providing more details
 * @param lottieRes The Lottie animation resource ID (from raw folder)
 */
data class OnboardingItem(
    val title: String,
    val description: String,
    @RawRes val lottieRes: Int
)
