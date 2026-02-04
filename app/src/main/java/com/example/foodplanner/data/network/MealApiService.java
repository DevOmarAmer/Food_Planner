package com.example.foodplanner.data.network;

import com.example.foodplanner.data.model.AreaResponse;
import com.example.foodplanner.data.model.CategoryResponse;
import com.example.foodplanner.data.model.IngredientResponse;
import com.example.foodplanner.data.model.MealResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    
    @GET("random.php")
    Single<MealResponse> getRandomMeal();
    
    @GET("categories.php")
    Single<CategoryResponse> getCategories();
    
    @GET("list.php?a=list")
    Single<AreaResponse> getAreas();
    
    @GET("list.php?i=list")
    Single<IngredientResponse> getIngredients();
    
    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String mealId);
    
    @GET("search.php")
    Single<MealResponse> searchMealsByName(@Query("s") String name);
    
    @GET("filter.php")
    Single<MealResponse> filterByCategory(@Query("c") String category);
    
    @GET("filter.php")
    Single<MealResponse> filterByArea(@Query("a") String area);
    
    @GET("filter.php")
    Single<MealResponse> filterByIngredient(@Query("i") String ingredient);
    
    @GET("search.php")
    Single<MealResponse> searchByFirstLetter(@Query("f") String letter);
}
