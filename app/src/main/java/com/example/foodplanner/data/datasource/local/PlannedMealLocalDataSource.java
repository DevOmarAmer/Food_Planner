package com.example.foodplanner.data.datasource.local;

import android.content.Context;

import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.db.PlannedMealDao;
import com.example.foodplanner.data.model.PlannedMeal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class PlannedMealLocalDataSource {

    private static PlannedMealLocalDataSource instance;
    private final PlannedMealDao plannedMealDao;

    private PlannedMealLocalDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        plannedMealDao = database.plannedMealDao();
    }

    public static synchronized PlannedMealLocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new PlannedMealLocalDataSource(context);
        }
        return instance;
    }

    public Completable insertPlannedMeal(PlannedMeal plannedMeal) {
        return plannedMealDao.insertPlannedMeal(plannedMeal);
    }

    public Completable insertPlannedMeals(List<PlannedMeal> plannedMeals) {
        return plannedMealDao.insertPlannedMeals(plannedMeals);
    }

    public Completable deletePlannedMeal(PlannedMeal plannedMeal) {
        return plannedMealDao.deletePlannedMeal(plannedMeal);
    }

    public Completable deletePlannedMealById(String id) {
        return plannedMealDao.deletePlannedMealById(id);
    }

    public Completable deletePlannedMealByMealIdAndDay(String mealId, String day) {
        return plannedMealDao.deletePlannedMealByMealIdAndDay(mealId, day);
    }

    public Flowable<List<PlannedMeal>> getAllPlannedMeals() {
        return plannedMealDao.getAllPlannedMeals();
    }

    public Flowable<List<PlannedMeal>> getPlannedMealsByDay(String day) {
        return plannedMealDao.getPlannedMealsByDay(day);
    }

    public Single<Boolean> isMealPlannedForDay(String mealId, String day) {
        return plannedMealDao.isMealPlannedForDay(mealId, day);
    }

    public Completable deleteAllPlannedMeals() {
        return plannedMealDao.deleteAllPlannedMeals();
    }

    public Completable clearDayPlan(String day) {
        return plannedMealDao.clearDayPlan(day);
    }

    public Flowable<List<PlannedMeal>> getPlannedMealsByDateRange(long startDate, long endDate) {
        return plannedMealDao.getPlannedMealsByDateRange(startDate, endDate);
    }
}
