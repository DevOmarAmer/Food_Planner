package com.example.foodplanner.presentation.plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.PlannedMeal;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PlanFragment extends Fragment implements PlanView, PlanAdapter.OnPlanMealClickListener {
    
    private RecyclerView rvPlan;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private LinearLayout layoutGuestPrompt;
    private MaterialButton btnSignIn;
    
    private PlanPresenter presenter;
    private PlanAdapter adapter;
    private SharedPrefsHelper sharedPrefsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefsHelper = SharedPrefsHelper.getInstance(requireContext());
        initViews(view);
        setupRecyclerView();
        
       
        if (sharedPrefsHelper.isGuest()) {
            showGuestPrompt();
        } else {
            presenter = new PlanPresenterImpl(this, requireContext());
            presenter.loadPlan();
        }
    }
    
    private void initViews(View view) {
        rvPlan = view.findViewById(R.id.rvPlan);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutGuestPrompt = view.findViewById(R.id.layoutGuestPrompt);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        
        
        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> navigateToAuth());
        }
    }
    
    private void showGuestPrompt() {
        progressBar.setVisibility(View.GONE);
        rvPlan.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutGuestPrompt.setVisibility(View.VISIBLE);
    }
    
    private void navigateToAuth() {
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    
    private void setupRecyclerView() {
        adapter = new PlanAdapter(this);
        rvPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlan.setAdapter(adapter);
    }
    
    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvPlan.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutGuestPrompt.setVisibility(View.GONE);
    }
    
    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    @Override
    public void showPlan(List<PlannedMeal> plannedMeals) {
        rvPlan.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutGuestPrompt.setVisibility(View.GONE);
        adapter.setPlannedMeals(plannedMeals);
    }
    
    @Override
    public void showEmpty() {
        rvPlan.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutGuestPrompt.setVisibility(View.GONE);
    }
    
    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void navigateToMealDetails(String mealId, String mealName) {
        Intent intent = new Intent(getContext(), MealDetailsActivity.class);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_ID, mealId);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_NAME, mealName);
        startActivity(intent);
    }
    
    @Override
    public void showMealRemoved(String mealName) {
        if (getView() != null) {
            Snackbar.make(getView(), mealName + " removed from plan", Snackbar.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onMealClick(PlannedMeal plannedMeal) {
        presenter.onMealClicked(plannedMeal);
    }
    
    @Override
    public void onRemoveClick(PlannedMeal plannedMeal) {
        presenter.onRemoveMeal(plannedMeal);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
