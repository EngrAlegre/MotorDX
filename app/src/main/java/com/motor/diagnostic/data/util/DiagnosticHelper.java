package com.motor.diagnostic.data.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.motor.diagnostic.domain.model.DiagnosticData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Helper class for diagnostic operations
 */
public class DiagnosticHelper {
    private static final String TAG = "DiagnosticHelper";
    private static final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    
    /**
     * Retry mechanism for Firebase operations
     * 
     * @param operation The operation to retry
     * @param maxRetries Maximum number of retries
     * @param initialDelayMs Initial delay in milliseconds
     */
    public static void retryOperation(Runnable operation, int maxRetries, long initialDelayMs) {
        retryOperation(operation, maxRetries, initialDelayMs, 0);
    }
    
    private static void retryOperation(Runnable operation, int maxRetries, long delayMs, int currentRetry) {
        try {
            operation.run();
        } catch (Exception e) {
            Log.e(TAG, "Operation failed (attempt " + (currentRetry + 1) + "/" + maxRetries + ")", e);
            
            if (currentRetry < maxRetries) {
                // Exponential backoff
                long nextDelay = delayMs * 2;
                
                Log.d(TAG, "Retrying operation in " + nextDelay + "ms");
                
                // Schedule retry
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                final int nextRetry = currentRetry + 1;
                handler.postDelayed(() -> retryOperation(operation, maxRetries, nextDelay, nextRetry), delayMs);
            }
        }
    }
    
    /**
     * Check if a motorcycle is connected
     * 
     * @param motorcycleId The ID of the motorcycle to check
     * @param listener The listener to notify with the result
     */
    public static void checkMotorcycleConnection(String motorcycleId, ConnectionCheckListener listener) {
        if (motorcycleId == null || motorcycleId.isEmpty()) {
            listener.onConnectionChecked(false);
            return;
        }
        
        try {
            DatabaseReference motorRef = FirebaseDatabase.getInstance()
                    .getReference("motorcycles").child(motorcycleId);
            
            motorRef.child("isConnected").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Boolean isConnected = snapshot.getValue(Boolean.class);
                    listener.onConnectionChecked(isConnected != null && isConnected);
                }
                
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Error checking motorcycle connection", error.toException());
                    listener.onConnectionChecked(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error accessing Firebase", e);
            listener.onConnectionChecked(false);
        }
    }
    
    /**
     * Safely update diagnostic data in Firebase
     * 
     * @param data The diagnostic data to update
     * @param deviceId The device ID to update for
     */
    public static void safelyUpdateDiagnosticData(DiagnosticData data, String deviceId) {
        if (data == null || deviceId == null || deviceId.isEmpty()) {
            Log.e(TAG, "Cannot update diagnostic data with null/empty data or deviceId");
            return;
        }
        
        backgroundExecutor.execute(() -> {
            try {
                // Ensure we have a timestamp
                if (data.getTimestamp() == null) {
                    data.setTimestamp(new Date());
                }
                
                // Create a map for Firebase update
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("timestamp", data.getTimestamp().getTime());
                
                // Battery data - use "batteryVoltage" instead of "batteryLevel"
                dataMap.put("batteryVoltage", data.getBatteryVoltage());
                
                // Add battery status if available
                if (data.getBatteryStatus() != null) {
                    dataMap.put("batteryStatus", data.getBatteryStatus());
                }
                
                // Oil level (primitive float, can't be null)
                dataMap.put("oilLevel", data.getOilLevel());
                
                // Oil status if available
                if (data.getOilStatus() != null) {
                    dataMap.put("oilStatus", data.getOilStatus());
                }
                
                // Temperature data - use "coolantTemp" instead of "engineTemperature"
                dataMap.put("coolantTemp", data.getCoolantTemp());
                
                // Vehicle speed (primitive float, can't be null)
                dataMap.put("vehicleSpeed", data.getVehicleSpeed());
                
                // Speed unit
                if (data.getSpeedUnit() != null) {
                    dataMap.put("speedUnit", data.getSpeedUnit());
                }
                
                // Update in Firebase
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("diagnosticData").child(deviceId);
                
                ref.updateChildren(dataMap)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Diagnostic data updated successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update diagnostic data", e));
                
            } catch (Exception e) {
                Log.e(TAG, "Error updating diagnostic data", e);
            }
        });
    }
    
    /**
     * Interface for connection check callbacks
     */
    public interface ConnectionCheckListener {
        void onConnectionChecked(boolean isConnected);
    }
} 