package com.example.foodplanner.presentation.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.PlannedMeal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_MEAL = 1;

    private static final String[] DAYS_ORDER = {
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    private final List<Object> items = new ArrayList<>();
    private final OnPlanMealClickListener listener;
    private String todayName;

    public interface OnPlanMealClickListener {
        void onMealClick(PlannedMeal plannedMeal);

        void onRemoveClick(PlannedMeal plannedMeal);
    }

    public PlanAdapter(OnPlanMealClickListener listener) {
        this.listener = listener;
        updateTodayName();
    }

    private void updateTodayName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        todayName = dayFormat.format(calendar.getTime());
    }

    public void setPlannedMeals(List<PlannedMeal> plannedMeals) {
        items.clear();
        updateTodayName();

        // Group meals by day
        Map<String, List<PlannedMeal>> mealsByDay = new LinkedHashMap<>();

        // Initialize with ordered days
        for (String day : DAYS_ORDER) {
            mealsByDay.put(day, new ArrayList<>());
        }

        // Populate meals
        for (PlannedMeal meal : plannedMeals) {
            List<PlannedMeal> dayMeals = mealsByDay.get(meal.getDay());
            if (dayMeals != null) {
                dayMeals.add(meal);
            }
        }

        // Build items list with headers and meals
        for (String day : DAYS_ORDER) {
            List<PlannedMeal> dayMeals = mealsByDay.get(day);
            if (dayMeals != null && !dayMeals.isEmpty()) {
                // Create header with meal count
                DayHeader header = new DayHeader(day, dayMeals.size(), day.equals(todayName));
                items.add(header);
                items.addAll(dayMeals);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof DayHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_MEAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_day_header, parent, false);
            return new DayHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_planned_meal, parent, false);
            return new PlannedMealViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DayHeaderViewHolder) {
            ((DayHeaderViewHolder) holder).bind((DayHeader) items.get(position));
        } else if (holder instanceof PlannedMealViewHolder) {
            ((PlannedMealViewHolder) holder).bind((PlannedMeal) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Day Header data class
    static class DayHeader {
        String dayName;
        int mealCount;
        boolean isToday;

        DayHeader(String dayName, int mealCount, boolean isToday) {
            this.dayName = dayName;
            this.mealCount = mealCount;
            this.isToday = isToday;
        }
    }

    // Day Header ViewHolder
    class DayHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDayName;
        private final TextView tvDate;
        private final TextView tvMealCount;
        private final Chip chipToday;
        private final CardView cardMealCount;

        public DayHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMealCount = itemView.findViewById(R.id.tvMealCount);
            chipToday = itemView.findViewById(R.id.chipToday);
            cardMealCount = itemView.findViewById(R.id.cardMealCount);
        }

        public void bind(DayHeader header) {
            tvDayName.setText(header.dayName);

            // Calculate date for this day
            Calendar cal = Calendar.getInstance();
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int targetDay = getDayOfWeek(header.dayName);
            int diff = targetDay - todayDayOfWeek;
            cal.add(Calendar.DAY_OF_MONTH, diff);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.getDefault());
            tvDate.setText(dateFormat.format(cal.getTime()));

            // Meal count
            if (header.mealCount == 1) {
                tvMealCount.setText(R.string.one_meal);
            } else {
                tvMealCount.setText(itemView.getContext().getString(R.string.meals_count, header.mealCount));
            }

            // Today badge
            chipToday.setVisibility(header.isToday ? View.VISIBLE : View.GONE);
        }

        private int getDayOfWeek(String dayName) {
            switch (dayName) {
                case "Sunday":
                    return Calendar.SUNDAY;
                case "Monday":
                    return Calendar.MONDAY;
                case "Tuesday":
                    return Calendar.TUESDAY;
                case "Wednesday":
                    return Calendar.WEDNESDAY;
                case "Thursday":
                    return Calendar.THURSDAY;
                case "Friday":
                    return Calendar.FRIDAY;
                case "Saturday":
                    return Calendar.SATURDAY;
                default:
                    return Calendar.SUNDAY;
            }
        }
    }

    // Planned Meal ViewHolder
    class PlannedMealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMealImage;
        private final TextView tvMealName;
        private final TextView tvMealArea;
        private final Chip chipCategory;
        private final MaterialButton btnRemove;

        public PlannedMealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealArea = itemView.findViewById(R.id.tvMealArea);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(PlannedMeal plannedMeal) {
            tvMealName.setText(plannedMeal.getMealName());

            // Category chip
            if (plannedMeal.getCategory() != null && !plannedMeal.getCategory().isEmpty()) {
                chipCategory.setVisibility(View.VISIBLE);
                chipCategory.setText(plannedMeal.getCategory());
            } else {
                chipCategory.setVisibility(View.GONE);
            }

            // Area
            if (plannedMeal.getArea() != null && !plannedMeal.getArea().isEmpty()) {
                tvMealArea.setVisibility(View.VISIBLE);
                tvMealArea.setText(plannedMeal.getArea());
            } else {
                tvMealArea.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(plannedMeal.getMealThumb())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_food)
                    .into(ivMealImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(plannedMeal);
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(plannedMeal);
                }
            });
        }
    }
}
