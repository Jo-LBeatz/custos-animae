package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * High-performance BootReceiver listening for device restart and app package update events.
 * Automatically reconciles database state and reschedules all exact Rule of Life alarms.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("BootReceiver", "BootReceiver onReceive triggered: action=$action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            RuleOfLifeCallStateHelper.markBootCompleted(context)
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val duties = db.appDao().getAllDutiesSync()
                    val profile = db.appDao().getUserProfileSync()
                    Log.d(
                        "BootReceiver",
                        "Device boot/update detected. Re-scheduling alarms for ${duties.size} spiritual duties..."
                    )
                    NotificationScheduler.scheduleAll(
                        context,
                        duties,
                        profile?.saintNotificationTime ?: "07:30",
                        profile
                    )
                    Log.d("BootReceiver", "Successfully rescheduled all Rule of Life alarms after $action")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error reconciling Rule of Life alarms on reboot/update", e)
                } finally {
                    try {
                        pendingResult.finish()
                    } catch (_: Exception) {}
                }
            }
        }
    }
}
