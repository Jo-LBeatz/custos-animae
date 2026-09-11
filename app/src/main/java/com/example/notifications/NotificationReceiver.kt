package com.example.notifications

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.calendar.SaintData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

object RuleTriggerTracker {
    private const val PREFS_NAME = "rule_trigger_tracker_prefs"

    fun shouldTriggerAndRecord(context: Context, ruleKey: String, windowMs: Long = 2_000L): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastTriggered = prefs.getLong("last_triggered_$ruleKey", 0L)
        val now = System.currentTimeMillis()
        if (now - lastTriggered < windowMs) {
            return false
        }
        prefs.edit().putLong("last_triggered_$ruleKey", now).apply()
        return true
    }
}

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "CustosAnimae:NotificationReceiverWakeLock"
        )?.apply {
            setReferenceCounted(false)
            acquire(15_000L) // 15-second timeout safeguard
        }

        val action = intent.action
        Log.d("NotificationReceiver", "onReceive triggered: action=$action, extraType=${intent.getStringExtra("EXTRA_TYPE")}")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_TIME_CHANGED) {
            RuleOfLifeCallStateHelper.markBootCompleted(context)
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val duties = db.appDao().getAllDutiesSync()
                    val profile = db.appDao().getUserProfileSync()
                    NotificationScheduler.scheduleAll(
                        context,
                        duties,
                        profile?.saintNotificationTime ?: "07:30",
                        profile
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (wakeLock?.isHeld == true) wakeLock.release()
                    } catch (_: Exception) {}
                    try { pendingResult.finish() } catch (_: Exception) {}
                }
            }
            return
        }

        val type = intent.getStringExtra("EXTRA_TYPE") ?: "DUTY"
        if (type == "SAINT" || action == NotificationScheduler.ACTION_SAINT_DAY) {
            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "SAINT_9999")) {
                Log.d("NotificationReceiver", "Duplicate SAINT alarm trigger suppressed within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }
            val cal = Calendar.getInstance()
            val mmdd = String.format(Locale.US, "%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            val saint = SaintData.getSaintForDate(mmdd) ?: SaintData.saintList.first()
            val title = "Saint of the Day: ${saint.saintName}"
            val message = "${saint.feastTitle} • \"${saint.quote}\""
            dispatchReminder(context, title, message, notificationId = 9999, wakeLock = wakeLock)
        } else if (type == "PRAYER") {
            val notificationId = intent.getIntExtra("EXTRA_ID", 7001)
            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "PRAYER_$notificationId")) {
                Log.d("NotificationReceiver", "Duplicate PRAYER alarm trigger suppressed for id=$notificationId within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }
            val title = intent.getStringExtra("EXTRA_TITLE") ?: "Daily Prayer"
            val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Time for your daily prayer."
            dispatchReminder(context, title, message, notificationId, wakeLock = wakeLock)
        } else if (type == "SACRED_HEART") {
            val notificationId = intent.getIntExtra("EXTRA_ID", 7101)
            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "SACRED_HEART_$notificationId")) {
                Log.d("NotificationReceiver", "Duplicate SACRED_HEART alarm trigger suppressed within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }
            val cal = Calendar.getInstance()
            val isFirstFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
            val title = if (isFirstFriday) "First Friday Devotion: Sacred Heart of Jesus" else "Friday Devotion: Sacred Heart of Jesus"
            val message = if (isFirstFriday) {
                "Offer your Holy Communion, prayers, and acts of reparation to the Sacred Heart of Jesus today."
            } else {
                "In honor of Christ's Passion, dedicate today's prayers and mortifications to His Most Sacred Heart."
            }
            dispatchReminder(context, title, message, notificationId, wakeLock = wakeLock)
        } else if (type == "IMMACULATE_HEART") {
            val notificationId = intent.getIntExtra("EXTRA_ID", 7102)
            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "IMMACULATE_HEART_$notificationId")) {
                Log.d("NotificationReceiver", "Duplicate IMMACULATE_HEART alarm trigger suppressed within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }
            val cal = Calendar.getInstance()
            val isFirstSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
            val title = if (isFirstSaturday) "First Saturday Devotion: Immaculate Heart of Mary" else "Saturday Devotion: Immaculate Heart of Mary"
            val message = if (isFirstSaturday) {
                "Make reparation to the Immaculate Heart of Mary with the Rosary, meditation, and confession."
            } else {
                "Dedicate today's prayers, Rosary, and offerings in loving reparation to the Immaculate Heart of Mary."
            }
            dispatchReminder(context, title, message, notificationId, wakeLock = wakeLock)
        } else if (type == "LITURGICAL_SEASON") {
            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "LITURGICAL_SEASON_7103")) {
                Log.d("NotificationReceiver", "Duplicate LITURGICAL_SEASON alarm trigger suppressed within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }
            val today = Calendar.getInstance()
            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH) + 1
            val day = today.get(Calendar.DAY_OF_MONTH)
            val todayInfo = com.example.data.calendar.Roman1954Calendar.getLiturgicalInfoForDate(year, month, day)
            
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            val yYear = yesterday.get(Calendar.YEAR)
            val yMonth = yesterday.get(Calendar.MONTH) + 1
            val yDay = yesterday.get(Calendar.DAY_OF_MONTH)
            val yesterdayInfo = com.example.data.calendar.Roman1954Calendar.getLiturgicalInfoForDate(yYear, yMonth, yDay)
            
            if (todayInfo.season != yesterdayInfo.season) {
                val title = "Liturgical Season: ${todayInfo.season.displayName}"
                val message = "Today begins the ${todayInfo.season.displayName}. Vestment color: ${todayInfo.vestmentColor}."
                dispatchReminder(context, title, message, 7103, wakeLock = wakeLock)
            } else {
                try {
                    if (wakeLock?.isHeld == true) wakeLock.release()
                } catch (_: Exception) {}
            }
        } else if (type == "TEST") {
            val title = intent.getStringExtra("EXTRA_TITLE") ?: "Test Rule Reminder"
            val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "This is a test Rule of Life reminder."
            val notificationId = intent.getIntExtra("EXTRA_ID", 12345)
            val testStyle = intent.getStringExtra("EXTRA_STYLE") ?: "CALL_OVERLAY"
            Log.d("NotificationReceiver", "[ALARM FIRED] Test Reminder triggered: style=$testStyle, notificationId=$notificationId")
            RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)
            if (testStyle == "BANNER") {
                NotificationHelper.showNotification(context, title, message, notificationId)
            } else {
                NotificationHelper.showIncomingCallNotification(context, title, message, notificationId)
            }
            try {
                if (wakeLock?.isHeld == true) wakeLock.release()
            } catch (_: Exception) {}
        } else {
            val dutyId = intent.getIntExtra("EXTRA_ID", -1)
            if (dutyId <= 0) {
                try {
                    if (wakeLock?.isHeld == true) wakeLock.release()
                } catch (_: Exception) {}
                return
            }

            if (!RuleTriggerTracker.shouldTriggerAndRecord(context, "DUTY_$dutyId")) {
                Log.d("NotificationReceiver", "Duplicate DUTY alarm trigger suppressed for dutyId=$dutyId within minute window.")
                try { if (wakeLock?.isHeld == true) wakeLock.release() } catch (_: Exception) {}
                return
            }

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val duty = db.appDao().getDutyById(dutyId)
                    val profile = db.appDao().getUserProfileSync()

                    if (duty != null) {
                        val title = duty.title
                        val displayMessage = if (duty.isCompleted) {
                            "'$title' task completed."
                        } else {
                            if (duty.description.isNotBlank()) {
                                duty.description
                            } else {
                                "Spiritual Duty: '$title' is due. Perform this duty faithfully for God's glory."
                            }
                        }

                        RescheduleManager.clearReschedule(context, dutyId)
                        RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)

                        val effectiveStyle = when (duty.notificationStyle) {
                            "CALL_OVERLAY" -> "CALL_OVERLAY"
                            "BANNER" -> "BANNER"
                            else -> profile?.ruleNotificationStyle ?: "CALL_OVERLAY"
                        }
                        Log.d(
                            "NotificationReceiver",
                            "[ALARM FIRED] Duty Reminder triggered for dutyId=$dutyId (${duty.title}). Effective ruleNotificationStyle=$effectiveStyle"
                        )

                        if (effectiveStyle == "BANNER") {
                            NotificationHelper.showNotification(
                                context,
                                title,
                                displayMessage,
                                notificationId = 1000 + duty.id
                            )
                        } else {
                            NotificationHelper.showIncomingCallNotification(
                                context,
                                title,
                                displayMessage,
                                notificationId = 1000 + duty.id
                            )
                        }

                        // Re-schedule alarm for repeating schedule starting from the next minute onwards
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        if (alarmManager != null) {
                            NotificationScheduler.scheduleDutyNotification(
                                context,
                                alarmManager,
                                duty,
                                fromTimeMs = System.currentTimeMillis() + 60_000L
                            )
                        }
                    } else {
                        NotificationScheduler.cancelDutyNotification(context, dutyId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (wakeLock?.isHeld == true) wakeLock.release()
                    } catch (_: Exception) {}
                    try { pendingResult.finish() } catch (_: Exception) {}
                }
            }
        }
    }

    private fun dispatchReminder(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        wakeLock: PowerManager.WakeLock? = null
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)
                NotificationHelper.showNotification(context, title, message, notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (wakeLock?.isHeld == true) wakeLock.release()
                } catch (_: Exception) {}
                try { pendingResult.finish() } catch (_: Exception) {}
            }
        }
    }
}
