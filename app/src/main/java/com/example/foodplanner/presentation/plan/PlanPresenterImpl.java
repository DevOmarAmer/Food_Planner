package com.example.foodplanner.presentation.plan;

import android.content.Context;

import com.example.foodplanner.data.model.PlannedMeal;
import com.example.foodplanner.data.repository.MealRepository;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlanPresenterImpl implements PlanPresenter {

    private PlanView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private long currentWeekStart;

    public PlanPresenterImpl(PlanView view, Context context) {
        this.view = view;
        this.repository = MealRepository.getInstance(context);
        this.currentWeekStart = getWeekStartDate(System.currentTimeMillis());
    }

    /**
     * Get the start of the week (Saturday at midnight) for a given timestamp
     */
    private long getWeekStartDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // Set to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Find Saturday (start of week)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract;
        if (dayOfWeek == Calendar.SATURDAY) {
            daysToSubtract = 0;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            daysToSubtract = 1;
        } else {
            daysToSubtract = dayOfWeek - Calendar.SATURDAY + 7;
        }

        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        return calendar.getTimeInMillis();
    }

    @Override
    public void loadPlan() {
        loadPlanForWeek(currentWeekStart);
    }

    @Override
    public void loadPlanForWeek(long weekStartDate) {
        this.currentWeekStart = weekStartDate;
        view.showLoading();

        // Calculate week end date (7 days after start)
        long weekEndDate = weekStartDate + (7L * 24 * 60 * 60 * 1000);

        disposables.add(
                repository.getPlannedMealsByDateRange(weekStartDate, weekEndDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                plannedMeals -> {
                                    view.hideLoading();
                                    // Always show the plan (adapter will handle empty days)
                                    view.showPlan(plannedMeals);
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError(throwable.getMessage());
                                }));
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
                                throwable -> view.showError(throwable.getMessage())));
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
