package com.example.foodplanner.presentation.plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.PlannedMeal;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.presentation.mealdetails.MealDetailsActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlanFragment extends Fragment implements PlanView, PlanAdapter.OnPlanMealClickListener {

    private RecyclerView rvPlan;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private LinearLayout layoutGuestPrompt;
    private MaterialButton btnSignIn;
    private MaterialButton btnBrowseMeals;

    // Week navigation
    private LinearLayout layoutWeekNav;
    private ImageButton btnPrevWeek;
    private ImageButton btnNextWeek;
    private TextView tvWeekRange;

    // Day selector
    private ChipGroup chipGroupDays;
    private Chip chipSaturday, chipSunday, chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday;

    private PlanPresenter presenter;
    private PlanAdapter adapter;
    private SharedPrefsHelper sharedPrefsHelper;

    private long currentWeekStart;
    private int selectedDayIndex = -1; // -1 means show all days

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

        // Initialize week to current week
        currentWeekStart = getWeekStartDate(System.currentTimeMillis());

        initViews(view);
        setupRecyclerView();
        setupWeekNavigation();
        setupDayChips();

        if (sharedPrefsHelper.isGuest()) {
            showGuestPrompt();
        } else {
            presenter = new PlanPresenterImpl(this, requireContext());
            presenter.loadPlanForWeek(currentWeekStart);
        }
    }

    private void initViews(View view) {
        rvPlan = view.findViewById(R.id.rvPlan);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutGuestPrompt = view.findViewById(R.id.layoutGuestPrompt);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnBrowseMeals = view.findViewById(R.id.btnBrowseMeals);

        // Week navigation
        layoutWeekNav = view.findViewById(R.id.layoutWeekNav);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
        tvWeekRange = view.findViewById(R.id.tvWeekRange);

        // Day selector chips
        chipGroupDays = view.findViewById(R.id.chipGroupDays);
        chipSaturday = view.findViewById(R.id.chipSaturday);
        chipSunday = view.findViewById(R.id.chipSunday);
        chipMonday = view.findViewById(R.id.chipMonday);
        chipTuesday = view.findViewById(R.id.chipTuesday);
        chipWednesday = view.findViewById(R.id.chipWednesday);
        chipThursday = view.findViewById(R.id.chipThursday);
        chipFriday = view.findViewById(R.id.chipFriday);

        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> navigateToAuth());
        }

        if (btnBrowseMeals != null) {
            btnBrowseMeals.setOnClickListener(v -> navigateToSearch());
        }
    }

    private void setupWeekNavigation() {
        updateWeekRangeText();

        btnPrevWeek.setOnClickListener(v -> {
            currentWeekStart -= 7L * 24 * 60 * 60 * 1000; // Subtract 7 days
            updateWeekRangeText();
            if (presenter != null) {
                presenter.loadPlanForWeek(currentWeekStart);
            }
            adapter.setWeekStartDate(currentWeekStart);
        });

        btnNextWeek.setOnClickListener(v -> {
            currentWeekStart += 7L * 24 * 60 * 60 * 1000; // Add 7 days
            updateWeekRangeText();
            if (presenter != null) {
                presenter.loadPlanForWeek(currentWeekStart);
            }
            adapter.setWeekStartDate(currentWeekStart);
        });
    }

    private void updateWeekRangeText() {
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(currentWeekStart);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DAY_OF_MONTH, 6);

        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        String startStr = monthDayFormat.format(startCal.getTime());
        String endStr = monthDayFormat.format(endCal.getTime());

        tvWeekRange.setText(startStr + " - " + endStr);
    }

    private void setupDayChips() {
        // Initially select today's chip
        selectTodayChip();

        Chip[] chips = { chipSaturday, chipSunday, chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday };

        for (int i = 0; i < chips.length; i++) {
            final int dayIndex = i;
            chips[i].setOnClickListener(v -> {
                selectedDayIndex = dayIndex;
                scrollToDayPosition(dayIndex);
            });
        }
    }

    private void selectTodayChip() {
        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        Chip todayChip = null;
        switch (dayOfWeek) {
            case Calendar.SATURDAY:
                todayChip = chipSaturday;
                break;
            case Calendar.SUNDAY:
                todayChip = chipSunday;
                break;
            case Calendar.MONDAY:
                todayChip = chipMonday;
                break;
            case Calendar.TUESDAY:
                todayChip = chipTuesday;
                break;
            case Calendar.WEDNESDAY:
                todayChip = chipWednesday;
                break;
            case Calendar.THURSDAY:
                todayChip = chipThursday;
                break;
            case Calendar.FRIDAY:
                todayChip = chipFriday;
                break;
        }

        if (todayChip != null) {
            todayChip.setChecked(true);
        }
    }

    private void scrollToDayPosition(int dayIndex) {
        // The adapter has alternating headers and content (meal or empty)
        // Each day takes 2 positions: 1 header + 1 or more meals/empty
        // For simplicity, we calculate position as dayIndex * 2 (header position)
        // But we need to properly count items in adapter

        // Find the position of the header for the selected day
        int targetPosition = 0;
        for (int i = 0; i < dayIndex; i++) {
            // Each day has 1 header + at least 1 item (meal or empty)
            targetPosition += 2; // Minimum: header + one item
        }

        // This is a simplified approach - just scroll to approximate position
        // In a more robust implementation, we'd track header positions
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvPlan.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(targetPosition, 0);
        }
    }

    private long getWeekStartDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // Set to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Find Saturday (start of week)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract;
        if (dayOfWeek == Calendar.SATURDAY) {
            daysToSubtract = 0;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            daysToSubtract = 1;
        } else {
            daysToSubtract = dayOfWeek - Calendar.SATURDAY + 7;
        }

        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        return calendar.getTimeInMillis();
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

    private void navigateToSearch() {
        try {
            Navigation.findNavController(requireView()).navigate(R.id.searchFragment);
        } catch (Exception e) {
            // Fallback if navigation fails
            Toast.makeText(getContext(), "Navigate to search to add meals", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new PlanAdapter(this);
        adapter.setWeekStartDate(currentWeekStart);
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
        // With the new design, we still show the RecyclerView with empty placeholders
        // so this method now just shows the plan with an empty list
        rvPlan.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutGuestPrompt.setVisibility(View.GONE);
        adapter.setPlannedMeals(new java.util.ArrayList<>());
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
        // Show confirmation dialog before deleting
        showDeleteConfirmationDialog(plannedMeal);
    }

    private void showDeleteConfirmationDialog(PlannedMeal plannedMeal) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_meal_title)
                .setMessage(getString(R.string.delete_meal_message, plannedMeal.getMealName()))
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    presenter.onRemoveMeal(plannedMeal);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onAddMealClick(String day, long dateTimestamp) {
        // Navigate to search screen to add a meal
        navigateToSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
