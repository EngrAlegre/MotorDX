package com.motor.diagnostic.data.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Handles network connectivity and Firebase connection state management
 */
public class NetworkConnectionHandler {
    private static final String TAG = "NetworkHandler";
    private static NetworkConnectionHandler instance;
    
    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkConnectionListener connectionListener;
    private boolean isConnected = false;
    private final Handler handler;
    
    /**
     * Interface for listening to network connection changes
     */
    public interface NetworkConnectionListener {
        void onConnectionChanged(boolean isConnected);
    }
    
    private NetworkConnectionHandler(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Get the singleton instance of NetworkConnectionHandler
     * 
     * @param context The application context
     * @return The NetworkConnectionHandler instance
     */
    public static synchronized NetworkConnectionHandler getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkConnectionHandler(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Start monitoring network connectivity
     * 
     * @param listener The listener to notify of connection changes
     */
    public void startMonitoring(NetworkConnectionListener listener) {
        this.connectionListener = listener;
        
        // Check current connection state
        checkConnectionState();
        
        // Register network callback
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Network available");
                updateConnectionState(true);
                pingFirebase();
            }
            
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d(TAG, "Network lost");
                updateConnectionState(false);
            }
            
            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d(TAG, "Network unavailable");
                updateConnectionState(false);
            }
        };
        
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }
    
    /**
     * Stop monitoring network connectivity
     */
    public void stopMonitoring() {
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering network callback", e);
            }
            networkCallback = null;
        }
        connectionListener = null;
    }
    
    /**
     * Check the current connection state
     */
    private void checkConnectionState() {
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(
                connectivityManager.getActiveNetwork());
        
        boolean hasConnection = capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                 capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                 capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        
        updateConnectionState(hasConnection);
        
        if (hasConnection) {
            pingFirebase();
        }
    }
    
    /**
     * Update the connection state and notify listener
     * 
     * @param connected Whether there is a network connection
     */
    private void updateConnectionState(boolean connected) {
        if (isConnected != connected) {
            isConnected = connected;
            
            if (connectionListener != null) {
                // Post on main thread
                handler.post(() -> connectionListener.onConnectionChanged(isConnected));
            }
        }
    }
    
    /**
     * Ping Firebase to test actual connectivity to the backend
     */
    private void pingFirebase() {
        try {
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class) != null && 
                                      snapshot.getValue(Boolean.class);
                    Log.d(TAG, "Firebase connection: " + (connected ? "connected" : "disconnected"));
                    
                    // Only update if we're disconnected from Firebase but have network
                    if (!connected && isConnected) {
                        // We have network but can't reach Firebase
                        Log.w(TAG, "Network available but Firebase unreachable");
                        
                        // Retry after a delay
                        handler.postDelayed(NetworkConnectionHandler.this::pingFirebase, 5000);
                    }
                }
                
                @Override
                public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                    Log.e(TAG, "Firebase connection check failed", error.toException());
                    
                    // Retry after a delay
                    handler.postDelayed(NetworkConnectionHandler.this::pingFirebase, 5000);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error checking Firebase connection", e);
        }
    }
    
    /**
     * Check if there is currently a network connection
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
} 