package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Category - Data model for meal categories from TheMealDB API
 * 
 * API Endpoint: www.themealdb.com/api/json/v1/1/categories.php
 */
public class Category {
    
    @SerializedName("idCategory")
    private String id;
    
    @SerializedName("strCategory")
    private String name;
    
    @SerializedName("strCategoryThumb")
    private String thumbnail;
    
    @SerializedName("strCategoryDescription")
    private String description;
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
    public String getDescription() { return description; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public void setDescription(String description) { this.description = description; }
}
