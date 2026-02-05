package com.example.foodplanner.presentation.mealslist.presenter;

import android.util.Log;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;
import com.example.foodplanner.presentation.mealslist.MealsListView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsListPresenterImpl implements MealsListPresenter {

    private static final String TAG = "MealsListPresenter";
    private MealsListView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public MealsListPresenterImpl(MealsListView view) {
        this.view = view;
        this.repository = MealRepository.getInstance();
    }

    @Override
    public void loadMealsByCategory(String category) {
        loadMeals(repository.filterByCategory(category));
    }

    @Override
    public void loadMealsByArea(String area) {
        loadMeals(repository.filterByArea(area));
    }

    @Override
    public void loadMealsByIngredient(String ingredient) {
        loadMeals(repository.filterByIngredient(ingredient));
    }

    @Override
    public void searchMeals(String query) {
        loadMeals(repository.searchMealsByName(query));
    }

    private void loadMeals(Single<List<Meal>> source) {
        if (view == null) return;
        
        view.showLoading();
        
        disposables.add(
                source.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    if (view != null) {
                                        view.hideLoading();
                                        if (meals == null || meals.isEmpty()) {
                                            view.showEmpty();
                                        } else {
                                            view.showMeals(meals);
                                        }
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading meals: " + error.getMessage(), error);
                                    if (view != null) {
                                        view.hideLoading();
                                        view.showError(error.getMessage());
                                    }
                                }
                        )
        );
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
