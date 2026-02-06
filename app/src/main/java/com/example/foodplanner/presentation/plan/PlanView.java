package com.example.foodplanner.presentation.plan;

import com.example.foodplanner.data.model.PlannedMeal;

import java.util.List;

public interface PlanView {
    void showLoading();
    void hideLoading();
    void showPlan(List<PlannedMeal> plannedMeals);
    void showEmpty();
    void showError(String message);
    void navigateToMealDetails(String mealId, String mealName);
    void showMealRemoved(String mealName);
}
