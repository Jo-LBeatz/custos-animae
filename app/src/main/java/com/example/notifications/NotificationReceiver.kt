package com.example.notifications

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import com.example.data.calendar.SaintData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
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
                    try { pendingResult.finish() } catch (_: Exception) {}
                }
            }
            return
        }

        val type = intent.getStringExtra("EXTRA_TYPE") ?: "DUTY"
        if (type == "SAINT" || action == NotificationScheduler.ACTION_SAINT_DAY) {
            val cal = Calendar.getInstance()
            val mmdd = String.format(Locale.US, "%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            val saint = SaintData.getSaintForDate(mmdd) ?: SaintData.saintList.first()
            val title = "Saint of the Day: ${saint.saintName}"
            val message = "${saint.feastTitle} • \"${saint.quote.take(120)}\""
            dispatchReminder(context, title, message, notificationId = 9999)
        } else if (type == "PRAYER") {
            val title = intent.getStringExtra("EXTRA_TITLE") ?: "Daily Prayer"
            val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Time for your daily prayer."
            val notificationId = intent.getIntExtra("EXTRA_ID", 7001)
            dispatchReminder(context, title, message, notificationId)
        } else if (type == "SACRED_HEART") {
            val cal = Calendar.getInstance()
            val isFirstFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
            val title = if (isFirstFriday) "First Friday Devotion: Sacred Heart of Jesus" else "Friday Devotion: Sacred Heart of Jesus"
            val message = if (isFirstFriday) {
                "Offer your Holy Communion, prayers, and acts of reparation to the Sacred Heart of Jesus today."
            } else {
                "In honor of Christ's Passion, dedicate today's prayers and mortifications to His Most Sacred Heart."
            }
            val notificationId = intent.getIntExtra("EXTRA_ID", 7101)
            dispatchReminder(context, title, message, notificationId)
        } else if (type == "IMMACULATE_HEART") {
            val cal = Calendar.getInstance()
            val isFirstSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
            val title = if (isFirstSaturday) "First Saturday Devotion: Immaculate Heart of Mary" else "Saturday Devotion: Immaculate Heart of Mary"
            val message = if (isFirstSaturday) {
                "Make reparation to the Immaculate Heart of Mary with the Rosary, meditation, and confession."
            } else {
                "Dedicate today's prayers, Rosary, and offerings in loving reparation to the Immaculate Heart of Mary."
            }
            val notificationId = intent.getIntExtra("EXTRA_ID", 7102)
            dispatchReminder(context, title, message, notificationId)
        } else if (type == "LITURGICAL_SEASON") {
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
                dispatchReminder(context, title, message, 7103)
            }
        } else if (type == "NOVENA_START") {
            val today = Calendar.getInstance()
            val novenasStartingToday = com.example.data.novena.NovenaData.getNovenasStartingOnDate(today)
            if (novenasStartingToday.isNotEmpty()) {
                novenasStartingToday.forEachIndexed { index, novena ->
                    NotificationHelper.showNovenaStartBannerNotification(context, novena, notificationId = 8000 + index)
                }
            }
        } else if (type == "TEST_NOVENA_START") {
            val sampleNovena = com.example.data.novena.NovenaData.allNovenas.firstOrNull() ?: com.example.data.novena.NovenaData.sampleNovena
            NotificationHelper.showNovenaStartBannerNotification(context, sampleNovena, notificationId = 8999)
        } else if (type == "TEST") {
            val title = intent.getStringExtra("EXTRA_TITLE") ?: "Test Rule Reminder"
            val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "This is a test Rule of Life reminder."
            val notificationId = intent.getIntExtra("EXTRA_ID", 12345)
            RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)
            NotificationHelper.showIncomingCallNotification(context, title, message, notificationId)
        } else {
            val dutyId = intent.getIntExtra("EXTRA_ID", -1)
            if (dutyId <= 0) return
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
                        NotificationHelper.showIncomingCallNotification(
                            context,
                            title,
                            displayMessage,
                            notificationId = 1000 + duty.id
                        )

                        // Re-schedule alarm for repeating schedule
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        if (alarmManager != null) {
                            NotificationScheduler.scheduleDutyNotification(context, alarmManager, duty)
                        }
                    } else {
                        NotificationScheduler.cancelDutyNotification(context, dutyId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try { pendingResult.finish() } catch (_: Exception) {}
                }
            }
        }
    }

    private fun dispatchReminder(context: Context, title: String, message: String, notificationId: Int) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val profile = db.appDao().getUserProfileSync()

                RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)
                NotificationHelper.showNotification(context, title, message, notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try { pendingResult.finish() } catch (_: Exception) {}
            }
        }
    }
}
