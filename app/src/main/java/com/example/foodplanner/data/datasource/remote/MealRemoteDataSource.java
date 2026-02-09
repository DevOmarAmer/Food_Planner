package com.example.foodplanner.data.datasource.remote;

import android.content.Context;

import com.example.foodplanner.data.exception.NoNetworkException;
import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.network.MealApiService;
import com.example.foodplanner.data.network.RetrofitClient;
import com.example.foodplanner.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MealRemoteDataSource {

    private static MealRemoteDataSource instance;
    private final MealApiService apiService;
    private NetworkUtils networkUtils;

    private MealRemoteDataSource() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static synchronized MealRemoteDataSource getInstance() {
        if (instance == null) {
            instance = new MealRemoteDataSource();
        }
        return instance;
    }

 
    public void init(Context context) {
        if (networkUtils == null) {
            networkUtils = NetworkUtils.getInstance(context);
        }
    }

  
    private <T> Single<T> executeWithNetworkCheck(Single<T> apiCall) {
        if (networkUtils != null && !networkUtils.isNetworkAvailable()) {
            return Single.error(new NoNetworkException("No internet connection. Please check your network settings."));
        }
        return apiCall;
    }

    public Single<Meal> getRandomMeal() {
        return executeWithNetworkCheck(
                apiService.getRandomMeal()
                        .map(response -> {
                            if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                                return response.getMeals().get(0);
                            }
                            throw new Exception("No meal found");
                        }));
    }

    public Single<List<Category>> getCategories() {
        return executeWithNetworkCheck(
                apiService.getCategories()
                        .map(response -> response.getCategories()));
    }

    public Single<List<Area>> getAreas() {
        return executeWithNetworkCheck(
                apiService.getAreas()
                        .map(response -> response.getAreas()));
    }

    public Single<List<Ingredient>> getIngredients() {
        return executeWithNetworkCheck(
                apiService.getIngredients()
                        .map(response -> response.getIngredients()));
    }

    public Single<Meal> getMealById(String mealId) {
        return executeWithNetworkCheck(
                apiService.getMealById(mealId)
                        .map(response -> {
                            if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                                return response.getMeals().get(0);
                            }
                            throw new Exception("Meal not found");
                        }));
    }

    public Single<List<Meal>> searchMealsByName(String name) {
        return executeWithNetworkCheck(
                apiService.searchMealsByName(name)
                        .map(response -> response.getMeals() != null ? response.getMeals() : new ArrayList<>()));
    }

    public Single<List<Meal>> filterByCategory(String category) {
        return executeWithNetworkCheck(
                apiService.filterByCategory(category)
                        .map(response -> response.getMeals() != null ? response.getMeals() : new ArrayList<>()));
    }

    public Single<List<Meal>> filterByArea(String area) {
        return executeWithNetworkCheck(
                apiService.filterByArea(area)
                        .map(response -> response.getMeals() != null ? response.getMeals() : new ArrayList<>()));
    }

    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return executeWithNetworkCheck(
                apiService.filterByIngredient(ingredient)
                        .map(response -> response.getMeals() != null ? response.getMeals() : new ArrayList<>()));
    }

    public Single<List<Meal>> searchByFirstLetter(String letter) {
        return executeWithNetworkCheck(
                apiService.searchByFirstLetter(letter)
                        .map(response -> response.getMeals() != null ? response.getMeals() : new ArrayList<>()));
    }
}
