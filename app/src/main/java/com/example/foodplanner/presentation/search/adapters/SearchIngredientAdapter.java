package com.example.foodplanner.presentation.search.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class SearchIngredientAdapter extends RecyclerView.Adapter<SearchIngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public void setOnIngredientClickListener(OnIngredientClickListener listener) {
        this.listener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
        }

        void bind(Ingredient ingredient) {
            tvName.setText(ingredient.getName());

            Glide.with(itemView.getContext())
                    .load(ingredient.getThumbnail())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(ivImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
        }
    }
}
