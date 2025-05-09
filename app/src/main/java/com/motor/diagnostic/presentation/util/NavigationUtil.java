package com.motor.diagnostic.presentation.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

/**
 * Utility class for handling navigation operations safely
 */
public class NavigationUtil {
    private static final String TAG = "NavigationUtil";

    /**
     * Navigate safely to a destination, with error handling
     * 
     * @param navController The NavController to use for navigation
     * @param destinationId The destination ID to navigate to
     * @param context The context for showing error messages
     * @return Whether navigation was successful
     */
    public static boolean navigateSafely(NavController navController, @IdRes int destinationId, Context context) {
        return navigateSafely(navController, destinationId, null, context);
    }

    /**
     * Navigate safely to a destination with arguments, with error handling
     * 
     * @param navController The NavController to use for navigation
     * @param destinationId The destination ID to navigate to
     * @param args Arguments to pass to the destination
     * @param context The context for showing error messages
     * @return Whether navigation was successful
     */
    public static boolean navigateSafely(NavController navController, @IdRes int destinationId, 
                                      Bundle args, Context context) {
        try {
            if (navController != null) {
                navController.navigate(destinationId, args);
                return true;
            } else {
                Log.e(TAG, "Cannot navigate: NavController is null");
                showNavigationError(context, "Navigation not available");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error to destination " + destinationId, e);
            showNavigationError(context, "Navigation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Navigate safely using an action, with error handling
     * 
     * @param navController The NavController to use for navigation
     * @param actionId The action ID to navigate with
     * @param args Arguments to pass to the destination
     * @param context The context for showing error messages
     * @return Whether navigation was successful
     */
    public static boolean navigateWithActionSafely(NavController navController, @IdRes int actionId,
                                               Bundle args, Context context) {
        try {
            if (navController != null) {
                navController.navigate(actionId, args);
                return true;
            } else {
                Log.e(TAG, "Cannot navigate with action: NavController is null");
                showNavigationError(context, "Navigation not available");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error with action " + actionId, e);
            showNavigationError(context, "Navigation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize NavController safely with error handling
     * 
     * @param fragment The fragment to get the NavController for
     * @return The NavController or null if it couldn't be initialized
     */
    public static NavController initNavController(Fragment fragment) {
        try {
            return androidx.navigation.Navigation.findNavController(fragment.requireView());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing NavController in fragment " + fragment.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * Show a navigation error toast
     * 
     * @param context The context for showing the toast
     * @param message The error message to show
     */
    private static void showNavigationError(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
} 