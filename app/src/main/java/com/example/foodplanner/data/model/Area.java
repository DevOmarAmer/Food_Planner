package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;


public class Area {
    
    @SerializedName("strArea")
    private String name;
    
    public Area() {}
    
    public Area(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; }
    
  
    public String getFlagUrl() {
        String countryCode = getCountryCode();
        if (countryCode != null) {
            return "https://flagcdn.com/w160/" + countryCode.toLowerCase() + ".png";
        }
        return null;
    }
    
  
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
            case "australian": return "au";
            case "argentinian": return "ar";
            case "algerian": return "dz";
            case "norwegian": return "no";
            case "saudi arabian": return "sa";
            case "slovakian": return "sk";
            case "syrian" : return "sy";
            case "uruguayan": return "uy";
            case "venezulan": return "ve";
            case "singaporean": return "sg";
            case "swedish": return "se";
            case "belgian": return "be";
            case "brazilian": return "br";

            default: return null;
        }
    }
}
