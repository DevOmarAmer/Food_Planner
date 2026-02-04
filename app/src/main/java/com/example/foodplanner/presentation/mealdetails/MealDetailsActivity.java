package com.example.foodplanner.presentation.mealdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.repository.MealRepository;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsActivity extends AppCompatActivity implements MealDetailsContract.View {

    public static final String EXTRA_MEAL_ID = "meal_id";
    public static final String EXTRA_MEAL_NAME = "meal_name";

    private MealDetailsPresenter presenter;
    private IngredientAdapter ingredientAdapter;

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivMealImage;
    private TextView tvMealName;
    private TextView tvMealInfo;
    private TextView tvInstructions;
    private RecyclerView rvIngredients;
    private FloatingActionButton fabFavorite;
    private ExtendedFloatingActionButton fabAddToPlan;
    private MaterialCardView cardVideo;
    private ImageView ivVideoThumbnail;
    private ImageView ivPlayButton;
    private Toolbar toolbar;

    private Meal currentMeal;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        initPresenter();

        String mealId = getIntent().getStringExtra(EXTRA_MEAL_ID);
        String mealName = getIntent().getStringExtra(EXTRA_MEAL_NAME);

        if (mealName != null) {
            collapsingToolbar.setTitle(mealName);
        }

        if (mealId != null) {
            presenter.getMealDetails(mealId);
        } else {
            Toast.makeText(this, "Error loading meal", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        ivMealImage = findViewById(R.id.ivMealImage);
        tvMealName = findViewById(R.id.tvMealName);
        tvMealInfo = findViewById(R.id.tvMealInfo);
        tvInstructions = findViewById(R.id.tvInstructions);
        rvIngredients = findViewById(R.id.rvIngredients);
        fabFavorite = findViewById(R.id.fabFavorite);
        fabAddToPlan = findViewById(R.id.fabAddToPlan);
        cardVideo = findViewById(R.id.cardVideo);
        ivVideoThumbnail = findViewById(R.id.ivVideoThumbnail);
        ivPlayButton = findViewById(R.id.ivPlayButton);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        ingredientAdapter = new IngredientAdapter();
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(ingredientAdapter);
        ingredientAdapter.setOnIngredientClickListener(ingredientName -> {
            Toast.makeText(this, "Search by: " + ingredientName, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        fabFavorite.setOnClickListener(v -> {
            if (currentMeal != null) {
                presenter.toggleFavorite(currentMeal);
            }
        });

        fabAddToPlan.setOnClickListener(v -> {
            if (currentMeal != null) {
                showDayPickerDialog();
            }
        });

        cardVideo.setOnClickListener(v -> {
            if (currentMeal != null && currentMeal.getYoutubeUrl() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentMeal.getYoutubeUrl()));
                startActivity(intent);
            }
        });
    }

    private void showDayPickerDialog() {
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Add to which day?")
                .setItems(days, (dialog, which) -> {
                    presenter.addToPlan(currentMeal, days[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void initPresenter() {
        presenter = new MealDetailsPresenter(this, MealRepository.getInstance());
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showMeal(Meal meal) {
        currentMeal = meal;

        collapsingToolbar.setTitle(meal.getName());
        tvMealName.setText(meal.getName());
        tvMealInfo.setText(meal.getCategory() + " • " + meal.getArea());

        Glide.with(this)
                .load(meal.getThumbnail())
                .placeholder(R.drawable.placeholder_food)
                .into(ivMealImage);

        tvInstructions.setText(meal.getInstructions());

        List<String> ingredients = meal.getIngredientsList();
        List<String> measures = meal.getMeasuresList();
        List<IngredientAdapter.IngredientItem> ingredientItems = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            String measure = i < measures.size() ? measures.get(i) : "";
            ingredientItems.add(new IngredientAdapter.IngredientItem(ingredients.get(i), measure));
        }

        ingredientAdapter.setIngredients(ingredientItems);

        if (meal.getYoutubeUrl() != null && !meal.getYoutubeUrl().isEmpty()) {
            cardVideo.setVisibility(View.VISIBLE);
            String videoId = meal.getYoutubeVideoId();
            if (videoId != null) {
                String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                Glide.with(this)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .into(ivVideoThumbnail);
            }
        } else {
            cardVideo.setVisibility(View.GONE);
        }

        presenter.checkFavoriteStatus(meal.getId());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAddedToFavorites() {
        isFavorite = true;
        fabFavorite.setImageResource(R.drawable.ic_favorite);
        Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRemovedFromFavorites() {
        isFavorite = false;
        fabFavorite.setImageResource(R.drawable.ic_favorite_border);
        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFavoriteStatus(boolean isFavorite) {
        this.isFavorite = isFavorite;
        fabFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public void showAddedToPlan(String day) {
        Toast.makeText(this, "Added to " + day + "'s plan!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
