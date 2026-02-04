package com.example.foodplanner.presentation.mealdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<IngredientItem> ingredients = new ArrayList<>();
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(String ingredientName);
    }

    public void setOnIngredientClickListener(OnIngredientClickListener listener) {
        this.listener = listener;
    }

    public void setIngredients(List<IngredientItem> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        IngredientItem item = ingredients.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIngredient;
        private final TextView tvIngredientName;
        private final TextView tvIngredientMeasure;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIngredient = itemView.findViewById(R.id.ivIngredient);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvIngredientMeasure = itemView.findViewById(R.id.tvIngredientMeasure);
        }

        void bind(IngredientItem item) {
            tvIngredientName.setText(item.getName());
            tvIngredientMeasure.setText(item.getMeasure());

            String imageUrl = "https://www.themealdb.com/images/ingredients/" + item.getName() + "-Small.png";
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .into(ivIngredient);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(item.getName());
                }
            });
        }
    }

    public static class IngredientItem {
        private final String name;
        private final String measure;

        public IngredientItem(String name, String measure) {
            this.name = name;
            this.measure = measure;
        }

        public String getName() {
            return name;
        }

        public String getMeasure() {
            return measure;
        }
    }
}
