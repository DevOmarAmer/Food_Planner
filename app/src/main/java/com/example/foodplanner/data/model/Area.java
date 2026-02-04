package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Area - Data model for countries/areas from TheMealDB API
 * 
 * API Endpoint: www.themealdb.com/api/json/v1/1/list.php?a=list
 */
public class Area {
    
    @SerializedName("strArea")
    private String name;
    
    // Constructor
    public Area() {}
    
    public Area(String name) {
        this.name = name;
    }
    
    // Getters
    public String getName() { return name; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    
    /**
     * Get flag image URL based on country name
     * Uses flagcdn.com for flag images
     */
    public String getFlagUrl() {
        String countryCode = getCountryCode();
        if (countryCode != null) {
            return "https://flagcdn.com/w160/" + countryCode.toLowerCase() + ".png";
        }
        return null;
    }
    
    /**
     * Map area name to ISO country code for flag display
     */
    private String getCountryCode() {
        if (name == null) return null;
        
        switch (name.toLowerCase()) {
            case "american": return "us";
            case "british": return "gb";
            case "canadian": return "ca";
            case "chinese": return "cn";
            case "croatian": return "hr";
            case "dutch": return "nl";
            case "egyptian": return "eg";
            case "filipino": return "ph";
            case "french": return "fr";
            case "greek": return "gr";
            case "indian": return "in";
            case "irish": return "ie";
            case "italian": return "it";
            case "jamaican": return "jm";
            case "japanese": return "jp";
            case "kenyan": return "ke";
            case "malaysian": return "my";
            case "mexican": return "mx";
            case "moroccan": return "ma";
            case "polish": return "pl";
            case "portuguese": return "pt";
            case "russian": return "ru";
            case "spanish": return "es";
            case "thai": return "th";
            case "tunisian": return "tn";
            case "turkish": return "tr";
            case "ukrainian": return "ua";
            case "vietnamese": return "vn";
            default: return null;
        }
    }
}
