package com.motor.diagnostic.presentation.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.motor.diagnostic.data.repository.DiagnosticRepositoryImpl;
import com.motor.diagnostic.data.repository.MotorcycleRepositoryImpl;
import com.motor.diagnostic.data.repository.NotificationRepositoryImpl;
import com.motor.diagnostic.data.repository.UserRepositoryImpl;
import com.motor.diagnostic.domain.repository.DiagnosticRepository;
import com.motor.diagnostic.domain.repository.MotorcycleRepository;
import com.motor.diagnostic.domain.repository.NotificationRepository;
import com.motor.diagnostic.domain.repository.UserRepository;
import com.motor.diagnostic.domain.usecase.ConnectMotorcycleUseCase;
import com.motor.diagnostic.domain.usecase.LoginUseCase;
import com.motor.diagnostic.domain.usecase.RegisterUserUseCase;
import com.motor.diagnostic.presentation.viewmodel.AuthViewModel;
import com.motor.diagnostic.presentation.viewmodel.DashboardViewModel;

/**
 * Module for providing ViewModel instances
 */
public class ViewModelModule {
    
    /**
     * Provider for ViewModelFactory
     * @return ViewModelFactory instance
     */
    public static ViewModelProvider.Factory provideViewModelFactory() {
        return new ViewModelProvider.Factory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(AuthViewModel.class)) {
                    return (T) provideAuthViewModel();
                } else if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
                    return (T) provideDashboardViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
            }
        };
    }
    
    /**
     * Provider for AuthViewModel
     * @return AuthViewModel instance
     */
    private static AuthViewModel provideAuthViewModel() {
        UserRepository userRepository = provideUserRepository();
        LoginUseCase loginUseCase = new LoginUseCase(userRepository);
        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository);
        return new AuthViewModel(loginUseCase, registerUserUseCase);
    }
    
    /**
     * Provider for DashboardViewModel
     * @return DashboardViewModel instance
     */
    private static DashboardViewModel provideDashboardViewModel() {
        MotorcycleRepository motorcycleRepository = provideMotorcycleRepository();
        DiagnosticRepository diagnosticRepository = provideDiagnosticRepository();
        NotificationRepository notificationRepository = provideNotificationRepository();
        ConnectMotorcycleUseCase connectMotorcycleUseCase = new ConnectMotorcycleUseCase(
                motorcycleRepository, diagnosticRepository, notificationRepository);
        return new DashboardViewModel(
                motorcycleRepository, diagnosticRepository, notificationRepository, connectMotorcycleUseCase);
    }
    
    /**
     * Provider for UserRepository
     * @return UserRepository instance
     */
    private static UserRepository provideUserRepository() {
        return new UserRepositoryImpl();
    }
    
    /**
     * Provider for MotorcycleRepository
     * @return MotorcycleRepository instance
     */
    private static MotorcycleRepository provideMotorcycleRepository() {
        return new MotorcycleRepositoryImpl();
    }
    
    /**
     * Provider for DiagnosticRepository
     * @return DiagnosticRepository instance
     */
    private static DiagnosticRepository provideDiagnosticRepository() {
        return new DiagnosticRepositoryImpl();
    }
    
    /**
     * Provider for NotificationRepository
     * @return NotificationRepository instance
     */
    private static NotificationRepository provideNotificationRepository() {
        return new NotificationRepositoryImpl();
    }
} 