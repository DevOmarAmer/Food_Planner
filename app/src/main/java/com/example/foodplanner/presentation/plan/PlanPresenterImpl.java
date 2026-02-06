package com.example.foodplanner.presentation.plan;

import android.content.Context;

import com.example.foodplanner.data.model.PlannedMeal;
import com.example.foodplanner.data.repository.MealRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlanPresenterImpl implements PlanPresenter {
    
    private PlanView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    public PlanPresenterImpl(PlanView view, Context context) {
        this.view = view;
        this.repository = MealRepository.getInstance(context);
    }
    
    @Override
    public void loadPlan() {
        view.showLoading();
        
        disposables.add(
                repository.getAllPlannedMeals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                plannedMeals -> {
                                    view.hideLoading();
                                    if (plannedMeals.isEmpty()) {
                                        view.showEmpty();
                                    } else {
                                        view.showPlan(plannedMeals);
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
    public void onMealClicked(PlannedMeal plannedMeal) {
        view.navigateToMealDetails(plannedMeal.getMealId(), plannedMeal.getMealName());
    }
    
    @Override
    public void onRemoveMeal(PlannedMeal plannedMeal) {
        disposables.add(
                repository.removeFromPlan(plannedMeal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showMealRemoved(plannedMeal.getMealName()),
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
