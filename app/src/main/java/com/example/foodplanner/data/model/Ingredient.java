package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Ingredient - Data model for ingredients from TheMealDB API
 * 
 * API Endpoint: www.themealdb.com/api/json/v1/1/list.php?i=list
 */
public class Ingredient {
    
    @SerializedName("idIngredient")
    private String id;
    
    @SerializedName("strIngredient")
    private String name;
    
    @SerializedName("strDescription")
    private String description;
    
    @SerializedName("strType")
    private String type;
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    
    /**
     * Get ingredient thumbnail URL
     * TheMealDB provides ingredient images at: www.themealdb.com/images/ingredients/{name}.png
     */
    public String getThumbnail() {
        if (name == null) return null;
        return "https://www.themealdb.com/images/ingredients/" + name + ".png";
    }
    
    /**
     * Get small ingredient thumbnail
     */
    public String getThumbnailSmall() {
        if (name == null) return null;
        return "https://www.themealdb.com/images/ingredients/" + name + "-Small.png";
    }
}
