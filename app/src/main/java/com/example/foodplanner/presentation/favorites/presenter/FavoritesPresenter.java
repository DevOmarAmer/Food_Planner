package com.example.foodplanner.presentation.favorites.presenter;

import com.example.foodplanner.data.model.Meal;

public interface FavoritesPresenter {
    void loadFavorites();
    void onMealClicked(Meal meal);
    void onRemoveFavorite(Meal meal);
    void onDestroy();
}
