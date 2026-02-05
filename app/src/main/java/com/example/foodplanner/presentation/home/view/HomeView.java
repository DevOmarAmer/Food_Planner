package com.example.foodplanner.presentation.home.view;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;

import java.util.List;


    
    public interface HomeView {
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
    



