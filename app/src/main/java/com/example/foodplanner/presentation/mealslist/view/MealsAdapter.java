package com.example.foodplanner.presentation.mealslist.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMealImage;
        private final TextView tvMealName;
        private final TextView tvMealInfo;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealInfo = itemView.findViewById(R.id.tvMealInfo);
        }

        void bind(Meal meal) {
            tvMealName.setText(meal.getName());
            
            String info = "";
            if (meal.getCategory() != null && !meal.getCategory().isEmpty()) {
                info = meal.getCategory();
            }
            if (meal.getArea() != null && !meal.getArea().isEmpty()) {
                if (!info.isEmpty()) info += " • ";
                info += meal.getArea();
            }
            tvMealInfo.setText(info);
            tvMealInfo.setVisibility(info.isEmpty() ? View.GONE : View.VISIBLE);

            Glide.with(itemView.getContext())
                    .load(meal.getThumbnail())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(ivMealImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });
        }
    }
}
