package com.example.foodplanner.presentation.search;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;

public interface SearchPresenter {
    void loadCategories();
    void loadAreas();
    void loadIngredients();
    void searchMeals(String query);
    void onCategoryClicked(Category category);
    void onAreaClicked(Area area);
    void onIngredientClicked(Ingredient ingredient);
    void onMealClicked(Meal meal);
    void onDestroy();
}
