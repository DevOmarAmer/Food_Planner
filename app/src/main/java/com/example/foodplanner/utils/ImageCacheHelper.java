package com.example.foodplanner.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.PlannedMeal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class for pre-caching meal images for offline viewing.
 * Call preloadMealImage() when a meal is added to favorites or planned meals.
 */
public class ImageCacheHelper {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Pre-cache the meal image for offline viewing.
     * This downloads and caches the image in the background.
     */
    public static void preloadMealImage(Context context, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())
            return;

        executor.execute(() -> {
            try {
                Glide.with(context.getApplicationContext())
                        .downloadOnly()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(imageUrl)
                        .submit()
                        .get(); // This blocks but we're on a background thread
            } catch (Exception e) {
                // Silently fail - not critical if caching fails
                e.printStackTrace();
            }
        });
    }

    /**
     * Pre-cache meal image from a Meal object.
     */
    public static void preloadMealImage(Context context, Meal meal) {
        if (meal != null && meal.getThumbnail() != null) {
            preloadMealImage(context, meal.getThumbnail());
        }
    }

    /**
     * Pre-cache meal image from a PlannedMeal object.
     */
    public static void preloadMealImage(Context context, PlannedMeal plannedMeal) {
        if (plannedMeal != null && plannedMeal.getMealThumb() != null) {
            preloadMealImage(context, plannedMeal.getMealThumb());
        }
    }

    /**
     * Clear the entire image cache.
     */
    public static void clearImageCache(Context context) {
        executor.execute(() -> {
            Glide.get(context.getApplicationContext()).clearDiskCache();
        });
        Glide.get(context.getApplicationContext()).clearMemory();
    }
}
