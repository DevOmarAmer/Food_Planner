package com.example.foodplanner.presentation.favorites.view;

import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface FavoritesView {
    void showLoading();
    void hideLoading();
    void showFavorites(List<Meal> meals);
    void showEmpty();
    void showError(String message);
    void navigateToMealDetails(String mealId, String mealName);
    void showMealRemoved(String mealName);
}
