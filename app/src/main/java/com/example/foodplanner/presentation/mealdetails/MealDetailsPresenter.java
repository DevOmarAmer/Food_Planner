package com.example.foodplanner.presentation.mealdetails;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private MealDetailsContract.View view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public MealDetailsPresenter(MealDetailsContract.View view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getMealDetails(String mealId) {
        view.showLoading();

        disposables.add(
                repository.getMealById(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    view.hideLoading();
                                    view.showMeal(meal);
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError(throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    public void toggleFavorite(Meal meal) {
        view.showAddedToFavorites();
    }

    @Override
    public void addToPlan(Meal meal, String day) {
        view.showAddedToPlan(day);
    }

    @Override
    public void checkFavoriteStatus(String mealId) {
        view.showFavoriteStatus(false);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
