package com.motor.diagnostic.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.motor.diagnostic.R;
import com.motor.diagnostic.databinding.ActivityMainBinding;
import com.motor.diagnostic.presentation.di.ViewModelModule;
import com.motor.diagnostic.presentation.ui.authentication.LoginActivity;
import com.motor.diagnostic.presentation.viewmodel.DashboardViewModel;

/**
 * Main activity that hosts the navigation components and bottom navigation
 */
public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    private NavController navController;
    private DashboardViewModel viewModel;
    private static final String TAG = "MainActivity";
    private static final int MAX_RETRY_COUNT = 3;
    private int navigationInitRetryCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Initialize binding
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            
            // Initialize ViewModel
            try {
                viewModel = new ViewModelProvider(this, ViewModelModule.provideViewModelFactory())
                        .get(DashboardViewModel.class);
                
                // Show progress indicator while navigation is loading
                binding.progressBar.setVisibility(View.VISIBLE);
                
                // Initialize navigation with retry mechanism
                initializeNavigation();
                
            } catch (Exception e) {
                // ViewModel initialization failed
                Log.e(TAG, "ViewModel initialization failed", e);
                showErrorAndRestart("Error initializing dashboard: " + e.getMessage());
            }
        } catch (Exception e) {
            // Fatal initialization error
            Log.e(TAG, "Fatal initialization error", e);
            showErrorAndRestart("Application initialization failed: " + e.getMessage());
        }
    }
    
    /**
     * Initialize navigation with retry mechanism
     */
    private void initializeNavigation() {
        // Wait for the fragment container to be properly initialized
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // First check if the fragment host exists and is properly inflated
                View navHostFragment = findViewById(R.id.nav_host_fragment);
                if (navHostFragment == null) {
                    throw new IllegalStateException("Nav host fragment not found");
                }
                
                navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
                
                // Handle navigation state changes
                navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                   @NonNull NavDestination destination,
                                                   @Nullable Bundle arguments) {
                        // Hide progress indicator once navigation is ready
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "Navigation to: " + destination.getLabel());
                    }
                });
                
                // Reset retry count on success
                navigationInitRetryCount = 0;
                
                // Load user's motorcycles
                if (viewModel != null) {
                    viewModel.loadUserMotorcycles();
                }
            } catch (Exception e) {
                Log.e(TAG, "Navigation setup failed", e);
                binding.progressBar.setVisibility(View.GONE);
                
                // Retry initialization if under max retry count
                if (navigationInitRetryCount < MAX_RETRY_COUNT) {
                    navigationInitRetryCount++;
                    Log.d(TAG, "Retrying navigation setup. Attempt " + navigationInitRetryCount);
                    // Wait longer for each retry
                    initializeNavigation(500 * navigationInitRetryCount);
                } else {
                    showErrorAndRestart("Navigation setup failed after multiple attempts: " + e.getMessage());
                }
            }
        }, 500); // Give it 500ms to initialize properly
    }
    
    /**
     * Initialize navigation with a custom delay
     * 
     * @param delayMillis The delay in milliseconds
     */
    private void initializeNavigation(long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(this::initializeNavigation, delayMillis);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Any additional context initialization if needed
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources
        binding = null;
    }
    
    /**
     * Show error message and restart to login
     */
    private void showErrorAndRestart(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        // Go back to login
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 