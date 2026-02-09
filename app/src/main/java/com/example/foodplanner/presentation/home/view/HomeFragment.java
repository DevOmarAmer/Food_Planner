package com.example.foodplanner.presentation.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Area;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.home.adapters.AreaAdapter;
import com.example.foodplanner.presentation.home.adapters.CategoryAdapter;
import com.example.foodplanner.presentation.home.view.presenter.HomePresenterImpl;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.example.foodplanner.presentation.mealslist.view.MealsListActivity;
import com.example.foodplanner.presentation.search.SearchFragment;
import com.example.foodplanner.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment implements HomeView {

    private SwipeRefreshLayout swipeRefresh;
    private View contentLayout;
    private LinearLayout offlineLayout;
    private LinearLayout onlineContentLayout;
    private LottieAnimationView lottieOffline;
    private MaterialButton btnRetry;
    private CardView cardMealOfDay;
    private ImageView ivMealOfDay;
    private TextView tvMealOfDayName;
    private TextView tvMealOfDayCategory;
    private RecyclerView rvCategories;
    private RecyclerView rvAreas;
    private TextInputEditText etSearch;
    private TextView tvSeeAllCategories;
    private TextView tvSeeAllCountries;

    private HomePresenterImpl presenter;
    private CategoryAdapter categoryAdapter;
    private AreaAdapter areaAdapter;
    private Meal currentMealOfDay;
    private NetworkUtils networkUtils;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkUtils = NetworkUtils.getInstance(requireContext());
        initViews(view);
        setupRecyclerViews();
        setupListeners();
        presenter = new HomePresenterImpl(this, requireContext());

        observeNetworkStatus();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        contentLayout = view.findViewById(R.id.contentLayout);
        offlineLayout = view.findViewById(R.id.offlineLayout);
        onlineContentLayout = view.findViewById(R.id.onlineContentLayout);
        lottieOffline = view.findViewById(R.id.lottieOffline);
        btnRetry = view.findViewById(R.id.btnRetry);
        cardMealOfDay = view.findViewById(R.id.cardMealOfDay);
        ivMealOfDay = view.findViewById(R.id.ivMealOfDay);
        tvMealOfDayName = view.findViewById(R.id.tvMealOfDayName);
        tvMealOfDayCategory = view.findViewById(R.id.tvMealOfDayCategory);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvAreas = view.findViewById(R.id.rvAreas);
        etSearch = view.findViewById(R.id.etSearch);
        tvSeeAllCategories = view.findViewById(R.id.tvSeeAllCategories);
        tvSeeAllCountries = view.findViewById(R.id.tvSeeAllCountries);

        ViewCompat.setOnApplyWindowInsetsListener(contentLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
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
        swipeRefresh.setOnRefreshListener(() -> {
            if (networkUtils.isNetworkAvailable()) {
                presenter.loadAllData();
            } else {
                swipeRefresh.setRefreshing(false);
                showOfflineState();
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);

        cardMealOfDay.setOnClickListener(v -> {
            if (currentMealOfDay != null) {
                presenter.onMealClicked(currentMealOfDay);
            }
        });

        categoryAdapter.setOnCategoryClickListener(category -> presenter.onCategoryClicked(category));
        areaAdapter.setOnAreaClickListener(area -> presenter.onAreaClicked(area));

        etSearch.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.searchFragment);
        });

        tvSeeAllCategories.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(SearchFragment.ARG_INITIAL_TAB, SearchFragment.TAB_CATEGORIES);
            Navigation.findNavController(v).navigate(R.id.searchFragment, args);
        });

        tvSeeAllCountries.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(SearchFragment.ARG_INITIAL_TAB, SearchFragment.TAB_COUNTRIES);
            Navigation.findNavController(v).navigate(R.id.searchFragment, args);
        });

        
        btnRetry.setOnClickListener(v -> {
            if (networkUtils.isNetworkAvailable()) {
                showOnlineState();
                presenter.loadAllData();
            } else {
                Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeNetworkStatus() {
        disposables.add(
                networkUtils.observeNetworkStatus()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isOnline -> {
                            if (isOnline) {
                                showOnlineState();
                                presenter.loadAllData();
                            } else {
                                showOfflineState();
                            }
                        }));
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
    public void showOfflineState() {
        if (offlineLayout != null && onlineContentLayout != null) {
            offlineLayout.setVisibility(View.VISIBLE);
            onlineContentLayout.setVisibility(View.GONE);
            if (lottieOffline != null) {
                lottieOffline.playAnimation();
            }
        }
    }

    @Override
    public void showOnlineState() {
        if (offlineLayout != null && onlineContentLayout != null) {
            offlineLayout.setVisibility(View.GONE);
            onlineContentLayout.setVisibility(View.VISIBLE);
            if (lottieOffline != null) {
                lottieOffline.pauseAnimation();
            }
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
        // Check if error is due to no network
        if (message != null && message.toLowerCase().contains("no internet")) {
            showOfflineState();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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
        Intent intent = MealsListActivity.createIntent(
                getContext(),
                MealsListActivity.FILTER_CATEGORY,
                categoryName);
        startActivity(intent);
    }

    @Override
    public void navigateToArea(String areaName) {
        Intent intent = MealsListActivity.createIntent(
                getContext(),
                MealsListActivity.FILTER_AREA,
                areaName);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        disposables.clear();
    }
}
