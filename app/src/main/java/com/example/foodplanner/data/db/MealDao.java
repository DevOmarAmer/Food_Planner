package com.example.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.foodplanner.data.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MealDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(Meal meal);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeals(List<Meal> meals);
    
    @Delete
    Completable deleteMeal(Meal meal);
    
    @Query("SELECT * FROM meals")
    Flowable<List<Meal>> getAllMeals();
    
    @Query("SELECT * FROM meals WHERE id = :mealId")
    Single<Meal> getMealById(String mealId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM meals WHERE id = :mealId)")
    Single<Boolean> isMealExists(String mealId);
    
    @Query("DELETE FROM meals WHERE id = :mealId")
    Completable deleteMealById(String mealId);
    
    @Query("DELETE FROM meals")
    Completable deleteAllMeals();
    
    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%'")
    Flowable<List<Meal>> searchMealsByName(String query);
    
    @Query("SELECT * FROM meals WHERE category = :category")
    Flowable<List<Meal>> getMealsByCategory(String category);
    
    @Query("SELECT * FROM meals WHERE area = :area")
    Flowable<List<Meal>> getMealsByArea(String area);
}
