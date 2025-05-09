package com.motor.diagnostic.presentation.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.motor.diagnostic.R;
import com.motor.diagnostic.databinding.ActivityLoginBinding;
import com.motor.diagnostic.presentation.di.ViewModelModule;
import com.motor.diagnostic.presentation.ui.MainActivity;
import com.motor.diagnostic.presentation.viewmodel.AuthViewModel;

/**
 * Activity for user login
 */
public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, ViewModelModule.provideViewModelFactory())
                .get(AuthViewModel.class);
        
        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }
        
        // Set up click listeners
        setupClickListeners();
        
        // Observe ViewModel LiveData
        observeViewModel();
    }
    
    private void setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener(v -> login());
        
        // Sign up button
        binding.btnSignUp.setOnClickListener(v -> navigateToRegisterActivity());
        
        // Forgot password
        binding.tvForgotPassword.setOnClickListener(v -> navigateToForgotPasswordActivity());
    }
    
    private void observeViewModel() {
        // Observe current user
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // User logged in successfully
                navigateToMainActivity();
            }
        });
        
        // Observe loading state
        viewModel.getLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnSignUp.setEnabled(!isLoading);
        });
        
        // Observe error message
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void login() {
        // Get input values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Validate input
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.hint_email) + " is required");
            return;
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.hint_password) + " is required");
            return;
        }
        
        // Clear errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        
        // Login
        viewModel.login(email, password);
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void navigateToForgotPasswordActivity() {
        // TODO: Implement forgot password activity
        Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
    }
} 