package com.example.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.foodplanner.data.model.PlannedMeal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlannedMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlannedMeal(PlannedMeal plannedMeal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlannedMeals(List<PlannedMeal> plannedMeals);

    @Delete
    Completable deletePlannedMeal(PlannedMeal plannedMeal);

    @Query("DELETE FROM planned_meals WHERE id = :id")
    Completable deletePlannedMealById(String id);

    @Query("DELETE FROM planned_meals WHERE mealId = :mealId AND day = :day")
    Completable deletePlannedMealByMealIdAndDay(String mealId, String day);

    @Query("SELECT * FROM planned_meals ORDER BY day")
    Flowable<List<PlannedMeal>> getAllPlannedMeals();

    @Query("SELECT * FROM planned_meals WHERE day = :day")
    Flowable<List<PlannedMeal>> getPlannedMealsByDay(String day);

    @Query("SELECT EXISTS(SELECT 1 FROM planned_meals WHERE mealId = :mealId AND day = :day)")
    Single<Boolean> isMealPlannedForDay(String mealId, String day);

    @Query("SELECT * FROM planned_meals WHERE plannedDate >= :startDate AND plannedDate < :endDate ORDER BY plannedDate")
    Flowable<List<PlannedMeal>> getPlannedMealsByDateRange(long startDate, long endDate);

    @Query("DELETE FROM planned_meals")
    Completable deleteAllPlannedMeals();

    @Query("DELETE FROM planned_meals WHERE day = :day")
    Completable clearDayPlan(String day);
}
