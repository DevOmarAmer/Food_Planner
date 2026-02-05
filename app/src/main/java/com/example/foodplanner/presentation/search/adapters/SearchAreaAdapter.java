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
import com.example.foodplanner.data.model.Area;

import java.util.ArrayList;
import java.util.List;

public class SearchAreaAdapter extends RecyclerView.Adapter<SearchAreaAdapter.ViewHolder> {

    private List<Area> areas = new ArrayList<>();
    private OnAreaClickListener listener;

    public interface OnAreaClickListener {
        void onAreaClick(Area area);
    }

    public void setOnAreaClickListener(OnAreaClickListener listener) {
        this.listener = listener;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas != null ? areas : new ArrayList<>();
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
        Area area = areas.get(position);
        holder.bind(area);
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
        }

        void bind(Area area) {
            tvName.setText(area.getName());

            Glide.with(itemView.getContext())
                    .load(area.getFlagUrl())
                    .placeholder(R.drawable.placeholder_flag)
                    .error(R.drawable.placeholder_flag)
                    .centerCrop()
                    .into(ivImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAreaClick(area);
                }
            });
        }
    }
}
