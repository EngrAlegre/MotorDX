package com.motor.diagnostic.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.motor.diagnostic.domain.model.DiagnosticData;
import com.motor.diagnostic.domain.model.Motorcycle;
import com.motor.diagnostic.domain.model.Notification;
import com.motor.diagnostic.domain.repository.DiagnosticRepository;
import com.motor.diagnostic.domain.repository.MotorcycleRepository;
import com.motor.diagnostic.domain.repository.NotificationRepository;
import com.motor.diagnostic.domain.usecase.ConnectMotorcycleUseCase;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for dashboard and diagnostic operations
 */
public class DashboardViewModel extends ViewModel {
    
    private final MotorcycleRepository motorcycleRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final NotificationRepository notificationRepository;
    private final ConnectMotorcycleUseCase connectMotorcycleUseCase;
    
    private final MutableLiveData<List<Motorcycle>> userMotorcycles = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Motorcycle> selectedMotorcycle = new MutableLiveData<>();
    private final MutableLiveData<DiagnosticData> currentDiagnosticData = new MutableLiveData<>();
    private final MutableLiveData<List<DiagnosticData>> historicalData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);
    
    public DashboardViewModel(
            MotorcycleRepository motorcycleRepository,
            DiagnosticRepository diagnosticRepository,
            NotificationRepository notificationRepository,
            ConnectMotorcycleUseCase connectMotorcycleUseCase) {
        this.motorcycleRepository = motorcycleRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.notificationRepository = notificationRepository;
        this.connectMotorcycleUseCase = connectMotorcycleUseCase;
    }
    
    /**
     * Load user's motorcycles
     */
    public void loadUserMotorcycles() {
        loading.setValue(true);
        errorMessage.setValue(null);
        
        motorcycleRepository.getUserMotorcycles()
                .thenAccept(motorcycles -> {
                    userMotorcycles.postValue(motorcycles);
                    loading.postValue(false);
                    
                    // Select the first motorcycle if available
                    if (!motorcycles.isEmpty() && selectedMotorcycle.getValue() == null) {
                        selectMotorcycle(motorcycles.get(0));
                    }
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Connect to the selected motorcycle
     */
    public void connectToMotorcycle() {
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle == null) {
            errorMessage.setValue("No motorcycle selected");
            return;
        }
        
        loading.setValue(true);
        errorMessage.setValue(null);
        
        connectMotorcycleUseCase.execute(motorcycle.getId())
                .thenAccept(connected -> {
                    isConnected.postValue(connected);
                    loading.postValue(false);
                    
                    // If connected, load diagnostic data and listen for updates
                    if (connected) {
                        loadCurrentDiagnosticData();
                        loadHistoricalData();
                        loadNotifications();
                        
                        // Subscribe to diagnostic updates
                        diagnosticRepository.subscribeToDiagnosticUpdates(motorcycle.getId(), new DiagnosticRepository.DiagnosticUpdateListener() {
                            @Override
                            public void onDiagnosticUpdate(DiagnosticData diagnosticData) {
                                currentDiagnosticData.postValue(diagnosticData);
                            }
                            
                            @Override
                            public void onDiagnosticError(Exception e) {
                                errorMessage.postValue("Diagnostic error: " + e.getMessage());
                            }
                        });
                    }
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Disconnect from the selected motorcycle
     */
    public void disconnectFromMotorcycle() {
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle == null) {
            errorMessage.setValue("No motorcycle selected");
            return;
        }
        
        loading.setValue(true);
        errorMessage.setValue(null);
        
        connectMotorcycleUseCase.disconnect(motorcycle.getId())
                .thenAccept(disconnected -> {
                    isConnected.postValue(false);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Load current diagnostic data for the selected motorcycle
     */
    public void loadCurrentDiagnosticData() {
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle == null) {
            return;
        }
        
        loading.setValue(true);
        
        diagnosticRepository.getCurrentDiagnosticData(motorcycle.getId())
                .thenAccept(diagnosticData -> {
                    currentDiagnosticData.postValue(diagnosticData);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Load historical diagnostic data for the selected motorcycle
     */
    public void loadHistoricalData() {
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle == null) {
            return;
        }
        
        loading.setValue(true);
        
        // Get data from the last 7 days
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (7 * 24 * 60 * 60 * 1000); // 7 days
        
        diagnosticRepository.getHistoricalDiagnosticData(
                motorcycle.getId(), new java.util.Date(startTime), new java.util.Date(endTime))
                .thenAccept(dataList -> {
                    historicalData.postValue(dataList);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Load notifications for the selected motorcycle
     */
    public void loadNotifications() {
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle == null) {
            return;
        }
        
        loading.setValue(true);
        
        diagnosticRepository.getDiagnosticNotifications(motorcycle.getId(), 20)
                .thenAccept(notificationList -> {
                    notifications.postValue(notificationList);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Select a motorcycle
     * @param motorcycle The motorcycle to select
     */
    public void selectMotorcycle(Motorcycle motorcycle) {
        selectedMotorcycle.setValue(motorcycle);
        
        // Check if the motorcycle is connected
        if (motorcycle != null) {
            motorcycleRepository.isMotorcycleConnected(motorcycle.getId())
                    .thenAccept(connected -> {
                        isConnected.postValue(connected);
                        
                        // If connected, load diagnostic data
                        if (connected) {
                            loadCurrentDiagnosticData();
                            loadHistoricalData();
                            loadNotifications();
                        }
                    })
                    .exceptionally(e -> {
                        errorMessage.postValue(e.getMessage());
                        return null;
                    });
        }
    }
    
    /**
     * Get user motorcycles
     * @return LiveData holding user motorcycles
     */
    public LiveData<List<Motorcycle>> getUserMotorcycles() {
        return userMotorcycles;
    }
    
    /**
     * Get selected motorcycle
     * @return LiveData holding the selected motorcycle
     */
    public LiveData<Motorcycle> getSelectedMotorcycle() {
        return selectedMotorcycle;
    }
    
    /**
     * Get current diagnostic data
     * @return LiveData holding current diagnostic data
     */
    public LiveData<DiagnosticData> getCurrentDiagnosticData() {
        return currentDiagnosticData;
    }
    
    /**
     * Get historical diagnostic data
     * @return LiveData holding historical diagnostic data
     */
    public LiveData<List<DiagnosticData>> getHistoricalData() {
        return historicalData;
    }
    
    /**
     * Get notifications
     * @return LiveData holding notifications
     */
    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }
    
    /**
     * Get loading state
     * @return LiveData holding loading state
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    /**
     * Get error message
     * @return LiveData holding error message
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get connection state
     * @return LiveData holding connection state
     */
    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        
        // Clean up resources
        Motorcycle motorcycle = selectedMotorcycle.getValue();
        if (motorcycle != null) {
            diagnosticRepository.unsubscribeFromDiagnosticUpdates(motorcycle.getId());
            diagnosticRepository.stopAlertMonitoring(motorcycle.getId());
        }
    }
} 