package com.motor.diagnostic.data.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.motor.diagnostic.MotorDiagnosticApplication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for verifying the app is working correctly
 */
public class AppVerifier {
    private static final String TAG = "AppVerifier";
    private static final String TEST_PATH = "app_verification";

    /**
     * Run a comprehensive verification of app components
     * 
     * @param context Application context
     * @param listener Listener for verification results
     */
    public static void verifyAppComponents(Context context, VerificationListener listener) {
        StringBuilder report = new StringBuilder();
        report.append("App Verification Report - ").append(new Date()).append("\n\n");
        
        // 1. Verify Firebase initialization
        boolean firebaseInit = false;
        try {
            if (context.getApplicationContext() instanceof MotorDiagnosticApplication) {
                MotorDiagnosticApplication app = (MotorDiagnosticApplication) context.getApplicationContext();
                firebaseInit = app.isFirebaseInitialized();
            } else {
                firebaseInit = !FirebaseApp.getApps(context).isEmpty();
            }
            report.append("Firebase Initialization: ").append(firebaseInit ? "SUCCESS" : "FAILED").append("\n");
        } catch (Exception e) {
            report.append("Firebase Initialization: EXCEPTION - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Firebase verification error", e);
        }
        
        final boolean firebaseInitialized = firebaseInit;
        
        // 2. Verify network connectivity
        boolean networkConnected = false;
        try {
            if (context.getApplicationContext() instanceof MotorDiagnosticApplication) {
                MotorDiagnosticApplication app = (MotorDiagnosticApplication) context.getApplicationContext();
                NetworkConnectionHandler networkHandler = app.getNetworkHandler();
                if (networkHandler != null) {
                    networkConnected = networkHandler.isConnected();
                }
            }
            report.append("Network Connectivity: ").append(networkConnected ? "CONNECTED" : "DISCONNECTED").append("\n");
        } catch (Exception e) {
            report.append("Network Connectivity: EXCEPTION - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Network verification error", e);
        }
        
        // 3. Verify Firebase read/write if Firebase is initialized
        if (firebaseInitialized) {
            try {
                // Try to write test data
                String testKey = "test_" + System.currentTimeMillis();
                DatabaseReference testRef = FirebaseDatabase.getInstance().getReference(TEST_PATH).child(testKey);
                
                Map<String, Object> testData = new HashMap<>();
                testData.put("timestamp", System.currentTimeMillis());
                testData.put("message", "Verification Test");
                
                testRef.setValue(testData, (error, ref) -> {
                    if (error == null) {
                        report.append("Firebase Write: SUCCESS\n");
                        
                        // Try to read back the data
                        testRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    report.append("Firebase Read: SUCCESS\n");
                                    // Clean up test data
                                    testRef.removeValue();
                                } else {
                                    report.append("Firebase Read: FAILED - Data not found\n");
                                }
                                
                                // Complete verification
                                finalizeVerification(context, report.toString(), listener);
                            }
                            
                            @Override
                            public void onCancelled(DatabaseError error) {
                                report.append("Firebase Read: FAILED - ").append(error.getMessage()).append("\n");
                                finalizeVerification(context, report.toString(), listener);
                            }
                        });
                    } else {
                        report.append("Firebase Write: FAILED - ").append(error.getMessage()).append("\n");
                        finalizeVerification(context, report.toString(), listener);
                    }
                });
            } catch (Exception e) {
                report.append("Firebase Operation: EXCEPTION - ").append(e.getMessage()).append("\n");
                Log.e(TAG, "Firebase operation verification error", e);
                finalizeVerification(context, report.toString(), listener);
            }
        } else {
            report.append("Firebase Operations: SKIPPED (Firebase not initialized)\n");
            finalizeVerification(context, report.toString(), listener);
        }
    }
    
    /**
     * Complete the verification process and notify listener
     * 
     * @param context Application context
     * @param report Verification report
     * @param listener Verification listener
     */
    private static void finalizeVerification(Context context, String report, VerificationListener listener) {
        Log.d(TAG, "Verification completed:\n" + report);
        
        if (listener != null) {
            listener.onVerificationComplete(report);
        }
    }
    
    /**
     * Interface for verification result callbacks
     */
    public interface VerificationListener {
        void onVerificationComplete(String report);
    }
} 