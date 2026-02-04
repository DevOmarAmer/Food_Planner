package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Meal - Data model for meal information from TheMealDB API
 * 
 * API Endpoint: www.themealdb.com/api/json/v1/1/lookup.php?i={id}
 */
public class Meal {
    
    @SerializedName("idMeal")
    private String id;
    
    @SerializedName("strMeal")
    private String name;
    
    @SerializedName("strCategory")
    private String category;
    
    @SerializedName("strArea")
    private String area;
    
    @SerializedName("strInstructions")
    private String instructions;
    
    @SerializedName("strMealThumb")
    private String thumbnail;
    
    @SerializedName("strTags")
    private String tags;
    
    @SerializedName("strYoutube")
    private String youtubeUrl;
    
    // Ingredients (TheMealDB returns them as separate fields)
    @SerializedName("strIngredient1")
    private String ingredient1;
    @SerializedName("strIngredient2")
    private String ingredient2;
    @SerializedName("strIngredient3")
    private String ingredient3;
    @SerializedName("strIngredient4")
    private String ingredient4;
    @SerializedName("strIngredient5")
    private String ingredient5;
    @SerializedName("strIngredient6")
    private String ingredient6;
    @SerializedName("strIngredient7")
    private String ingredient7;
    @SerializedName("strIngredient8")
    private String ingredient8;
    @SerializedName("strIngredient9")
    private String ingredient9;
    @SerializedName("strIngredient10")
    private String ingredient10;
    @SerializedName("strIngredient11")
    private String ingredient11;
    @SerializedName("strIngredient12")
    private String ingredient12;
    @SerializedName("strIngredient13")
    private String ingredient13;
    @SerializedName("strIngredient14")
    private String ingredient14;
    @SerializedName("strIngredient15")
    private String ingredient15;
    @SerializedName("strIngredient16")
    private String ingredient16;
    @SerializedName("strIngredient17")
    private String ingredient17;
    @SerializedName("strIngredient18")
    private String ingredient18;
    @SerializedName("strIngredient19")
    private String ingredient19;
    @SerializedName("strIngredient20")
    private String ingredient20;
    
    // Measures
    @SerializedName("strMeasure1")
    private String measure1;
    @SerializedName("strMeasure2")
    private String measure2;
    @SerializedName("strMeasure3")
    private String measure3;
    @SerializedName("strMeasure4")
    private String measure4;
    @SerializedName("strMeasure5")
    private String measure5;
    @SerializedName("strMeasure6")
    private String measure6;
    @SerializedName("strMeasure7")
    private String measure7;
    @SerializedName("strMeasure8")
    private String measure8;
    @SerializedName("strMeasure9")
    private String measure9;
    @SerializedName("strMeasure10")
    private String measure10;
    @SerializedName("strMeasure11")
    private String measure11;
    @SerializedName("strMeasure12")
    private String measure12;
    @SerializedName("strMeasure13")
    private String measure13;
    @SerializedName("strMeasure14")
    private String measure14;
    @SerializedName("strMeasure15")
    private String measure15;
    @SerializedName("strMeasure16")
    private String measure16;
    @SerializedName("strMeasure17")
    private String measure17;
    @SerializedName("strMeasure18")
    private String measure18;
    @SerializedName("strMeasure19")
    private String measure19;
    @SerializedName("strMeasure20")
    private String measure20;
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getArea() { return area; }
    public String getInstructions() { return instructions; }
    public String getThumbnail() { return thumbnail; }
    public String getTags() { return tags; }
    public String getYoutubeUrl() { return youtubeUrl; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setArea(String area) { this.area = area; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public void setTags(String tags) { this.tags = tags; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }
    
    /**
     * Get all ingredients as a list (filters out empty values)
     */
    public java.util.List<String> getIngredientsList() {
        java.util.List<String> ingredients = new java.util.ArrayList<>();
        addIfNotEmpty(ingredients, ingredient1);
        addIfNotEmpty(ingredients, ingredient2);
        addIfNotEmpty(ingredients, ingredient3);
        addIfNotEmpty(ingredients, ingredient4);
        addIfNotEmpty(ingredients, ingredient5);
        addIfNotEmpty(ingredients, ingredient6);
        addIfNotEmpty(ingredients, ingredient7);
        addIfNotEmpty(ingredients, ingredient8);
        addIfNotEmpty(ingredients, ingredient9);
        addIfNotEmpty(ingredients, ingredient10);
        addIfNotEmpty(ingredients, ingredient11);
        addIfNotEmpty(ingredients, ingredient12);
        addIfNotEmpty(ingredients, ingredient13);
        addIfNotEmpty(ingredients, ingredient14);
        addIfNotEmpty(ingredients, ingredient15);
        addIfNotEmpty(ingredients, ingredient16);
        addIfNotEmpty(ingredients, ingredient17);
        addIfNotEmpty(ingredients, ingredient18);
        addIfNotEmpty(ingredients, ingredient19);
        addIfNotEmpty(ingredients, ingredient20);
        return ingredients;
    }
    
    /**
     * Get all measures as a list
     */
    public java.util.List<String> getMeasuresList() {
        java.util.List<String> measures = new java.util.ArrayList<>();
        addIfNotEmpty(measures, measure1);
        addIfNotEmpty(measures, measure2);
        addIfNotEmpty(measures, measure3);
        addIfNotEmpty(measures, measure4);
        addIfNotEmpty(measures, measure5);
        addIfNotEmpty(measures, measure6);
        addIfNotEmpty(measures, measure7);
        addIfNotEmpty(measures, measure8);
        addIfNotEmpty(measures, measure9);
        addIfNotEmpty(measures, measure10);
        addIfNotEmpty(measures, measure11);
        addIfNotEmpty(measures, measure12);
        addIfNotEmpty(measures, measure13);
        addIfNotEmpty(measures, measure14);
        addIfNotEmpty(measures, measure15);
        addIfNotEmpty(measures, measure16);
        addIfNotEmpty(measures, measure17);
        addIfNotEmpty(measures, measure18);
        addIfNotEmpty(measures, measure19);
        addIfNotEmpty(measures, measure20);
        return measures;
    }
    
    private void addIfNotEmpty(java.util.List<String> list, String value) {
        if (value != null && !value.trim().isEmpty()) {
            list.add(value.trim());
        }
    }
    
    /**
     * Get YouTube video ID from URL
     */
    public String getYoutubeVideoId() {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            return null;
        }
        // YouTube URL format: https://www.youtube.com/watch?v=VIDEO_ID
        String[] parts = youtubeUrl.split("v=");
        if (parts.length > 1) {
            String videoId = parts[1];
            // Remove any additional parameters
            int ampIndex = videoId.indexOf('&');
            if (ampIndex != -1) {
                videoId = videoId.substring(0, ampIndex);
            }
            return videoId;
        }
        return null;
    }
}
