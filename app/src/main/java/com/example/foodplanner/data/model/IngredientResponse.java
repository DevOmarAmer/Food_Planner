package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * IngredientResponse - Wrapper class for API responses containing ingredients
 */
public class IngredientResponse {
    
    @SerializedName("meals")
    private List<Ingredient> ingredients;
    
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
