package com.example.foodplanner.presentation.favorites.view;

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
import com.example.foodplanner.data.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private OnFavoriteInteractionListener listener;

    public interface OnFavoriteInteractionListener {
        void onMealClick(Meal meal);
        void onRemoveClick(Meal meal);
    }

    public void setOnFavoriteInteractionListener(OnFavoriteInteractionListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_meal, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMealImage;
        private final TextView tvMealName;
        private final TextView tvMealInfo;
        private final ImageButton btnRemove;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealInfo = itemView.findViewById(R.id.tvMealInfo);
            btnRemove = itemView.findViewById(R.id.btnRemove);
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

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(meal);
                }
            });
        }
    }
}
