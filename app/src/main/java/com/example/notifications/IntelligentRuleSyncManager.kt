package com.example.notifications

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import com.example.data.SpiritualDuty
import com.example.data.UserProfile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Intelligent Real-Time Rule Synchronization Engine.
 * Continuously observes the SpiritualDuty table in real-time, analyzes diffs between
 * state transitions, and performs surgical, punctual alarm dispatching:
 * - Added Rules: Instantly calculates exact timestamp and registers alarm.
 * - Modified Rules (Time, Style, Frequency, Days, Title, Completion): Cancels stale alarms,
 *   recalculates millisecond triggers, and registers updated alarms.
 * - Deleted Rules: Immediately cancels active AlarmManager intents, stops ringers, and clears notifications.
 * - Completed Rules: Disarms alarms for current cycle and schedules next recurrence.
 */
object IntelligentRuleSyncManager {

    private const val TAG = "RuleSyncEngine"
    private val syncMutex = Mutex()
    private val previousDutiesMap = mutableMapOf<Int, SpiritualDuty>()
    private var isInitialized = false

    /**
     * Synchronizes the active alarms with the fresh list of SpiritualDuty items.
     * Detects additions, edits, deletions, and toggles in real time.
     */
    suspend fun syncDuties(
        context: Context,
        currentDuties: List<SpiritualDuty>,
        profile: UserProfile?
    ) {
        syncMutex.withLock {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val currentMap = currentDuties.associateBy { it.id }

            if (!isInitialized) {
                Log.d(TAG, "Initial sync of ${currentDuties.size} rules. Scheduling all active alarms...")
                NotificationScheduler.scheduleAll(
                    context,
                    currentDuties,
                    profile?.saintNotificationTime ?: "07:30",
                    profile
                )
                previousDutiesMap.clear()
                previousDutiesMap.putAll(currentMap)
                isInitialized = true
                return
            }

            // 1. Identify and process Deleted Rules
            val deletedIds = previousDutiesMap.keys - currentMap.keys
            for (deletedId in deletedIds) {
                val deletedDuty = previousDutiesMap[deletedId]
                Log.i(TAG, "⚡ [DELETED RULE DETECTED] id=$deletedId ('${deletedDuty?.title}'). Canceling system alarm and ringers.")
                NotificationScheduler.cancelDutyNotification(context, deletedId)
            }

            // 2. Identify and process Newly Added Rules
            val addedIds = currentMap.keys - previousDutiesMap.keys
            for (addedId in addedIds) {
                val newDuty = currentMap[addedId] ?: continue
                Log.i(TAG, "⚡ [NEW RULE ADDED] id=$addedId ('${newDuty.title}') time=${newDuty.reminderTime} style=${newDuty.notificationStyle}. Calculating exact trigger...")
                NotificationScheduler.scheduleDutyNotification(context, alarmManager, newDuty)
            }

            // 3. Identify and process Modified Rules
            val existingIds = currentMap.keys.intersect(previousDutiesMap.keys)
            for (existingId in existingIds) {
                val oldDuty = previousDutiesMap[existingId] ?: continue
                val newDuty = currentMap[existingId] ?: continue

                val timeChanged = oldDuty.reminderTime != newDuty.reminderTime
                val styleChanged = oldDuty.notificationStyle != newDuty.notificationStyle
                val freqChanged = oldDuty.frequency != newDuty.frequency
                val daysOfWeekChanged = oldDuty.scheduledDaysOfWeek != newDuty.scheduledDaysOfWeek
                val daysOfMonthChanged = oldDuty.scheduledDaysOfMonth != newDuty.scheduledDaysOfMonth
                val daysOfYearChanged = oldDuty.scheduledDaysOfYear != newDuty.scheduledDaysOfYear
                val completionChanged = oldDuty.isCompleted != newDuty.isCompleted
                val titleChanged = oldDuty.title != newDuty.title
                val descChanged = oldDuty.description != newDuty.description

                val hasChanges = timeChanged || styleChanged || freqChanged || daysOfWeekChanged ||
                        daysOfMonthChanged || daysOfYearChanged || completionChanged || titleChanged || descChanged

                if (hasChanges) {
                    Log.i(
                        TAG,
                        "⚡ [RULE MODIFIED] id=$existingId ('${newDuty.title}') -> " +
                                "timeChanged=$timeChanged (${oldDuty.reminderTime} -> ${newDuty.reminderTime}), " +
                                "styleChanged=$styleChanged (${oldDuty.notificationStyle} -> ${newDuty.notificationStyle}), " +
                                "freqChanged=$freqChanged, completionChanged=$completionChanged. Re-dispatching exact alarm."
                    )
                    NotificationScheduler.cancelDutyNotification(context, existingId)
                    NotificationScheduler.scheduleDutyNotification(context, alarmManager, newDuty)
                }
            }

            // Update cached snapshot
            previousDutiesMap.clear()
            previousDutiesMap.putAll(currentMap)
        }
    }

    /**
     * Resets the cached snapshot, forcing a complete re-sync on the next emission.
     */
    fun invalidateCache() {
        isInitialized = false
        previousDutiesMap.clear()
    }
}
