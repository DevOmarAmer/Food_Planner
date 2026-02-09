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
import com.google.android.material.appbar.MaterialToolbar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchFragment extends Fragment implements SearchView {

    public static final String ARG_INITIAL_TAB = "initialTab";
    public static final int TAB_MEALS = 0;
    public static final int TAB_CATEGORIES = 1;
    public static final int TAB_COUNTRIES = 2;
    public static final int TAB_INGREDIENTS = 3;

    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private RecyclerView rvMeals;
    private RecyclerView rvCategories;
    private RecyclerView rvAreas;
    private RecyclerView rvIngredients;
    private RecyclerView rvSearchResults;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private SearchPresenterImpl presenter;
    private MealsAdapter mealsAdapter;
    private SearchCategoryAdapter categoryAdapter;
    private SearchAreaAdapter areaAdapter;
    private SearchIngredientAdapter ingredientAdapter;
    private MealsAdapter searchResultsAdapter;

    // Original unfiltered lists for local filtering
    private List<Category> allCategories = new ArrayList<>();
    private List<Area> allAreas = new ArrayList<>();
    private List<Ingredient> allIngredients = new ArrayList<>();

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

        int initialTab = getArguments() != null ? getArguments().getInt(ARG_INITIAL_TAB, TAB_MEALS) : TAB_MEALS;
        if (initialTab >= 0 && initialTab <= TAB_INGREDIENTS) {
            tabLayout.selectTab(tabLayout.getTabAt(initialTab));
            showTabContent(initialTab);
        } else {
            showTabContent(TAB_MEALS);
        }

        presenter = new SearchPresenterImpl(this, requireContext());
        loadInitialData();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tabLayout);
        progressBar = view.findViewById(R.id.progressBar);
        rvMeals = view.findViewById(R.id.rvMeals);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvAreas = view.findViewById(R.id.rvAreas);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Meals"));
        tabLayout.addTab(tabLayout.newTab().setText("Categories"));
        tabLayout.addTab(tabLayout.newTab().setText("Countries"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                exitSearchMode();
                showTabContent(tab.getPosition());
                updateSearchHint(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void updateSearchHint(int tabPosition) {
        switch (tabPosition) {
            case TAB_MEALS:
                etSearch.setHint("Search for meals...");
                break;
            case TAB_CATEGORIES:
                etSearch.setHint("Filter categories...");
                break;
            case TAB_COUNTRIES:
                etSearch.setHint("Filter countries...");
                break;
            case TAB_INGREDIENTS:
                etSearch.setHint("Filter ingredients...");
                break;
        }
    }

    private void setupRecyclerViews() {
        mealsAdapter = new MealsAdapter();
        rvMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMeals.setAdapter(mealsAdapter);
        mealsAdapter.setOnMealClickListener(meal -> presenter.onMealClicked(meal));

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
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(query -> {
                            if (query.isEmpty()) {
                                exitSearchMode();
                            } else {
                                performTabSearch(query);
                            }
                        }));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubject.onNext(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
                if (!query.isEmpty()) {
                    performTabSearch(query);
                }
                return true;
            }
            return false;
        });
    }

    private void performTabSearch(String query) {
        int selectedTab = tabLayout.getSelectedTabPosition();
        isSearchMode = true;

        switch (selectedTab) {
            case TAB_MEALS:
                presenter.searchMeals(query);
                break;
            case TAB_CATEGORIES:
                filterCategories(query);
                break;
            case TAB_COUNTRIES:
                filterAreas(query);
                break;
            case TAB_INGREDIENTS:
                filterIngredients(query);
                break;
        }
    }

    private void filterCategories(String query) {
        String lowerQuery = query.toLowerCase();
        List<Category> filtered = allCategories.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            showEmpty();
            tvEmptyMessage.setText("No categories found");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            categoryAdapter.setCategories(filtered);
        }
    }

    private void filterAreas(String query) {
        String lowerQuery = query.toLowerCase();
        List<Area> filtered = allAreas.stream()
                .filter(a -> a.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            showEmpty();
            tvEmptyMessage.setText("No countries found");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            areaAdapter.setAreas(filtered);
        }
    }

    private void filterIngredients(String query) {
        String lowerQuery = query.toLowerCase();
        List<Ingredient> filtered = allIngredients.stream()
                .filter(i -> i.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            showEmpty();
            tvEmptyMessage.setText("No ingredients found");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            ingredientAdapter.setIngredients(filtered);
        }
    }

    private void exitSearchMode() {
        isSearchMode = false;
        rvSearchResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        // Restore original lists
        categoryAdapter.setCategories(allCategories);
        areaAdapter.setAreas(allAreas);
        ingredientAdapter.setIngredients(allIngredients);

        int position = tabLayout.getSelectedTabPosition();
        showTabContent(position >= 0 ? position : TAB_MEALS);
    }

    private void loadInitialData() {
        presenter.loadCategories();
        presenter.loadAreas();
        presenter.loadIngredients();
    }

    private void showTabContent(int tabPosition) {
        rvMeals.setVisibility(tabPosition == TAB_MEALS ? View.VISIBLE : View.GONE);
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
        allCategories = new ArrayList<>(categories);
        categoryAdapter.setCategories(categories);
    }

    @Override
    public void showAreas(List<Area> areas) {
        allAreas = new ArrayList<>(areas);
        areaAdapter.setAreas(areas);
    }

    @Override
    public void showIngredients(List<Ingredient> ingredients) {
        allIngredients = new ArrayList<>(ingredients);
        ingredientAdapter.setIngredients(ingredients);
    }

    @Override
    public void showSearchResults(List<Meal> meals) {
        int selectedTab = tabLayout.getSelectedTabPosition();

        if (selectedTab == TAB_MEALS) {
            rvMeals.setVisibility(View.VISIBLE);
            rvCategories.setVisibility(View.GONE);
            rvAreas.setVisibility(View.GONE);
            rvIngredients.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            mealsAdapter.setMeals(meals);
        } else {
            rvMeals.setVisibility(View.GONE);
            rvCategories.setVisibility(View.GONE);
            rvAreas.setVisibility(View.GONE);
            rvIngredients.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            searchResultsAdapter.setMeals(meals);
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmpty() {
        rvMeals.setVisibility(View.GONE);
        rvCategories.setVisibility(View.GONE);
        rvAreas.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText("No results found");
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
