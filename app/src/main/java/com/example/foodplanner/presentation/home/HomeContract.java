package com.example.foodplanner.presentation.home;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface HomeContract {
    
    interface View {
        void showLoading();
        void hideLoading();
        void showRandomMeal(Meal meal);
        void showCategories(List<Category> categories);
        void showAreas(List<Area> areas);
        void showError(String message);
        void navigateToMealDetails(String mealId);
        void navigateToCategory(String categoryName);
        void navigateToArea(String areaName);
    }
    
    interface Presenter {
        void loadRandomMeal();
        void loadCategories();
        void loadAreas();
        void loadAllData();
        void onMealClicked(Meal meal);
        void onCategoryClicked(Category category);
        void onAreaClicked(Area area);
        void onDestroy();
    }
}
