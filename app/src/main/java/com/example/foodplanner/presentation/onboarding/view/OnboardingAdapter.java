package com.example.foodplanner.presentation.onboarding.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.R;
import com.example.foodplanner.presentation.onboarding.model.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> items;

    public OnboardingAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<OnboardingItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_page, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private final LottieAnimationView lottieAnimation;
        private final TextView tvTitle;
        private final TextView tvDescription;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            lottieAnimation = itemView.findViewById(R.id.lottieAnimation);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(OnboardingItem item) {
            lottieAnimation.setAnimation(item.getLottieRes());
            lottieAnimation.playAnimation();
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
        }
    }
}
