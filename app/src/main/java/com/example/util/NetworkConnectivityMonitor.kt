package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Real-time network connectivity monitor for Android.
 * Emits active online/offline state changes as a StateFlow and provides synchronous checks.
 */
class NetworkConnectivityMonitor private constructor(context: Context) {

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isOnline = MutableStateFlow(isCurrentlyOnline())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d("NetworkMonitor", "Network onAvailable: $network")
            updateConnectionState()
        }

        override fun onLost(network: Network) {
            Log.d("NetworkMonitor", "Network onLost: $network")
            updateConnectionState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            Log.d("NetworkMonitor", "Network onCapabilitiesChanged: internet=$hasInternet")
            _isOnline.value = hasInternet
        }
    }

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager?.registerNetworkCallback(request, networkCallback)
            }
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Failed to register network callback", e)
            _isOnline.value = isCurrentlyOnline()
        }
    }

    private fun updateConnectionState() {
        _isOnline.value = isCurrentlyOnline()
    }

    /**
     * Synchronously checks if the device currently has active internet connectivity.
     */
    @Suppress("DEPRECATION")
    fun isCurrentlyOnline(): Boolean {
        val cm = connectivityManager ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = cm.activeNetwork ?: return false
                val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val networkInfo = cm.activeNetworkInfo
                networkInfo != null && networkInfo.isConnectedOrConnecting
            }
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error checking online status", e)
            false
        }
    }

    companion object {
        @Volatile
        private var instance: NetworkConnectivityMonitor? = null

        fun getInstance(context: Context): NetworkConnectivityMonitor {
            return instance ?: synchronized(this) {
                instance ?: NetworkConnectivityMonitor(context.applicationContext).also { instance = it }
            }
        }
    }
}
