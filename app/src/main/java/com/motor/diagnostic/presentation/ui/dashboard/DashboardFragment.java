package com.motor.diagnostic.presentation.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.motor.diagnostic.R;
import com.motor.diagnostic.databinding.FragmentDashboardBinding;
import com.motor.diagnostic.domain.model.DiagnosticData;
import com.motor.diagnostic.domain.model.Motorcycle;
import com.motor.diagnostic.presentation.di.ViewModelModule;
import com.motor.diagnostic.presentation.util.NavigationUtil;
import com.motor.diagnostic.presentation.viewmodel.DashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment for displaying the dashboard with diagnostic information
 */
public class DashboardFragment extends Fragment {
    
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private NavController navController;
    private static final String TAG = "DashboardFragment";
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            binding = FragmentDashboardBinding.inflate(inflater, container, false);
            return binding.getRoot();
        } catch (Exception e) {
            Log.e(TAG, "Error inflating dashboard layout", e);
            Toast.makeText(requireContext(), "Error creating dashboard view", Toast.LENGTH_SHORT).show();
            // Return an empty view to avoid crashes
            return new View(requireContext());
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialize NavController using the utility class
            navController = NavigationUtil.initNavController(this);
            
            // Initialize ViewModel
            try {
                viewModel = new ViewModelProvider(requireActivity(), ViewModelModule.provideViewModelFactory())
                        .get(DashboardViewModel.class);
                
                // Set up click listeners
                setupClickListeners();
                
                // Observe ViewModel LiveData
                observeViewModel();
                
                // Load data
                viewModel.loadUserMotorcycles();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ViewModel", e);
                Toast.makeText(requireContext(), "Error loading dashboard data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in onViewCreated", e);
        }
    }
    
    private void setupClickListeners() {
        try {
            // Review other diagnostics button
            binding.btnReviewOtherDiagnostics.setOnClickListener(v -> {
                // TODO: Implement review diagnostics
            });
            
            // Choose motorcycle button
            binding.btnChooseMotorcycle.setOnClickListener(v -> {
                NavigationUtil.navigateSafely(navController, R.id.motorcyclesFragment, requireContext());
            });
            
            // Notification button
            binding.btnNotifications.setOnClickListener(v -> {
                NavigationUtil.navigateSafely(navController, R.id.notificationsFragment, requireContext());
            });
            
            // Add motorcycle button (in no data container)
            binding.btnAddMotorcycle.setOnClickListener(v -> {
                NavigationUtil.navigateSafely(navController, R.id.motorcyclesFragment, requireContext());
            });
            
            // Diagnostic card clicks
            binding.cardBattery.setOnClickListener(v -> navigateToDiagnosticDetail("battery"));
            binding.cardOil.setOnClickListener(v -> navigateToDiagnosticDetail("oil"));
            binding.cardEngine.setOnClickListener(v -> navigateToDiagnosticDetail("engine"));
            binding.cardTemperature.setOnClickListener(v -> navigateToDiagnosticDetail("temperature"));
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }
    
    private void observeViewModel() {
        if (viewModel == null || !isAdded()) return;
        
        try {
            // Observe selected motorcycle
            viewModel.getSelectedMotorcycle().observe(getViewLifecycleOwner(), this::updateMotorcycleUI);
            
            // Observe current diagnostic data
            viewModel.getCurrentDiagnosticData().observe(getViewLifecycleOwner(), this::updateDiagnosticUI);
            
            // Observe loading state
            viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
                if (binding != null) {
                    binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            });
            
            // Observe error message
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty() && isAdded()) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
            
            // Observe connection state
            viewModel.getIsConnected().observe(getViewLifecycleOwner(), isConnected -> {
                if (binding == null) return;
                
                if (isConnected) {
                    // If connected, show diagnostic data
                    binding.diagnosticsContainer.setVisibility(View.VISIBLE);
                    binding.noDataContainer.setVisibility(View.GONE);
                    
                    // Reload diagnostic data
                    viewModel.loadCurrentDiagnosticData();
                } else {
                    // If not connected, show no data container
                    Motorcycle motorcycle = viewModel.getSelectedMotorcycle().getValue();
                    if (motorcycle != null) {
                        // A motorcycle is selected but not connected
                        binding.diagnosticsContainer.setVisibility(View.GONE);
                        binding.noDataContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error observing ViewModel", e);
        }
    }
    
    private void updateMotorcycleUI(Motorcycle motorcycle) {
        if (!isAdded() || binding == null) return;
        
        try {
            if (motorcycle != null) {
                // Update UI with motorcycle information
                binding.tvUsername.setText(motorcycle.getNickname() != null ? 
                    motorcycle.getNickname() : "Motorcycle");
                
                // Load motorcycle image if available
                if (motorcycle.getImageUrl() != null && !motorcycle.getImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(motorcycle.getImageUrl())
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile)
                            .into(binding.ivUserProfile);
                }
            } else {
                // No motorcycle selected
                binding.tvUsername.setText("User");
                binding.diagnosticsContainer.setVisibility(View.GONE);
                binding.noDataContainer.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating motorcycle UI", e);
        }
    }
    
    private void updateDiagnosticUI(DiagnosticData diagnosticData) {
        if (!isAdded() || binding == null) return;
        
        try {
            if (diagnosticData != null) {
                // Update timestamp
                if (diagnosticData.getTimestamp() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                    binding.tvUpdateTime.setText(sdf.format(diagnosticData.getTimestamp()));
                } else {
                    binding.tvUpdateTime.setText("N/A");
                }
                
                // Update speed - prevent NullPointerException
                String speedUnit = diagnosticData.getSpeedUnit() != null ? diagnosticData.getSpeedUnit() : "km/h";
                binding.tvSpeedValue.setText(String.format(Locale.getDefault(), "%.0f %s", 
                        diagnosticData.getVehicleSpeed(), speedUnit));
                
                // Update gauge - protect against NPE and timing issues
                if (binding.gaugeBackground != null && binding.gaugeBackground.getWidth() > 0) {
                    float speedPercentage = diagnosticData.getVehicleSpeed() / 100f; // Assuming max speed is 100
                    // Ensure percentage is between 0 and 1
                    speedPercentage = Math.max(0, Math.min(1, speedPercentage));
                    binding.gaugeFill.getLayoutParams().width = (int) (binding.gaugeBackground.getWidth() * speedPercentage);
                    binding.gaugeFill.requestLayout();
                }
                
                // Update gauge color based on speed
                if (diagnosticData.getVehicleSpeed() > 80) {
                    binding.gaugeFill.setBackgroundColor(requireContext().getColor(R.color.gauge_red));
                } else if (diagnosticData.getVehicleSpeed() > 50) {
                    binding.gaugeFill.setBackgroundColor(requireContext().getColor(R.color.gauge_yellow));
                } else {
                    binding.gaugeFill.setBackgroundColor(requireContext().getColor(R.color.gauge_green));
                }
                
                // Update MIL and TRIP - primitives can't be null
                binding.tvMilValue.setText(String.format(Locale.getDefault(), "%07d", diagnosticData.getMileage()));
                binding.tvTripValue.setText(String.format(Locale.getDefault(), "%07d", diagnosticData.getTripDistance()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating diagnostic UI", e);
        }
    }
    
    private void navigateToDiagnosticDetail(String diagnosticType) {
        try {
            Bundle args = new Bundle();
            args.putString("diagnosticType", diagnosticType);
            NavigationUtil.navigateWithActionSafely(navController, 
                R.id.action_dashboardFragment_to_diagnosticDetailFragment, args, requireContext());
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to diagnostic detail", e);
            Toast.makeText(requireContext(), "Navigation failed", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 