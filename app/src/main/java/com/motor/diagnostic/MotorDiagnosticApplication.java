package com.motor.diagnostic;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.motor.diagnostic.data.util.NetworkConnectionHandler;

/**
 * Main application class for the Motor Diagnostic app
 */
public class MotorDiagnosticApplication extends Application {
    
    private static final String TAG = "MotorDiagnosticApp";
    private NetworkConnectionHandler networkHandler;
    private boolean firebaseInitialized = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Initialize Firebase with safety checks
            initializeFirebase();
            
            // Initialize Network Connection Handler
            initializeNetworkHandler();
        } catch (Exception e) {
            Log.e(TAG, "Error during application initialization", e);
            Toast.makeText(this, "Error initializing application", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Initialize Firebase safely
     */
    private void initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                
                // Enable Firebase offline capabilities
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                
                firebaseInitialized = true;
                Log.d(TAG, "Firebase initialized successfully");
            } else {
                firebaseInitialized = true;
                Log.d(TAG, "Firebase was already initialized");
            }
        } catch (Exception e) {
            firebaseInitialized = false;
            Log.e(TAG, "Firebase initialization failed", e);
            
            // Try again after a delay
            new Handler(Looper.getMainLooper()).postDelayed(this::initializeFirebase, 3000);
        }
    }
    
    /**
     * Initialize the network connection handler
     */
    private void initializeNetworkHandler() {
        try {
            networkHandler = NetworkConnectionHandler.getInstance(this);
            networkHandler.startMonitoring(new NetworkConnectionHandler.NetworkConnectionListener() {
                @Override
                public void onConnectionChanged(boolean isConnected) {
                    Log.d(TAG, "Network connection changed: " + (isConnected ? "Connected" : "Disconnected"));
                    
                    if (isConnected && !firebaseInitialized) {
                        // If network is available but Firebase isn't initialized, try to initialize it
                        initializeFirebase();
                    }
                }
            });
            Log.d(TAG, "Network connection handler initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize network handler", e);
        }
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        
        try {
            // Initialize Multidex
            MultiDex.install(this);
            Log.d(TAG, "Multidex installed");
        } catch (Exception e) {
            Log.e(TAG, "Error installing Multidex", e);
        }
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        
        // Clean up resources
        if (networkHandler != null) {
            networkHandler.stopMonitoring();
        }
        
        Log.d(TAG, "Application terminated");
    }
    
    /**
     * Get the network connection handler
     * 
     * @return The network connection handler
     */
    public NetworkConnectionHandler getNetworkHandler() {
        return networkHandler;
    }
    
    /**
     * Check if Firebase is initialized
     * 
     * @return true if Firebase is initialized, false otherwise
     */
    public boolean isFirebaseInitialized() {
        return firebaseInitialized;
    }
} 