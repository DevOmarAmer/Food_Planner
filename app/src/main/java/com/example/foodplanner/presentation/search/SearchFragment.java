package com.example.foodplanner.presentation.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Ingredient;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.example.foodplanner.presentation.mealslist.view.MealsAdapter;
import com.example.foodplanner.presentation.mealslist.view.MealsListActivity;
import com.example.foodplanner.presentation.search.adapters.SearchAreaAdapter;
import com.example.foodplanner.presentation.search.adapters.SearchCategoryAdapter;
import com.example.foodplanner.presentation.search.adapters.SearchIngredientAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchFragment extends Fragment implements SearchView {

    private static final int TAB_CATEGORIES = 0;
    private static final int TAB_COUNTRIES = 1;
    private static final int TAB_INGREDIENTS = 2;

    private TextInputEditText etSearch;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private RecyclerView rvCategories;
    private RecyclerView rvAreas;
    private RecyclerView rvIngredients;
    private RecyclerView rvSearchResults;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private SearchPresenterImpl presenter;
    private SearchCategoryAdapter categoryAdapter;
    private SearchAreaAdapter areaAdapter;
    private SearchIngredientAdapter ingredientAdapter;
    private MealsAdapter searchResultsAdapter;

    private boolean isSearchMode = false;
    private final PublishSubject<String> searchSubject = PublishSubject.create();
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupTabs();
        setupRecyclerViews();
        setupSearch();
        showTabContent(TAB_CATEGORIES);
        
        presenter = new SearchPresenterImpl(this);
        loadInitialData();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tabLayout);
        progressBar = view.findViewById(R.id.progressBar);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvAreas = view.findViewById(R.id.rvAreas);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Categories"));
        tabLayout.addTab(tabLayout.newTab().setText("Countries"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isSearchMode) {
                    showTabContent(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerViews() {
        categoryAdapter = new SearchCategoryAdapter();
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategories.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryClickListener(category -> presenter.onCategoryClicked(category));

        areaAdapter = new SearchAreaAdapter();
        rvAreas.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvAreas.setAdapter(areaAdapter);
        areaAdapter.setOnAreaClickListener(area -> presenter.onAreaClicked(area));

        ingredientAdapter = new SearchIngredientAdapter();
        rvIngredients.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvIngredients.setAdapter(ingredientAdapter);
        ingredientAdapter.setOnIngredientClickListener(ingredient -> presenter.onIngredientClicked(ingredient));

        searchResultsAdapter = new MealsAdapter();
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchResultsAdapter);
        searchResultsAdapter.setOnMealClickListener(meal -> presenter.onMealClicked(meal));
    }

    private void setupSearch() {
        disposables.add(
                searchSubject
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(query -> {
                            if (query.isEmpty()) {
                                exitSearchMode();
                            } else {
                                isSearchMode = true;
                                presenter.searchMeals(query);
                            }
                        })
        );

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubject.onNext(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        if (!query.isEmpty()) {
            isSearchMode = true;
            presenter.searchMeals(query);
        } else {
            exitSearchMode();
        }
    }

    private void exitSearchMode() {
        isSearchMode = false;
        rvSearchResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        int position = tabLayout.getSelectedTabPosition();
        showTabContent(position >= 0 ? position : TAB_CATEGORIES);
    }

    private void loadInitialData() {
        presenter.loadCategories();
        presenter.loadAreas();
        presenter.loadIngredients();
    }

    private void showTabContent(int tabPosition) {
        rvCategories.setVisibility(tabPosition == TAB_CATEGORIES ? View.VISIBLE : View.GONE);
        rvAreas.setVisibility(tabPosition == TAB_COUNTRIES ? View.VISIBLE : View.GONE);
        rvIngredients.setVisibility(tabPosition == TAB_INGREDIENTS ? View.VISIBLE : View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
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
    public void showIngredients(List<Ingredient> ingredients) {
        ingredientAdapter.setIngredients(ingredients);
    }

    @Override
    public void showSearchResults(List<Meal> meals) {
        rvCategories.setVisibility(View.GONE);
        rvAreas.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
        searchResultsAdapter.setMeals(meals);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmpty() {
        rvCategories.setVisibility(View.GONE);
        rvAreas.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText("No meals found");
    }

    @Override
    public void navigateToMealsList(String filterType, String filterValue) {
        Intent intent = MealsListActivity.createIntent(getContext(), filterType, filterValue);
        startActivity(intent);
    }

    @Override
    public void navigateToMealDetails(String mealId, String mealName) {
        Intent intent = new Intent(getContext(), MealDetailsActivity.class);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_ID, mealId);
        intent.putExtra(MealDetailsActivity.EXTRA_MEAL_NAME, mealName);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
