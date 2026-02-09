package com.example.foodplanner.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Custom Glide module for offline image caching.
 * This enables automatic disk caching of meal images for offline viewing.
 */
@GlideModule
public final class FoodPlannerGlideModule extends AppGlideModule {

    // 100 MB cache size
    private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // Set up disk cache
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "meal_images", DISK_CACHE_SIZE));

        // Set default request options to cache all images
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // No custom components needed
    }

    @Override
    public boolean isManifestParsingEnabled() {
        // Disable manifest parsing for faster initialization
        return false;
    }
}
