package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * CategoryResponse - Wrapper class for API responses containing categories
 */
public class CategoryResponse {
    
    @SerializedName("categories")
    private List<Category> categories;
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
