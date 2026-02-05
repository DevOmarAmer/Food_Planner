package com.example.foodplanner.data.repository;

import android.content.Context;

import com.example.foodplanner.data.datasource.local.MealLocalDataSource;
import com.example.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class MealRepository {
    
    private static MealRepository instance;
    private final MealRemoteDataSource remoteDataSource;
    private final MealLocalDataSource localDataSource;
    
    private MealRepository(Context context) {
        remoteDataSource = MealRemoteDataSource.getInstance();
        localDataSource = MealLocalDataSource.getInstance(context);
    }
    
    public static synchronized MealRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MealRepository(context);
        }
        return instance;
    }
    
    public Single<Meal> getRandomMeal() {
        return remoteDataSource.getRandomMeal();
    }
    
    public Single<List<Category>> getCategories() {
        return remoteDataSource.getCategories();
    }
    
    public Single<List<Area>> getAreas() {
        return remoteDataSource.getAreas();
    }
    
    public Single<List<Ingredient>> getIngredients() {
        return remoteDataSource.getIngredients();
    }
    
    public Single<Meal> getMealById(String mealId) {
        return remoteDataSource.getMealById(mealId);
    }
    
    public Single<List<Meal>> searchMealsByName(String name) {
        return remoteDataSource.searchMealsByName(name);
    }
    
    public Single<List<Meal>> filterByCategory(String category) {
        return remoteDataSource.filterByCategory(category);
    }
    
    public Single<List<Meal>> filterByArea(String area) {
        return remoteDataSource.filterByArea(area);
    }
    
    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return remoteDataSource.filterByIngredient(ingredient);
    }
    
    public Single<List<Meal>> searchByFirstLetter(String letter) {
        return remoteDataSource.searchByFirstLetter(letter);
    }
    
    public Completable addToFavorites(Meal meal) {
        return localDataSource.insertMeal(meal);
    }
    
    public Completable removeFromFavorites(Meal meal) {
        return localDataSource.deleteMeal(meal);
    }
    
    public Completable removeFromFavoritesById(String mealId) {
        return localDataSource.deleteMealById(mealId);
    }
    
    public Flowable<List<Meal>> getFavorites() {
        return localDataSource.getAllMeals();
    }
    
    public Single<Meal> getFavoriteMealById(String mealId) {
        return localDataSource.getMealById(mealId);
    }
    
    public Single<Boolean> isFavorite(String mealId) {
        return localDataSource.isMealFavorite(mealId);
    }
    
    public Completable clearAllFavorites() {
        return localDataSource.deleteAllMeals();
    }
    
    public Flowable<List<Meal>> searchFavoritesByName(String query) {
        return localDataSource.searchMealsByName(query);
    }
    
    public Flowable<List<Meal>> getFavoritesByCategory(String category) {
        return localDataSource.getMealsByCategory(category);
    }
    
    public Flowable<List<Meal>> getFavoritesByArea(String area) {
        return localDataSource.getMealsByArea(area);
    }
}
