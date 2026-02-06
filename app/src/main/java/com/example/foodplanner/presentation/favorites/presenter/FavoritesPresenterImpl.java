package com.example.foodplanner.presentation.favorites.presenter;

import android.content.Context;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;
import com.example.foodplanner.presentation.favorites.view.FavoritesView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenterImpl implements FavoritesPresenter {

    private FavoritesView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public FavoritesPresenterImpl(FavoritesView view, Context context) {
        this.view = view;
        this.repository = MealRepository.getInstance(context);
    }

    @Override
    public void loadFavorites() {
        view.showLoading();
        disposables.add(
                repository.getFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmpty();
                                    } else {
                                        view.showFavorites(meals);
                                    }
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError(throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onMealClicked(Meal meal) {
        view.navigateToMealDetails(meal.getId(), meal.getName());
    }

    @Override
    public void onRemoveFavorite(Meal meal) {
        disposables.add(
                repository.removeFromFavorites(meal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showMealRemoved(meal.getName()),
                                throwable -> view.showError(throwable.getMessage())
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
