package com.example.foodplanner.presentation.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.PlannedMeal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_MEAL = 1;
    
    private static final String[] DAYS_ORDER = {
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };
    
    private final List<Object> items = new ArrayList<>();
    private final OnPlanMealClickListener listener;
    
    public interface OnPlanMealClickListener {
        void onMealClick(PlannedMeal plannedMeal);
        void onRemoveClick(PlannedMeal plannedMeal);
    }
    
    public PlanAdapter(OnPlanMealClickListener listener) {
        this.listener = listener;
    }
    
    public void setPlannedMeals(List<PlannedMeal> plannedMeals) {
        items.clear();
        
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
                items.add(day); // Header
                items.addAll(dayMeals);
            }
        }
        
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_MEAL;
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
            ((DayHeaderViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof PlannedMealViewHolder) {
            ((PlannedMealViewHolder) holder).bind((PlannedMeal) items.get(position));
        }
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    // Day Header ViewHolder
    static class DayHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDayName;
        
        public DayHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
        }
        
        public void bind(String day) {
            tvDayName.setText(day);
        }
    }
    
    // Planned Meal ViewHolder
    class PlannedMealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMealImage;
        private final TextView tvMealName;
        private final TextView tvMealInfo;
        private final ImageButton btnRemove;
        
        public PlannedMealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealInfo = itemView.findViewById(R.id.tvMealInfo);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
        
        public void bind(PlannedMeal plannedMeal) {
            tvMealName.setText(plannedMeal.getMealName());
            
            String info = "";
            if (plannedMeal.getCategory() != null && !plannedMeal.getCategory().isEmpty()) {
                info = plannedMeal.getCategory();
            }
            if (plannedMeal.getArea() != null && !plannedMeal.getArea().isEmpty()) {
                if (!info.isEmpty()) {
                    info += " • ";
                }
                info += plannedMeal.getArea();
            }
            tvMealInfo.setText(info);
            
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
