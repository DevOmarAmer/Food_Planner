package com.example.foodplanner.presentation.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodplanner.R;
import com.example.foodplanner.presentation.auth.view.AuthActivity;
import com.example.foodplanner.utils.SharedPrefsHelper;
import com.google.firebase.auth.FirebaseAuth;

/**
 * MainActivity - Home screen of the app
 * 
 * This will eventually contain:
 * 1. Meal of the day
 * 2. Categories list
 * 3. Countries list
 * 4. Bottom navigation (Home, Search, Favorites, Plan)
 * 
 * TODO: Implement in the next step
 */
public class MainActivity extends AppCompatActivity {

    private SharedPrefsHelper sharedPrefsHelper;
    private TextView tvWelcome;
    private TextView tvUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        sharedPrefsHelper = SharedPrefsHelper.getInstance(this);
        
        initViews();
        displayUserInfo();
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserStatus = findViewById(R.id.tvUserStatus);
    }
    
    private void displayUserInfo() {
        String userName = sharedPrefsHelper.getUserName();
        boolean isGuest = sharedPrefsHelper.isGuest();
        
        if (isGuest) {
            tvWelcome.setText("Welcome, Guest!");
            tvUserStatus.setText(R.string.guest_mode_notice);
        } else {
            tvWelcome.setText("Welcome, " + (userName != null ? userName : "User") + "!");
            tvUserStatus.setText("You're logged in. Full features available.");
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
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();
        
        // Clear local session
        sharedPrefsHelper.logout();
        
        // Navigate to Auth screen
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
