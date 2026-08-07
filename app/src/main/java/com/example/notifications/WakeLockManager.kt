package com.example.notifications

import android.content.Context
import android.os.PowerManager
import android.util.Log

/**
 * Centralized WakeLockManager singleton that explicitly manages a partial WakeLock
 * during incoming call intervals to keep CPU active until answered or rejected.
 */
object WakeLockManager {
    private const val TAG = "WakeLockManager"
    private var wakeLock: PowerManager.WakeLock? = null

    @Synchronized
    fun acquireWakeLock(context: Context, timeoutMs: Long = 60000L) {
        try {
            if (wakeLock == null) {
                val powerManager = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "CustosAnimae::RuleOfLifeCallWakeLock"
                )
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire(timeoutMs)
                Log.d(TAG, "Partial WakeLock acquired for ${timeoutMs}ms")
            } else {
                Log.d(TAG, "WakeLock is already held")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire WakeLock", e)
        }
    }

    @Synchronized
    fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
                Log.d(TAG, "Partial WakeLock released successfully")
            } else {
                Log.d(TAG, "WakeLock release requested but lock was not held")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release WakeLock", e)
        } finally {
            wakeLock = null
        }
    }

    @Synchronized
    fun isHeld(): Boolean {
        return wakeLock?.isHeld == true
    }
}
