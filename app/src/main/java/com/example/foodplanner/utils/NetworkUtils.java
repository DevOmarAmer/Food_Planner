package com.example.foodplanner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * Utility class for checking and monitoring network connectivity.
 * Uses RxJava to provide reactive network status updates.
 */
public class NetworkUtils {
    
    private static NetworkUtils instance;
    private final ConnectivityManager connectivityManager;
    private final BehaviorSubject<Boolean> networkStatusSubject;
    private ConnectivityManager.NetworkCallback networkCallback;
    
    private NetworkUtils(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkStatusSubject = BehaviorSubject.createDefault(isNetworkAvailable());
        registerNetworkCallback();
    }
    
    public static synchronized NetworkUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtils(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Check if network is currently available
     * @return true if network is available, false otherwise
     */
    public boolean isNetworkAvailable() {
        if (connectivityManager == null) {
            return false;
        }
        
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
    
    /**
     * Get an Observable that emits network status changes
     * @return Observable<Boolean> that emits true when online, false when offline
     */
    public Observable<Boolean> observeNetworkStatus() {
        return networkStatusSubject.distinctUntilChanged();
    }
    
    /**
     * Get the current network status as a one-time check
     * @return current network status
     */
    public boolean getCurrentStatus() {
        return networkStatusSubject.getValue() != null && networkStatusSubject.getValue();
    }
    
    private void registerNetworkCallback() {
        if (connectivityManager == null) {
            return;
        }
        
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                networkStatusSubject.onNext(true);
            }
            
            @Override
            public void onLost(@NonNull Network network) {
                // Double check if we really have no network
                networkStatusSubject.onNext(isNetworkAvailable());
            }
            
            @Override
            public void onCapabilitiesChanged(@NonNull Network network, 
                                              @NonNull NetworkCapabilities networkCapabilities) {
                boolean hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                     networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                networkStatusSubject.onNext(hasInternet);
            }
        };
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }
    
    /**
     * Unregister the network callback when no longer needed
     */
    public void unregister() {
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {
                // Callback may not be registered
            }
        }
    }
}
