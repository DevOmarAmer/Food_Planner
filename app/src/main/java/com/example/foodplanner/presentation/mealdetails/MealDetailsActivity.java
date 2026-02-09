package com.example.foodplanner.presentation.mealdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
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
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.utils.CalendarHelper;
import com.example.foodplanner.utils.ImageCacheHelper;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
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
    private FloatingActionButton fabAddToCalendar;
    private ExtendedFloatingActionButton fabAddToPlan;
    private View cardVideo;
    private TextView tvVideoTitle;
    private WebView youtubeWebView;
    private Toolbar toolbar;

    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 100;

    private Meal currentMeal;
    private boolean isFavorite = false;
    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);

        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);

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
        fabAddToCalendar = findViewById(R.id.fabAddToCalendar);
        cardVideo = findViewById(R.id.cardVideo);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        youtubeWebView = findViewById(R.id.youtubeWebView);
        toolbar = findViewById(R.id.toolbar);

        setupWebView();
    }

    private void setupWebView() {
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Important user agent for YouTube compatibility
        webSettings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                        "Chrome/120.0.0.0 Mobile Safari/537.36");

        youtubeWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        youtubeWebView.setWebChromeClient(new WebChromeClient());

        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("YouTubeWebView", "WebView error, fallback to YouTube app");
                openYoutubeExternally();
            }
        });
    }

    private void loadYouTubeVideo(String videoId) {
        if (videoId == null || videoId.isEmpty()) {
            cardVideo.setVisibility(View.GONE);
            tvVideoTitle.setVisibility(View.GONE);
            return;
        }

        cardVideo.setVisibility(View.VISIBLE);
        tvVideoTitle.setVisibility(View.VISIBLE);

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "html, body { margin: 0; padding: 0; background: black; width: 100%; height: 100%; }" +
                "iframe { width: 100%; height: 100%; border: 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<iframe src='https://www.youtube-nocookie.com/embed/" + videoId +
                "?playsinline=1&rel=0' " +
                "allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share' "
                +
                "allowfullscreen></iframe>" +
                "</body>" +
                "</html>";

        youtubeWebView.loadDataWithBaseURL(
                "https://www.youtube-nocookie.com",
                html,
                "text/html",
                "UTF-8",
                null);
    }

    private void openYoutubeExternally() {
        if (currentMeal != null && currentMeal.getYoutubeUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentMeal.getYoutubeUrl()));
            startActivity(intent);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
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
            if (sharedPrefsHelper.isGuest()) {
                showGuestSignInDialog();
                return;
            }
            if (currentMeal != null) {
                presenter.toggleFavorite(currentMeal);
            }
        });

        fabAddToPlan.setOnClickListener(v -> {
            if (sharedPrefsHelper.isGuest()) {
                showGuestSignInDialog();
                return;
            }
            if (currentMeal != null) {
                showDayPickerDialog();
            }
        });

        fabAddToCalendar.setOnClickListener(v -> {
            if (sharedPrefsHelper.isGuest()) {
                showGuestSignInDialog();
                return;
            }
            if (currentMeal != null) {
                showCalendarDatePicker();
            }
        });
    }

    private void showCalendarDatePicker() {
        if (!CalendarHelper.hasCalendarPermission(this)) {
            requestCalendarPermission();
            return;
        }

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            boolean success = CalendarHelper.addMealToCalendar(this, currentMeal, year, month, day);

            if (success) {
                Toast.makeText(this, R.string.meal_added_to_calendar, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add to calendar", Toast.LENGTH_SHORT).show();
            }
        });

        datePicker.show(getSupportFragmentManager(), "date_picker");
    }

    private void requestCalendarPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[] {
                            android.Manifest.permission.READ_CALENDAR,
                            android.Manifest.permission.WRITE_CALENDAR
                    },
                    CALENDAR_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show date picker
                showCalendarDatePicker();
            } else {
                Toast.makeText(this, R.string.calendar_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showGuestSignInDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sign_in_required)
                .setMessage(R.string.sign_in_to_use_feature)
                .setPositiveButton(R.string.sign_in, (dialog, which) -> {
                    Intent intent = new Intent(this, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDayPickerDialog() {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        com.google.android.material.datepicker.CalendarConstraints.Builder constraintsBuilder = new com.google.android.material.datepicker.CalendarConstraints.Builder();
        constraintsBuilder.setStart(today.getTimeInMillis());
        constraintsBuilder.setValidator(com.google.android.material.datepicker.DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date_for_meal)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);

            // Set to midnight for the planned date timestamp
            selectedDate.set(Calendar.HOUR_OF_DAY, 0);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEEE", java.util.Locale.ENGLISH);
            String dayName = dayFormat.format(selectedDate.getTime());

            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMMM d, yyyy",
                    java.util.Locale.getDefault());
            String displayDate = displayFormat.format(selectedDate.getTime());

            presenter.addToPlanWithDate(currentMeal, dayName, selectedDate.getTimeInMillis());

            Toast.makeText(this,
                    getString(R.string.meal_added_to_plan) + " (" + displayDate + ")",
                    Toast.LENGTH_SHORT).show();
        });

        datePicker.show(getSupportFragmentManager(), "meal_date_picker");
    }

    private void initPresenter() {
        presenter = new MealDetailsPresenter(this, MealRepository.getInstance(this));
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
            String videoId = meal.getYoutubeVideoId();
            Log.d("YouTubePlayer", "YouTube URL: " + meal.getYoutubeUrl());
            Log.d("YouTubePlayer", "Extracted Video ID: " + videoId);
            if (videoId != null && !videoId.isEmpty()) {
                loadYouTubeVideo(videoId);
            } else {
                tvVideoTitle.setVisibility(View.GONE);
                cardVideo.setVisibility(View.GONE);
            }
        } else {
            tvVideoTitle.setVisibility(View.GONE);
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
        // Pre-cache the meal image for offline viewing
        if (currentMeal != null) {
            ImageCacheHelper.preloadMealImage(this, currentMeal);
        }
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
        // Pre-cache the meal image for offline viewing
        if (currentMeal != null) {
            ImageCacheHelper.preloadMealImage(this, currentMeal);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (youtubeWebView != null) {
            youtubeWebView.onPause();
            youtubeWebView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (youtubeWebView != null) {
            youtubeWebView.onResume();
            youtubeWebView.resumeTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if (youtubeWebView != null) {
            youtubeWebView.destroy();
        }
        super.onDestroy();
        presenter.onDestroy();
    }
}
