package com.example.foodplanner.presentation.favorites.view;

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
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.favorites.presenter.FavoritesPresenter;
import com.example.foodplanner.presentation.favorites.presenter.FavoritesPresenterImpl;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritesView {

    private ProgressBar progressBar;
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmpty;

    private FavoritesPresenter presenter;
    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        presenter = new FavoritesPresenterImpl(this, requireContext());
        presenter.loadFavorites();
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter();
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavorites.setAdapter(adapter);
        adapter.setOnFavoriteInteractionListener(new FavoritesAdapter.OnFavoriteInteractionListener() {
            @Override
            public void onMealClick(Meal meal) {
                presenter.onMealClicked(meal);
            }

            @Override
            public void onRemoveClick(Meal meal) {
                presenter.onRemoveFavorite(meal);
            }
        });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        rvFavorites.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        adapter.setMeals(meals);
    }

    @Override
    public void showEmpty() {
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
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
            Snackbar.make(getView(), mealName + " removed from favorites", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}
