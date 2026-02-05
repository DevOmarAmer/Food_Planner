package com.example.foodplanner.presentation.search;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;
import com.example.foodplanner.presentation.mealslist.view.MealsListActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImpl implements SearchPresenter {

    private static final String TAG = "SearchPresenter";
    private SearchView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public SearchPresenterImpl(SearchView view, Context context) {
        this.view = view;
        this.repository = MealRepository.getInstance(context);
    }

    @Override
    public void loadCategories() {
        disposables.add(
                repository.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categories -> {
                                    if (view != null) {
                                        view.showCategories(categories);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading categories: " + error.getMessage());
                                    if (view != null) {
                                        view.showError("Failed to load categories");
                                    }
                                }
                        )
        );
    }

    @Override
    public void loadAreas() {
        disposables.add(
                repository.getAreas()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                areas -> {
                                    if (view != null) {
                                        view.showAreas(areas);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading areas: " + error.getMessage());
                                    if (view != null) {
                                        view.showError("Failed to load countries");
                                    }
                                }
                        )
        );
    }

    @Override
    public void loadIngredients() {
        disposables.add(
                repository.getIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ingredients -> {
                                    if (view != null) {
                                        view.showIngredients(ingredients);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading ingredients: " + error.getMessage());
                                    if (view != null) {
                                        view.showError("Failed to load ingredients");
                                    }
                                }
                        )
        );
    }

    @Override
    public void searchMeals(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        if (view != null) {
            view.showLoading();
        }

        disposables.add(
                repository.searchMealsByName(query.trim())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    if (view != null) {
                                        view.hideLoading();
                                        if (meals == null || meals.isEmpty()) {
                                            view.showEmpty();
                                        } else {
                                            view.showSearchResults(meals);
                                        }
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error searching meals: " + error.getMessage());
                                    if (view != null) {
                                        view.hideLoading();
                                        view.showError("Search failed: " + error.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void onCategoryClicked(Category category) {
        if (view != null && category != null) {
            view.navigateToMealsList(MealsListActivity.FILTER_CATEGORY, category.getName());
        }
    }

    @Override
    public void onAreaClicked(Area area) {
        if (view != null && area != null) {
            view.navigateToMealsList(MealsListActivity.FILTER_AREA, area.getName());
        }
    }

    @Override
    public void onIngredientClicked(Ingredient ingredient) {
        if (view != null && ingredient != null) {
            view.navigateToMealsList(MealsListActivity.FILTER_INGREDIENT, ingredient.getName());
        }
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null && meal != null) {
            view.navigateToMealDetails(meal.getId(), meal.getName());
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
