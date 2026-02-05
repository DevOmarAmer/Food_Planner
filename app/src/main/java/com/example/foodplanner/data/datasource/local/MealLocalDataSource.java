package com.example.foodplanner.data.datasource.local;

import android.content.Context;

import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.db.MealDao;
import com.example.foodplanner.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class MealLocalDataSource {
    
    private static MealLocalDataSource instance;
    private final MealDao mealDao;
    
    private MealLocalDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        mealDao = database.mealDao();
    }
    
    public static synchronized MealLocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new MealLocalDataSource(context);
        }
        return instance;
    }
    
    public Completable insertMeal(Meal meal) {
        return mealDao.insertMeal(meal);
    }
    
    public Completable insertMeals(List<Meal> meals) {
        return mealDao.insertMeals(meals);
    }
    
    public Completable deleteMeal(Meal meal) {
        return mealDao.deleteMeal(meal);
    }
    
    public Completable deleteMealById(String mealId) {
        return mealDao.deleteMealById(mealId);
    }
    
    public Flowable<List<Meal>> getAllMeals() {
        return mealDao.getAllMeals();
    }
    
    public Single<Meal> getMealById(String mealId) {
        return mealDao.getMealById(mealId);
    }
    
    public Single<Boolean> isMealFavorite(String mealId) {
        return mealDao.isMealExists(mealId);
    }
    
    public Completable deleteAllMeals() {
        return mealDao.deleteAllMeals();
    }
    
    public Flowable<List<Meal>> searchMealsByName(String query) {
        return mealDao.searchMealsByName(query);
    }
    
    public Flowable<List<Meal>> getMealsByCategory(String category) {
        return mealDao.getMealsByCategory(category);
    }
    
    public Flowable<List<Meal>> getMealsByArea(String area) {
        return mealDao.getMealsByArea(area);
    }
}
