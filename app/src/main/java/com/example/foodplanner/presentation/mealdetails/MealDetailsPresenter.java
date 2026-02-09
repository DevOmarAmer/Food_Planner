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
    private boolean isFavorite = false;

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
                                }));
    }

    @Override
    public void toggleFavorite(Meal meal) {
        if (isFavorite) {
            disposables.add(
                    repository.removeFromFavorites(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        isFavorite = false;
                                        view.showRemovedFromFavorites();
                                    },
                                    throwable -> view.showError(throwable.getMessage())));
        } else {
            disposables.add(
                    repository.addToFavorites(meal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        isFavorite = true;
                                        view.showAddedToFavorites();
                                    },
                                    throwable -> view.showError(throwable.getMessage())));
        }
    }

    @Override
    public void addToPlan(Meal meal, String day) {
        disposables.add(
                repository.addToPlan(meal, day)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showAddedToPlan(day),
                                throwable -> view.showError(throwable.getMessage())));
    }

    @Override
    public void addToPlanWithDate(Meal meal, String day, long plannedDate) {
        disposables.add(
                repository.addToPlanWithDate(meal, day, plannedDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showAddedToPlan(day),
                                throwable -> view.showError(throwable.getMessage())));
    }

    @Override
    public void checkFavoriteStatus(String mealId) {
        disposables.add(
                repository.isFavorite(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isFav -> {
                                    isFavorite = isFav;
                                    view.showFavoriteStatus(isFav);
                                },
                                throwable -> view.showFavoriteStatus(false)));
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
