package com.example.foodplanner.presentation.search;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface SearchView {
    void showLoading();
    void hideLoading();
    void showCategories(List<Category> categories);
    void showAreas(List<Area> areas);
    void showIngredients(List<Ingredient> ingredients);
    void showSearchResults(List<Meal> meals);
    void showError(String message);
    void showEmpty();
    void navigateToMealsList(String filterType, String filterValue);
    void navigateToMealDetails(String mealId, String mealName);
}
