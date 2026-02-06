package com.example.foodplanner.presentation.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.presentation.onboarding.model.OnboardingItem;
import com.example.foodplanner.presentation.onboarding.presenter.OnboardingPresenter;
import com.example.foodplanner.presentation.onboarding.presenter.OnboardingPresenterImpl;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity implements OnboardingView {

    private ViewPager2 viewPager;
    private LinearLayout indicatorContainer;
    private MaterialButton btnNext;
    private MaterialButton btnPrevious;
    private TextView tvSkip;
    private MaterialCardView cardButtons;

    private OnboardingAdapter adapter;
    private OnboardingPresenter presenter;
    private List<View> indicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initPresenter();
        setupListeners();
        presenter.loadOnboardingData();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding);
        indicatorContainer = findViewById(R.id.indicatorContainer);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        tvSkip = findViewById(R.id.tvSkip);
        cardButtons = findViewById(R.id.cardButtons);

        adapter = new OnboardingAdapter();
        indicators = new ArrayList<>();

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new OnboardingPageTransformer());
        View child = viewPager.getChildAt(0);
        if (child != null) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    private void initPresenter() {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        presenter = new OnboardingPresenterImpl(this, sharedPrefsHelper);
    }

    private void setupListeners() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                presenter.onPageSelected(position);
            }
        });

        btnNext.setOnClickListener(v -> presenter.onNextClicked(viewPager.getCurrentItem()));
        btnPrevious.setOnClickListener(v -> presenter.onPreviousClicked(viewPager.getCurrentItem()));
        tvSkip.setOnClickListener(v -> presenter.onSkipClicked());
    }

    @Override
    public void showOnboardingPages(List<OnboardingItem> items) {
        adapter.setItems(items);
        setupIndicators(items.size());
    }

    private void setupIndicators(int count) {
        indicatorContainer.removeAllViews();
        indicators.clear();

        int activeWidth = getResources().getDimensionPixelSize(R.dimen.indicator_active_width);
        int inactiveWidth = getResources().getDimensionPixelSize(R.dimen.indicator_inactive_width);
        int height = getResources().getDimensionPixelSize(R.dimen.indicator_height);
        int margin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);

        for (int i = 0; i < count; i++) {
            View indicator = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == 0 ? activeWidth : inactiveWidth,
                    height
            );
            params.setMarginEnd(margin);
            indicator.setLayoutParams(params);
            indicator.setBackground(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.indicator_active : R.drawable.indicator_inactive));
            indicators.add(indicator);
            indicatorContainer.addView(indicator);
        }
    }

    @Override
    public void updateIndicators(int position, int totalCount) {
        int activeWidth = getResources().getDimensionPixelSize(R.dimen.indicator_active_width);
        int inactiveWidth = getResources().getDimensionPixelSize(R.dimen.indicator_inactive_width);

        for (int i = 0; i < indicators.size(); i++) {
            View indicator = indicators.get(i);
            boolean isActive = i == position;

            indicator.setBackground(ContextCompat.getDrawable(this,
                    isActive ? R.drawable.indicator_active : R.drawable.indicator_inactive));

            int targetWidth = isActive ? activeWidth : inactiveWidth;
            android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(indicator.getLayoutParams().width, targetWidth);
            animator.setDuration(250);
            animator.addUpdateListener(animation -> {
                indicator.getLayoutParams().width = (int) animation.getAnimatedValue();
                indicator.requestLayout();
            });
            animator.start();
        }
    }

    @Override
    public void updateButtons(int position, int totalCount) {
        boolean isLastPage = position == totalCount - 1;
        boolean isFirstPage = position == 0;

        btnPrevious.animate()
                .alpha(isFirstPage ? 0f : 1f)
                .translationX(isFirstPage ? -50f : 0f)
                .setDuration(250)
                .withStartAction(() -> {
                    if (!isFirstPage) btnPrevious.setVisibility(View.VISIBLE);
                })
                .withEndAction(() -> {
                    if (isFirstPage) btnPrevious.setVisibility(View.INVISIBLE);
                })
                .start();

        if (isLastPage) {
            btnNext.setText(R.string.get_started);
            btnNext.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(150)
                    .withEndAction(() -> btnNext.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                    .start();
        } else {
            btnNext.setText(R.string.next);
        }

        tvSkip.animate()
                .alpha(isLastPage ? 0f : 1f)
                .setDuration(200)
                .start();
    }

    @Override
    public void navigateToNextPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @Override
    public void navigateToPreviousPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    public void navigateToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        finish();
    }

    @Override
    public void animateInitialEntry() {
        cardButtons.setTranslationY(200f);
        cardButtons.setAlpha(0f);
        tvSkip.setTranslationY(-50f);
        tvSkip.setAlpha(0f);
        indicatorContainer.setScaleX(0f);
        indicatorContainer.setScaleY(0f);

        cardButtons.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        tvSkip.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(400)
                .start();

        indicatorContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setStartDelay(500)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    private static class OnboardingPageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            float absPosition = Math.abs(position);

            page.setAlpha(1f - (absPosition * 0.5f));

            float scale = 0.85f + (1f - absPosition) * 0.15f;
            page.setScaleX(scale);
            page.setScaleY(scale);

            View animationView = page.findViewById(R.id.cardAnimation);
            if (animationView != null) {
                animationView.setTranslationX(position * page.getWidth() * 0.25f);
            }

            View contentContainer = page.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                contentContainer.setTranslationY(absPosition * 100f);
            }
        }
    }
}
