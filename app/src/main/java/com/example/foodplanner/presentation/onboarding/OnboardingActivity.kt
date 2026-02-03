package com.example.foodplanner.presentation.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.foodplanner.R
import com.example.foodplanner.presentation.auth.view.AuthActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var btnPrevious: MaterialButton
    private lateinit var tvSkip: TextView
    private lateinit var cardButtons: MaterialCardView
    private lateinit var onboardingAdapter: OnboardingAdapter
    private var indicators: MutableList<View> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        
        setContentView(R.layout.activity_onboarding)
        
        initViews()
        setupOnboardingItems()
        setupIndicators()
        setupListeners()
        animateInitialEntry()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        tvSkip = findViewById(R.id.tvSkip)
        cardButtons = findViewById(R.id.cardButtons)
    }

    private fun setupOnboardingItems() {
        // Create onboarding pages - customize these with your own content and Lottie files
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.onboarding_title_1),
                description = getString(R.string.onboarding_desc_1),
                lottieRes = R.raw.onboarding_food // Add your Lottie files
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_title_2),
                description = getString(R.string.onboarding_desc_2),
                lottieRes = R.raw.onboarding_plan // Add your Lottie files
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_title_3),
                description = getString(R.string.onboarding_desc_3),
                lottieRes = R.raw.onboarding_save // Add your Lottie files
            )
        )
        
        onboardingAdapter = OnboardingAdapter(items)
        viewPager.adapter = onboardingAdapter
        
        // Set custom page transformer for elegant transitions
        viewPager.setPageTransformer(OnboardingPageTransformer())
        
        // Disable over-scroll effect
        viewPager.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
    }

    private fun setupIndicators() {
        val itemCount = onboardingAdapter.itemCount
        indicatorContainer.removeAllViews()
        indicators.clear()

        for (i in 0 until itemCount) {
            val indicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.indicator_inactive_width),
                    resources.getDimensionPixelSize(R.dimen.indicator_height)
                ).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.indicator_margin)
                }
                background = ContextCompat.getDrawable(
                    this@OnboardingActivity,
                    if (i == 0) R.drawable.indicator_active else R.drawable.indicator_inactive
                )
            }
            indicators.add(indicator)
            indicatorContainer.addView(indicator)
        }
        
        // Animate the first indicator
        if (indicators.isNotEmpty()) {
            animateIndicator(0)
        }
    }

    private fun setupListeners() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateButtons(position)
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingAdapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        btnPrevious.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        tvSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in indicators.indices) {
            val indicator = indicators[i]
            val isActive = i == position
            
            indicator.background = ContextCompat.getDrawable(
                this,
                if (isActive) R.drawable.indicator_active else R.drawable.indicator_inactive
            )
            
            // Animate size change
            val targetWidth = resources.getDimensionPixelSize(
                if (isActive) R.dimen.indicator_active_width else R.dimen.indicator_inactive_width
            )
            
            ObjectAnimator.ofInt(indicator.layoutParams.width, targetWidth).apply {
                duration = 250
                addUpdateListener { animator ->
                    indicator.layoutParams.width = animator.animatedValue as Int
                    indicator.requestLayout()
                }
                start()
            }
        }
    }

    private fun animateIndicator(position: Int) {
        if (position < indicators.size) {
            val indicator = indicators[position]
            val scaleX = ObjectAnimator.ofFloat(indicator, View.SCALE_X, 0.5f, 1f)
            val scaleY = ObjectAnimator.ofFloat(indicator, View.SCALE_Y, 0.5f, 1f)
            
            AnimatorSet().apply {
                playTogether(scaleX, scaleY)
                duration = 300
                interpolator = OvershootInterpolator()
                start()
            }
        }
    }

    private fun updateButtons(position: Int) {
        val isLastPage = position == onboardingAdapter.itemCount - 1
        val isFirstPage = position == 0

        // Update Previous button visibility with animation
        btnPrevious.animate()
            .alpha(if (isFirstPage) 0f else 1f)
            .translationX(if (isFirstPage) -50f else 0f)
            .setDuration(250)
            .withStartAction {
                if (!isFirstPage) btnPrevious.visibility = View.VISIBLE
            }
            .withEndAction {
                if (isFirstPage) btnPrevious.visibility = View.INVISIBLE
            }
            .start()

        // Update Next button text and icon
        if (isLastPage) {
            btnNext.text = getString(R.string.get_started)
            btnNext.setIconResource(R.drawable.ic_arrow_forward)
            
            // Animate button expansion
            btnNext.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction {
                    btnNext.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
                .start()
        } else {
            btnNext.text = getString(R.string.next)
            btnNext.setIconResource(R.drawable.ic_arrow_forward)
        }

        // Update Skip button visibility
        tvSkip.animate()
            .alpha(if (isLastPage) 0f else 1f)
            .setDuration(200)
            .start()
    }

    private fun animateInitialEntry() {
        // Initial state - elements are invisible
        cardButtons.translationY = 200f
        cardButtons.alpha = 0f
        tvSkip.translationY = -50f
        tvSkip.alpha = 0f
        indicatorContainer.scaleX = 0f
        indicatorContainer.scaleY = 0f

        // Animate card buttons sliding up
        cardButtons.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Animate skip button sliding down
        tvSkip.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(400)
            .setStartDelay(400)
            .start()

        // Animate indicators scaling in
        indicatorContainer.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay(500)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun finishOnboarding() {
        // SharedPreferences to save onboarding completion status
        
        // Navigate to Auth screen
        startActivity(Intent(this, AuthActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }


    inner class OnboardingPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.apply {
                val absPosition = kotlin.math.abs(position)
                
                // Fade effect
                alpha = 1f - (absPosition * 0.5f)
                
                // Subtle scale effect
                val scale = 0.85f + (1f - absPosition) * 0.15f
                scaleX = scale
                scaleY = scale
                
                // Parallax effect for the animation container
                val animationView = findViewById<View>(R.id.cardAnimation)
                animationView?.translationX = position * width * 0.25f
                
                // Slide content up/down based on position
                val contentContainer = findViewById<View>(R.id.contentContainer)
                contentContainer?.translationY = absPosition * 100f
            }
        }
    }
}
