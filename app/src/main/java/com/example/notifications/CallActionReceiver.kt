package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.data.CallLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 1001)
        val title = intent.getStringExtra("EXTRA_CALL_TITLE") ?: "Rule of Life Call"
        val message = intent.getStringExtra("EXTRA_CALL_MESSAGE") ?: "Spiritual Rule Reminder"

        // Stop ringer audio immediately
        CallRingerManager.stopRinging()

        // Stop the foreground call service
        RuleOfLifeCallService.stopCall(context, notificationId)

        // Cancel the system notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.cancel(notificationId)

        when (action) {
            ACTION_MARK_COMPLETED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dutyId = if (notificationId >= 1000) notificationId - 1000 else -1
                        val db = AppDatabase.getDatabase(context)
                        val duty = if (dutyId > 0) db.appDao().getDutyById(dutyId) else null
                        if (duty != null) {
                            val repo = com.example.data.SpiritualRepository(db.appDao())
                            if (!duty.isCompleted) {
                                repo.toggleDutyCompletion(duty)
                            }
                            NotificationScheduler.cancelDutyNotification(context, dutyId)
                        }

                        val callRepo = CallLogRepository.getInstance(context)
                        callRepo.logCall(title, "Marked as completed: $message", "ANSWERED")

                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "Marked '$title' completed for today.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try { pendingResult.finish() } catch (_: Exception) {}
                    }
                }
            }

            ACTION_ANSWER_CALL -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val repository = CallLogRepository.getInstance(context)
                        repository.logCall(title, message, "ANSWERED")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try { pendingResult.finish() } catch (_: Exception) {}
                    }
                }

                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    setAction(ACTION_ANSWER_CALL)
                    putExtra("EXTRA_CALL_TITLE", title)
                    putExtra("EXTRA_CALL_MESSAGE", message)
                    putExtra("EXTRA_NOTIFICATION_ID", notificationId)
                }
                context.startActivity(mainIntent)
            }

            ACTION_DECLINE_CALL -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val repository = CallLogRepository.getInstance(context)
                        repository.logCall(title, message, "DECLINED")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try { pendingResult.finish() } catch (_: Exception) {}
                    }
                }

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Rule of Life call declined.", Toast.LENGTH_SHORT).show()
                }
            }

            ACTION_SNOOZE_15 -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val cal = Calendar.getInstance().apply {
                            add(Calendar.MINUTE, 15)
                        }
                        val formattedTime = String.format(Locale.US, "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                        val displayTime = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)

                        val dutyId = if (notificationId >= 1000) notificationId - 1000 else -1
                        val db = AppDatabase.getDatabase(context)
                        val duty = if (dutyId > 0) db.appDao().getDutyById(dutyId) else null

                        if (duty != null) {
                            val today = RescheduleManager.getTodayDateString()
                            RescheduleManager.setReschedule(context, dutyId, today, formattedTime)
                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                            if (alarmManager != null) {
                                NotificationScheduler.scheduleDutyNotification(context, alarmManager, duty)
                            }
                        } else {
                            NotificationScheduler.scheduleOneOffReminder(
                                context,
                                title,
                                message,
                                formattedTime,
                                notificationId
                            )
                        }

                        val repository = CallLogRepository.getInstance(context)
                        repository.logCall(title, "Postponed 15 min (until $displayTime): $message", "RESCHEDULED")

                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "Postponed '$title' by 15m (rings at $displayTime).",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try { pendingResult.finish() } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_ANSWER_CALL = "com.example.notifications.CallActionReceiver.ACTION_ANSWER_CALL"
        const val ACTION_DECLINE_CALL = "com.example.notifications.CallActionReceiver.ACTION_DECLINE_CALL"
        const val ACTION_SNOOZE_15 = "com.example.notifications.CallActionReceiver.ACTION_SNOOZE_15"
        const val ACTION_MARK_COMPLETED = "com.example.notifications.CallActionReceiver.ACTION_MARK_COMPLETED"
    }
}
