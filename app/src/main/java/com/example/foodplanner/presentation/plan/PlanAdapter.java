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
    private static final int VIEW_TYPE_EMPTY = 2;

    private static final String[] DAYS_ORDER = {
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    private final List<Object> items = new ArrayList<>();
    private final OnPlanMealClickListener listener;
    private String todayName;
    private long weekStartDate;

    public interface OnPlanMealClickListener {
        void onMealClick(PlannedMeal plannedMeal);

        void onRemoveClick(PlannedMeal plannedMeal);

        void onAddMealClick(String day, long dateTimestamp);
    }

    public PlanAdapter(OnPlanMealClickListener listener) {
        this.listener = listener;
        updateTodayName();
        this.weekStartDate = System.currentTimeMillis();
    }

    private void updateTodayName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        todayName = dayFormat.format(calendar.getTime());
    }

    public void setWeekStartDate(long weekStartDate) {
        this.weekStartDate = weekStartDate;
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

        // Build items list with headers and meals (always show all days)
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(weekStartDate);

        for (int i = 0; i < DAYS_ORDER.length; i++) {
            String day = DAYS_ORDER[i];
            List<PlannedMeal> dayMeals = mealsByDay.get(day);

            // Calculate the date for this day
            Calendar dayCal = (Calendar) cal.clone();
            dayCal.add(Calendar.DAY_OF_MONTH, i);
            long dateTimestamp = dayCal.getTimeInMillis();

            // Check if this day is today
            boolean isToday = day.equals(todayName) && isCurrentWeek();

            int mealCount = dayMeals != null ? dayMeals.size() : 0;
            DayHeader header = new DayHeader(day, mealCount, isToday, dateTimestamp);
            items.add(header);

            if (dayMeals != null && !dayMeals.isEmpty()) {
                items.addAll(dayMeals);
            } else {
                // Add empty placeholder
                items.add(new EmptyMealPlaceholder(day, dateTimestamp));
            }
        }

        notifyDataSetChanged();
    }

    private boolean isCurrentWeek() {
        Calendar now = Calendar.getInstance();
        Calendar weekStart = Calendar.getInstance();
        weekStart.setTimeInMillis(weekStartDate);

        // Check if weekStartDate is within the current week
        long diff = Math.abs(now.getTimeInMillis() - weekStart.getTimeInMillis());
        return diff < 7L * 24 * 60 * 60 * 1000;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof DayHeader) {
            return VIEW_TYPE_HEADER;
        } else if (item instanceof EmptyMealPlaceholder) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_MEAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_day_header, parent, false);
            return new DayHeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_EMPTY) {
            View view = inflater.inflate(R.layout.item_empty_meal, parent, false);
            return new EmptyMealViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_planned_meal, parent, false);
            return new PlannedMealViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DayHeaderViewHolder) {
            ((DayHeaderViewHolder) holder).bind((DayHeader) items.get(position));
        } else if (holder instanceof EmptyMealViewHolder) {
            ((EmptyMealViewHolder) holder).bind((EmptyMealPlaceholder) items.get(position));
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
        long dateTimestamp;

        DayHeader(String dayName, int mealCount, boolean isToday, long dateTimestamp) {
            this.dayName = dayName;
            this.mealCount = mealCount;
            this.isToday = isToday;
            this.dateTimestamp = dateTimestamp;
        }
    }

    // Empty meal placeholder data class
    static class EmptyMealPlaceholder {
        String dayName;
        long dateTimestamp;

        EmptyMealPlaceholder(String dayName, long dateTimestamp) {
            this.dayName = dayName;
            this.dateTimestamp = dateTimestamp;
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

            // Format the date from the header's timestamp
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(header.dateTimestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.getDefault());
            tvDate.setText(dateFormat.format(cal.getTime()));

            // Meal count
            if (header.mealCount == 0) {
                cardMealCount.setVisibility(View.GONE);
            } else {
                cardMealCount.setVisibility(View.VISIBLE);
                if (header.mealCount == 1) {
                    tvMealCount.setText(R.string.one_meal);
                } else {
                    tvMealCount.setText(itemView.getContext().getString(R.string.meals_count, header.mealCount));
                }
            }

            // Today badge
            chipToday.setVisibility(header.isToday ? View.VISIBLE : View.GONE);
        }
    }

    // Empty Meal ViewHolder
    class EmptyMealViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton btnAddMeal;

        public EmptyMealViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAddMeal = itemView.findViewById(R.id.btnAddMeal);
        }

        public void bind(EmptyMealPlaceholder placeholder) {
            btnAddMeal.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddMealClick(placeholder.dayName, placeholder.dateTimestamp);
                }
            });
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
