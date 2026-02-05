package com.example.foodplanner.presentation.mealslist.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.example.foodplanner.presentation.mealslist.presenter.MealsListPresenterImpl;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MealsListActivity extends AppCompatActivity implements MealsListView {

    public static final String EXTRA_FILTER_TYPE = "filter_type";
    public static final String EXTRA_FILTER_VALUE = "filter_value";
    
    public static final String FILTER_CATEGORY = "category";
    public static final String FILTER_AREA = "area";
    public static final String FILTER_INGREDIENT = "ingredient";
    public static final String FILTER_SEARCH = "search";

    private MealsListPresenterImpl presenter;
    private MealsAdapter adapter;

    private MaterialToolbar toolbar;
    private RecyclerView rvMeals;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private String filterType;
    private String filterValue;

    public static Intent createIntent(Context context, String filterType, String filterValue) {
        Intent intent = new Intent(context, MealsListActivity.class);
        intent.putExtra(EXTRA_FILTER_TYPE, filterType);
        intent.putExtra(EXTRA_FILTER_VALUE, filterValue);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_list);

        filterType = getIntent().getStringExtra(EXTRA_FILTER_TYPE);
        filterValue = getIntent().getStringExtra(EXTRA_FILTER_VALUE);

        initViews();
        setupToolbar();
        setupRecyclerView();
        initPresenter();
        loadData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvMeals = findViewById(R.id.rvMeals);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupToolbar() {
        String title = filterValue != null ? filterValue : "Meals";
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new MealsAdapter();
        rvMeals.setLayoutManager(new LinearLayoutManager(this));
        rvMeals.setAdapter(adapter);
        adapter.setOnMealClickListener(meal -> presenter.onMealClicked(meal));
    }

    private void initPresenter() {
        presenter = new MealsListPresenterImpl(this);
    }

    private void loadData() {
        if (filterType == null || filterValue == null) {
            showError("Invalid filter");
            finish();
            return;
        }

        switch (filterType) {
            case FILTER_CATEGORY:
                presenter.loadMealsByCategory(filterValue);
                break;
            case FILTER_AREA:
                presenter.loadMealsByArea(filterValue);
                break;
            case FILTER_INGREDIENT:
                presenter.loadMealsByIngredient(filterValue);
                break;
            case FILTER_SEARCH:
                presenter.searchMeals(filterValue);
                break;
            default:
                showError("Unknown filter type");
                finish();
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvMeals.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMeals(List<Meal> meals) {
        rvMeals.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        adapter.setMeals(meals);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmpty() {
        rvMeals.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText("No meals found for \"" + filterValue + "\"");
    }

    @Override
    public void navigateToMealDetails(String mealId, String mealName) {
        Intent intent = new Intent(this, MealDetailsActivity.class);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_ID, mealId);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_NAME, mealName);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
