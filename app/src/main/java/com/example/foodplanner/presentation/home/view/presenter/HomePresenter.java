package com.example.foodplanner.presentation.home.view.presenter;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;

interface HomePresenter {
    void loadRandomMeal();
    void loadCategories();
    void loadAreas();
    void loadAllData();
    void onMealClicked(Meal meal);
    void onCategoryClicked(Category category);
    void onAreaClicked(Area area);
    void onDestroy();
}
