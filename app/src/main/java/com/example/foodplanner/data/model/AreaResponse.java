package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * AreaResponse - Wrapper class for API responses containing areas/countries
 */
public class AreaResponse {
    
    @SerializedName("meals")
    private List<Area> areas;
    
    public List<Area> getAreas() {
        return areas;
    }
    
    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
