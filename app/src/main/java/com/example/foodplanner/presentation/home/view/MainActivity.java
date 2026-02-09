package com.example.foodplanner.presentation.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.AuthActivity;
import com.example.foodplanner.utils.NetworkUtils;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private SharedPrefsHelper sharedPrefsHelper;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private TextView tvOfflineBanner;
    private NetworkUtils networkUtils;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });
        
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        networkUtils = NetworkUtils.getInstance(this);
        
        initViews();
        setupNavigation();
        observeNetworkStatus();
    }

    private void initViews() {
        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);

        ViewCompat.setOnApplyWindowInsetsListener(tvOfflineBanner, (v, insets) -> {
            int statusBarHeight = insets
                    .getInsets(WindowInsetsCompat.Type.statusBars())
                    .top;

            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarHeight + dpToPx(16),
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });
    }
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
    
    private void observeNetworkStatus() {
        disposables.add(
            networkUtils.observeNetworkStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isOnline -> {
                    if (tvOfflineBanner != null) {
                        tvOfflineBanner.setVisibility(isOnline ? View.GONE : View.VISIBLE);
                    }
                })
        );
    }
    
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPrefsHelper.logout();
        
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
