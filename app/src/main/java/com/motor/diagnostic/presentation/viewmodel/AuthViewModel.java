package com.motor.diagnostic.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.motor.diagnostic.domain.model.User;
import com.motor.diagnostic.domain.usecase.LoginUseCase;
import com.motor.diagnostic.domain.usecase.RegisterUserUseCase;

import java.util.Date;

/**
 * ViewModel for authentication operations
 */
public class AuthViewModel extends ViewModel {
    
    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public AuthViewModel(LoginUseCase loginUseCase, RegisterUserUseCase registerUserUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }
    
    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     */
    public void login(String email, String password) {
        loading.setValue(true);
        errorMessage.setValue(null);
        
        loginUseCase.execute(email, password)
                .thenAccept(user -> {
                    currentUser.postValue(user);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param fullName User's full name
     * @param nickName User's nickname
     * @param phoneNumber User's phone number
     * @param dateOfBirth User's date of birth
     * @param country User's country
     * @param sex User's sex
     * @param address User's address
     */
    public void register(String email, String password, String fullName, String nickName,
                         String phoneNumber, Date dateOfBirth, String country, String sex,
                         String address) {
        loading.setValue(true);
        errorMessage.setValue(null);
        
        registerUserUseCase.execute(email, password, fullName, nickName, phoneNumber, 
                dateOfBirth, country, sex, address)
                .thenAccept(user -> {
                    currentUser.postValue(user);
                    loading.postValue(false);
                })
                .exceptionally(e -> {
                    errorMessage.postValue(e.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return loginUseCase.isUserLoggedIn();
    }
    
    /**
     * Get the current user
     * @return LiveData holding the current user
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Get the loading state
     * @return LiveData holding the loading state
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    /**
     * Get the error message
     * @return LiveData holding the error message
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
} 