package com.example.foodplanner.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.PlannedMeal;

@Database(entities = {Meal.class, PlannedMeal.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "food_planner_db";
    private static volatile AppDatabase instance;
    
    public abstract MealDao mealDao();
    public abstract PlannedMealDao plannedMealDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
