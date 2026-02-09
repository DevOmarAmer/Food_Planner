package com.example.foodplanner.presentation.mealdetails;

import com.example.foodplanner.data.model.Meal;

public interface MealDetailsContract {

    interface View {
        void showLoading();

        void hideLoading();

        void showMeal(Meal meal);

        void showError(String message);

        void showAddedToFavorites();

        void showRemovedFromFavorites();

        void showFavoriteStatus(boolean isFavorite);

        void showAddedToPlan(String day);
    }

    interface Presenter {
        void getMealDetails(String mealId);

        void toggleFavorite(Meal meal);

        void addToPlan(Meal meal, String day);

        void addToPlanWithDate(Meal meal, String day, long plannedDate);

        void checkFavoriteStatus(String mealId);

        void onDestroy();
    }
}
