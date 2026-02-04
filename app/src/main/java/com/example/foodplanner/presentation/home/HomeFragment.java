package com.example.foodplanner.presentation.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.home.adapters.AreaAdapter;
import com.example.foodplanner.presentation.home.adapters.CategoryAdapter;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;

import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private SwipeRefreshLayout swipeRefresh;
    private CardView cardMealOfDay;
    private ImageView ivMealOfDay;
    private TextView tvMealOfDayName;
    private TextView tvMealOfDayCategory;
    private RecyclerView rvCategories;
    private RecyclerView rvAreas;

    private HomePresenter presenter;
    private CategoryAdapter categoryAdapter;
    private AreaAdapter areaAdapter;
    private Meal currentMealOfDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerViews();
        setupListeners();
        
        presenter = new HomePresenter(this);
        presenter.loadAllData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        cardMealOfDay = view.findViewById(R.id.cardMealOfDay);
        ivMealOfDay = view.findViewById(R.id.ivMealOfDay);
        tvMealOfDayName = view.findViewById(R.id.tvMealOfDayName);
        tvMealOfDayCategory = view.findViewById(R.id.tvMealOfDayCategory);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvAreas = view.findViewById(R.id.rvAreas);
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter();
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        areaAdapter = new AreaAdapter();
        rvAreas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAreas.setAdapter(areaAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> presenter.loadAllData());
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);

        cardMealOfDay.setOnClickListener(v -> {
            if (currentMealOfDay != null) {
                presenter.onMealClicked(currentMealOfDay);
            }
        });

        categoryAdapter.setOnCategoryClickListener(category -> presenter.onCategoryClicked(category));
        areaAdapter.setOnAreaClickListener(area -> presenter.onAreaClicked(area));
    }

    @Override
    public void showLoading() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
    }

    @Override
    public void hideLoading() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void showRandomMeal(Meal meal) {
        currentMealOfDay = meal;
        tvMealOfDayName.setText(meal.getName());
        tvMealOfDayCategory.setText(meal.getCategory() + " • " + meal.getArea());
        
        Glide.with(this)
                .load(meal.getThumbnail())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_food)
                .into(ivMealOfDay);
    }

    @Override
    public void showCategories(List<Category> categories) {
        categoryAdapter.setCategories(categories);
    }

    @Override
    public void showAreas(List<Area> areas) {
        areaAdapter.setAreas(areas);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMealDetails(String mealId) {
        Intent intent = new Intent(getContext(), MealDetailsActivity.class);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_ID, mealId);
        if (currentMealOfDay != null) {
            intent.putExtra(MealDetailsActivity.EXTRA_MEAL_NAME, currentMealOfDay.getName());
        }
        startActivity(intent);
    }

    @Override
    public void navigateToCategory(String categoryName) {
        Toast.makeText(getContext(), "Category: " + categoryName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToArea(String areaName) {
        Toast.makeText(getContext(), "Country: " + areaName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
