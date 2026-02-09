package com.example.foodplanner.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "planned_meals")
public class PlannedMeal {

    @PrimaryKey
    @NonNull
    private String id; // Composite key: mealId_plannedDate

    private String mealId;
    private String mealName;
    private String mealThumb;
    private String category;
    private String area;
    private String day; // Saturday, Sunday, Monday, etc.
    private long dateAdded;
    private long plannedDate; // The date the meal is planned for (midnight timestamp)

    public PlannedMeal() {
        this.id = "";
    }

    public PlannedMeal(Meal meal, String day) {
        this.id = meal.getId() + "_" + day;
        this.mealId = meal.getId();
        this.mealName = meal.getName();
        this.mealThumb = meal.getThumbnail();
        this.category = meal.getCategory();
        this.area = meal.getArea();
        this.day = day;
        this.dateAdded = System.currentTimeMillis();
        this.plannedDate = 0; // Legacy constructor for backwards compatibility
    }

    public PlannedMeal(Meal meal, String day, long plannedDate) {
        this.id = meal.getId() + "_" + plannedDate;
        this.mealId = meal.getId();
        this.mealName = meal.getName();
        this.mealThumb = meal.getThumbnail();
        this.category = meal.getCategory();
        this.area = meal.getArea();
        this.day = day;
        this.dateAdded = System.currentTimeMillis();
        this.plannedDate = plannedDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealThumb() {
        return mealThumb;
    }

    public void setMealThumb(String mealThumb) {
        this.mealThumb = mealThumb;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(long plannedDate) {
        this.plannedDate = plannedDate;
    }
}
