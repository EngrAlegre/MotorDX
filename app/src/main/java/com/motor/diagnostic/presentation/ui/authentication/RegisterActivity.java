package com.motor.diagnostic.presentation.ui.authentication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.motor.diagnostic.databinding.ActivityRegisterBinding;
import com.motor.diagnostic.presentation.di.ViewModelModule;
import com.motor.diagnostic.presentation.viewmodel.AuthViewModel;

import java.util.Date;

/**
 * Activity for user registration
 */
public class RegisterActivity extends AppCompatActivity {
    
    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, ViewModelModule.provideViewModelFactory())
                .get(AuthViewModel.class);
        
        // Set button click listeners
        binding.btnSignup.setOnClickListener(v -> register());
        binding.tvLogin.setOnClickListener(v -> finish());
    }
    
    /**
     * Attempts to register a new user
     */
    private void register() {
        // Get input values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        
        // Basic validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add dummy values for other required fields
        String fullName = "Test User";
        String nickName = "testuser";
        String phoneNumber = "1234567890";
        Date dateOfBirth = new Date(); // Current date
        String country = "United States";
        String sex = "Male";
        String address = "123 Main St, Anytown, USA";
        
        // Attempt registration
        viewModel.register(email, password, fullName, nickName, phoneNumber, dateOfBirth, country, sex, address);
        
        // Show success message and finish activity
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        finish();
    }
} 