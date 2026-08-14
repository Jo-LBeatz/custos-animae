package com.example.notifications

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.annotation.RequiresApi

object RuleOfLifeCallStateHelper {
    private const val PREFS_NAME = "rule_of_life_call_prefs"
    private const val KEY_LAST_EVENT_TIMESTAMP = "last_scheduled_event_timestamp"
    private const val KEY_IS_BOOT_INITIALIZING = "is_boot_initializing"
    private const val VALID_WINDOW_MS = 5 * 60 * 1000L // 5 minutes window

    fun markScheduledEventTriggered(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putLong(KEY_LAST_EVENT_TIMESTAMP, System.currentTimeMillis())
            .putBoolean(KEY_IS_BOOT_INITIALIZING, false)
            .apply()
    }

    fun markBootCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_BOOT_INITIALIZING, true)
            .putLong(KEY_LAST_EVENT_TIMESTAMP, 0L)
            .apply()
    }

    fun isScheduledEventActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isBoot = prefs.getBoolean(KEY_IS_BOOT_INITIALIZING, false)
        if (isBoot) {
            return false // Never invoke during initial app boot or system startup
        }
        val lastEvent = prefs.getLong(KEY_LAST_EVENT_TIMESTAMP, 0L)
        if (lastEvent <= 0L) return false
        val elapsed = System.currentTimeMillis() - lastEvent
        return elapsed in 0..VALID_WINDOW_MS
    }
}

@RequiresApi(Build.VERSION_CODES.N)
class RuleOfLifeCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Rule of Life Call"

        // Respond to telecom call with full allow response
        val builder = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setSilenceCall(false)
        }

        val response = builder.build()

        respondToCall(callDetails, response)

        // Conditional Check: Ensure it ONLY executes when a scheduled Rule of Life event is active,
        // and is NEVER invoked during initial application boot process or idle state.
        if (!RuleOfLifeCallStateHelper.isScheduledEventActive(this)) {
            return
        }

        // Trigger IncomingCallActivity as full screen overlay with high priority
        val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as android.app.KeyguardManager
        val isScreenInteractive = powerManager.isInteractive
        val isKeyguardLocked = keyguardManager.isKeyguardLocked

        try {
            val intent = Intent(this, IncomingCallActivity::class.java).apply {
                // Force fullscreen direct launch via NEW_TASK when screen is interactive and unlocked
                var launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
                if (isScreenInteractive && !isKeyguardLocked) {
                    launchFlags = launchFlags or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                } else {
                    launchFlags = launchFlags or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                flags = launchFlags
                putExtra("EXTRA_CALL_TITLE", "Rule of Life Scheduled Call")
                putExtra("EXTRA_CALL_MESSAGE", "Incoming Rule of Life call from: $phoneNumber")
                putExtra("EXTRA_NOTIFICATION_ID", 1003)
                putExtra("EXTRA_DEVICE_ACTIVE", isScreenInteractive && !isKeyguardLocked)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

