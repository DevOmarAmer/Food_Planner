package com.example.foodplanner.presentation.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.presentation.home.view.MainActivity;
import com.example.foodplanner.presentation.onboarding.view.OnboardingActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    
    private LottieAnimationView lottieAnimationView;
    private TextView tvAppName;
    private TextView tvTagline;
    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        

        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        startAnimations();
        navigateToNextScreen();
    }

    private void initViews() {
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        tvAppName = findViewById(R.id.tvAppName);
        tvTagline = findViewById(R.id.tvTagline);
    }
    
    private void startAnimations() {
        tvAppName.setAlpha(0f);
        tvTagline.setAlpha(0f);
        

        tvAppName.postDelayed(() -> {
            tvAppName.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start();
        }, 500);
        

        tvTagline.postDelayed(() -> {
            tvTagline.animate()
                    .alpha(0.9f)
                    .setDuration(800)
                    .start();
        }, 1000);
    }
    

    private void navigateToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;

            if (sharedPrefsHelper.hasActiveSession()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else if (!sharedPrefsHelper.isOnboardingCompleted()) {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            
            startActivity(intent);
            

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
        }
    }
}