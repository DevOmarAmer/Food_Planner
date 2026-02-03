package com.example.foodplanner.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplanner.R

/**
 * Adapter for the onboarding ViewPager2.
 * Displays onboarding pages with Lottie animations, titles, and descriptions.
 */
class OnboardingAdapter(
    private val items: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_page, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val lottieAnimation: LottieAnimationView = itemView.findViewById(R.id.lottieAnimation)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

        fun bind(item: OnboardingItem) {
            // Set Lottie animation
            lottieAnimation.setAnimation(item.lottieRes)
            lottieAnimation.playAnimation()
            
            // Set text content
            tvTitle.text = item.title
            tvDescription.text = item.description
        }
    }
}
