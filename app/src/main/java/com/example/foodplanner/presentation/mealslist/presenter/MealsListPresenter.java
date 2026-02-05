package com.example.foodplanner.presentation.mealslist.presenter;

import com.example.foodplanner.data.model.Meal;

public interface MealsListPresenter {
    void loadMealsByCategory(String category);
    void loadMealsByArea(String area);
    void loadMealsByIngredient(String ingredient);
    void searchMeals(String query);
    void onMealClicked(Meal meal);
    void onDestroy();
}
