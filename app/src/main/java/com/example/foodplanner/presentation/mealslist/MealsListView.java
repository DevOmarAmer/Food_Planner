package com.example.foodplanner.presentation.mealslist;

import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface MealsListView {
    void showLoading();
    void hideLoading();
    void showMeals(List<Meal> meals);
    void showError(String message);
    void showEmpty();
    void navigateToMealDetails(String mealId, String mealName);
}
