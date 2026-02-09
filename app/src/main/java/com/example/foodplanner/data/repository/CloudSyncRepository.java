package com.example.foodplanner.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.PlannedMeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Repository for syncing favorites and meal plans to/from Firebase Firestore.
 * Data structure in Firestore:
 * users/{userId}/favorites/{mealId}
 * users/{userId}/plannedMeals/{plannedMealId}
 */
public class CloudSyncRepository {
    
    private static final String TAG = "CloudSyncRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_FAVORITES = "favorites";
    private static final String COLLECTION_PLANNED_MEALS = "plannedMeals";
    
    private static CloudSyncRepository instance;
    private final FirebaseFirestore firestore;
    private final MealRepository mealRepository;
    CompositeDisposable disposable = new CompositeDisposable();


    
    private CloudSyncRepository(Context context) {
        firestore = FirebaseFirestore.getInstance();
        mealRepository = MealRepository.getInstance(context);
    }
    
    public static synchronized CloudSyncRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CloudSyncRepository(context);
        }
        return instance;
    }
    
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public Completable uploadFavoritesToCloud() {
        return Completable.create(emitter -> {
            String userId = getCurrentUserId();
            if (userId == null) {
                emitter.onError(new Exception("User not logged in"));
                return;
            }
            
            mealRepository.getFavorites()
                .firstOrError()
                .subscribe(meals -> {
                    if (meals.isEmpty()) {
                        emitter.onComplete();
                        return;
                    }
                    
                    WriteBatch batch = firestore.batch();
                    for (Meal meal : meals) {
                        Map<String, Object> mealData = mealToMap(meal);
                        batch.set(
                            firestore.collection(COLLECTION_USERS)
                                .document(userId)
                                .collection(COLLECTION_FAVORITES)
                                .document(meal.getId()),
                            mealData
                        );
                    }
                    
                    batch.commit()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Favorites uploaded successfully");
                            emitter.onComplete();
                        })
                        .addOnFailureListener(emitter::onError);
                }, emitter::onError);
        });
    }
    

    public Completable uploadPlannedMealsToCloud() {
        return Completable.create(emitter -> {
            String userId = getCurrentUserId();
            if (userId == null) {
                emitter.onError(new Exception("User not logged in"));
                return;
            }
            
            mealRepository.getAllPlannedMeals()
                .firstOrError()
                .subscribe(plannedMeals -> {
                    if (plannedMeals.isEmpty()) {
                        emitter.onComplete();
                        return;
                    }
                    
                    WriteBatch batch = firestore.batch();
                    for (PlannedMeal plannedMeal : plannedMeals) {
                        Map<String, Object> mealData = plannedMealToMap(plannedMeal);
                        batch.set(
                            firestore.collection(COLLECTION_USERS)
                                .document(userId)
                                .collection(COLLECTION_PLANNED_MEALS)
                                .document(plannedMeal.getId()),
                            mealData
                        );
                    }
                    
                    batch.commit()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Planned meals uploaded successfully");
                            emitter.onComplete();
                        })
                        .addOnFailureListener(emitter::onError);
                }, emitter::onError);
        });
    }
    

    public Completable downloadFavoritesFromCloud() {
        return Completable.create(emitter -> {
            String userId = getCurrentUserId();
            if (userId == null) {
                emitter.onError(new Exception("User not logged in"));
                return;
            }
            
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_FAVORITES)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Meal> meals = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Meal meal = mapToMeal(doc);
                        if (meal != null) {
                            meals.add(meal);
                        }
                    }
                    
                    if (meals.isEmpty()) {
                        emitter.onComplete();
                        return;
                    }
                    
                    // Save each meal locally using Completable.merge
                    io.reactivex.rxjava3.core.Observable.fromIterable(meals)
                        .flatMapCompletable(meal -> mealRepository.addToFavorites(meal))
                        .subscribe(
                            () -> {
                                Log.d(TAG, "Favorites downloaded and saved locally");
                                emitter.onComplete();
                            },
                            emitter::onError
                        );
                })
                .addOnFailureListener(emitter::onError);
        });
    }
    

    public Completable downloadPlannedMealsFromCloud() {
        return Completable.create(emitter -> {
            String userId = getCurrentUserId();
            if (userId == null) {
                emitter.onError(new Exception("User not logged in"));
                return;
            }
            
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_PLANNED_MEALS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PlannedMeal> plannedMeals = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        PlannedMeal plannedMeal = mapToPlannedMeal(doc);
                        if (plannedMeal != null) {
                            plannedMeals.add(plannedMeal);
                        }
                    }
                    
                    if (plannedMeals.isEmpty()) {
                        emitter.onComplete();
                        return;
                    }
                    
                    // Save planned meals using the repository
                    mealRepository.insertPlannedMeals(plannedMeals)
                        .subscribe(
                            () -> {
                                Log.d(TAG, "Planned meals downloaded and saved locally");
                                emitter.onComplete();
                            },
                            emitter::onError
                        );
                })
                .addOnFailureListener(emitter::onError);
        });
    }
    

    public Completable syncToCloud() {
        return uploadFavoritesToCloud()
            .andThen(uploadPlannedMealsToCloud());
    }
    

    public Completable syncFromCloud() {
        return downloadFavoritesFromCloud()
            .andThen(downloadPlannedMealsFromCloud());
    }
    
   
    public Completable fullSync() {
        return syncToCloud().andThen(syncFromCloud());
    }
    
    // Helper methods for conversion
    private Map<String, Object> mealToMap(Meal meal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", meal.getId());
        map.put("name", meal.getName());
        map.put("category", meal.getCategory());
        map.put("area", meal.getArea());
        map.put("instructions", meal.getInstructions());
        map.put("thumbnail", meal.getThumbnail());
        map.put("tags", meal.getTags());
        map.put("youtubeUrl", meal.getYoutubeUrl());
        
        // Store ingredients list
        List<String> ingredients = meal.getIngredientsList();
        List<String> measures = meal.getMeasuresList();
        map.put("ingredients", ingredients);
        map.put("measures", measures);
        
        map.put("syncedAt", System.currentTimeMillis());
        return map;
    }
    
    private Map<String, Object> plannedMealToMap(PlannedMeal plannedMeal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", plannedMeal.getId());
        map.put("mealId", plannedMeal.getMealId());
        map.put("mealName", plannedMeal.getMealName());
        map.put("mealThumb", plannedMeal.getMealThumb());
        map.put("category", plannedMeal.getCategory());
        map.put("area", plannedMeal.getArea());
        map.put("day", plannedMeal.getDay());
        map.put("dateAdded", plannedMeal.getDateAdded());
        map.put("syncedAt", System.currentTimeMillis());
        return map;
    }
    
    private Meal mapToMeal(DocumentSnapshot doc) {
        try {
            Meal meal = new Meal();
            meal.setId(doc.getString("id"));
            meal.setName(doc.getString("name"));
            meal.setCategory(doc.getString("category"));
            meal.setArea(doc.getString("area"));
            meal.setInstructions(doc.getString("instructions"));
            meal.setThumbnail(doc.getString("thumbnail"));
            meal.setTags(doc.getString("tags"));
            meal.setYoutubeUrl(doc.getString("youtubeUrl"));
            
            // Handle ingredients
            @SuppressWarnings("unchecked")
            List<String> ingredients = (List<String>) doc.get("ingredients");
            @SuppressWarnings("unchecked")
            List<String> measures = (List<String>) doc.get("measures");
            
            if (ingredients != null && measures != null) {
                meal.setIngredientsFromList(ingredients, measures);
            }
            
            return meal;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing meal from Firestore", e);
            return null;
        }
    }
    
    private PlannedMeal mapToPlannedMeal(DocumentSnapshot doc) {
        try {
            PlannedMeal plannedMeal = new PlannedMeal();
            plannedMeal.setId(doc.getString("id"));
            plannedMeal.setMealId(doc.getString("mealId"));
            plannedMeal.setMealName(doc.getString("mealName"));
            plannedMeal.setMealThumb(doc.getString("mealThumb"));
            plannedMeal.setCategory(doc.getString("category"));
            plannedMeal.setArea(doc.getString("area"));
            plannedMeal.setDay(doc.getString("day"));
            
            Long dateAdded = doc.getLong("dateAdded");
            if (dateAdded != null) {
                plannedMeal.setDateAdded(dateAdded);
            }
            
            return plannedMeal;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing planned meal from Firestore", e);
            return null;
        }
    }
}
