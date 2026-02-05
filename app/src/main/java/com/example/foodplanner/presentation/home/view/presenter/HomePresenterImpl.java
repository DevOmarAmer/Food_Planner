package com.example.foodplanner.presentation.home.view.presenter;

import android.util.Log;

import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;
import com.example.foodplanner.presentation.home.view.HomeView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImpl implements HomePresenter {
    
    private static final String TAG = "HomePresenter";
    private HomeView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables;
    
    public HomePresenterImpl(HomeView view) {
        this.view = view;
        this.repository = MealRepository.getInstance();
        this.disposables = new CompositeDisposable();
    }
    
    @Override
    public void loadRandomMeal() {
        disposables.add(
                repository.getRandomMeal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    if (view != null) {
                                        view.showRandomMeal(meal);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Failed to load random meal: " + error.getMessage(), error);
                                    if (view != null) {
                                        view.showError("Failed to load meal: " + error.getMessage());
                                    }
                                }
                        )
        );
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
                                    Log.e(TAG, "Failed to load categories: " + error.getMessage(), error);
                                    if (view != null) {
                                        view.showError("Categories: " + error.getMessage());
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
                                    Log.e(TAG, "Failed to load areas: " + error.getMessage(), error);
                                    if (view != null) {
                                        view.showError("Countries: " + error.getMessage());
                                    }
                                }
                        )
        );
    }
    
    @Override
    public void loadAllData() {
        view.showLoading();
        loadRandomMeal();
        loadCategories();
        loadAreas();
        view.hideLoading();
    }
    
    @Override
    public void onMealClicked(Meal meal) {
        if (view != null && meal != null) {
            view.navigateToMealDetails(meal.getId());
        }
    }
    
    @Override
    public void onCategoryClicked(Category category) {
        if (view != null && category != null) {
            view.navigateToCategory(category.getName());
        }
    }
    
    @Override
    public void onAreaClicked(Area area) {
        if (view != null && area != null) {
            view.navigateToArea(area.getName());
        }
    }
    
    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
