package com.example.foodplanner.presentation.home.adapters;

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
import com.example.foodplanner.data.model.Area;

import java.util.ArrayList;
import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {
    
    private List<Area> areas = new ArrayList<>();
    private OnAreaClickListener listener;
    
    public interface OnAreaClickListener {
        void onAreaClick(Area area);
    }
    
    public void setOnAreaClickListener(OnAreaClickListener listener) {
        this.listener = listener;
    }
    
    public void setAreas(List<Area> areas) {
        this.areas = areas;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area, parent, false);
        return new AreaViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        Area area = areas.get(position);
        holder.bind(area);
    }
    
    @Override
    public int getItemCount() {
        return areas.size();
    }
    
    class AreaViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView ivFlag;
        private final TextView tvAreaName;
        
        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlag = itemView.findViewById(R.id.ivFlag);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAreaClick(areas.get(position));
                }
            });
        }
        
        public void bind(Area area) {
            tvAreaName.setText(area.getName());
            String flagUrl = area.getFlagUrl();
            Glide.with(itemView.getContext())
                    .load(flagUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_flag)
                    .error(R.drawable.placeholder_flag)
                    .into(ivFlag);
        }
    }
}
