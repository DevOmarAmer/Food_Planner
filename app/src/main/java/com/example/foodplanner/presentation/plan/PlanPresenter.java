package com.example.foodplanner.presentation.plan;

import com.example.foodplanner.data.model.PlannedMeal;

public interface PlanPresenter {
    void loadPlan();

    void loadPlanForWeek(long weekStartDate);

    void onMealClicked(PlannedMeal plannedMeal);

    void onRemoveMeal(PlannedMeal plannedMeal);

    void onDestroy();
}
