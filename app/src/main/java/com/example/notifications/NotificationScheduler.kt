package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.SpiritualDuty
import java.util.Calendar
import java.util.Locale

object NotificationScheduler {

    const val ACTION_SAINT_DAY = "com.example.ACTION_SAINT_DAY"
    const val ACTION_DUTY_REMINDER = "com.example.ACTION_DUTY_REMINDER"

    fun scheduleAll(
        context: Context,
        duties: List<SpiritualDuty>,
        saintNotificationTime: String = "07:30",
        profile: com.example.data.UserProfile? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val notificationsEnabled = profile?.notificationEnabled != false

        if (!notificationsEnabled) {
            // Cancel alarms if master switch is off
            return
        }

        // 1. Schedule Saint of the Day daily notification
        scheduleSaintOfDay(context, saintNotificationTime)

        // 2. Schedule Daily Prayer Alarms (Morning Prayer, Evening Prayer, Angelus 6am/12pm/6pm)
        if (profile != null) {
            schedulePrayerReminder(
                context,
                "Morning Prayer (Lauds / Prime)",
                "Time for your morning prayer and daily offering.",
                profile.morningPrayerTime,
                intentId = 7001,
                enabled = profile.notificationEnabled
            )
            schedulePrayerReminder(
                context,
                "Evening Prayer / Night Examen",
                "Time for your evening Rosary and Examen of Conscience.",
                profile.eveningPrayerTime,
                intentId = 7002,
                enabled = profile.notificationEnabled
            )

            // Angelus alarms
            schedulePrayerReminder(
                context,
                "The Angelus (Morning)",
                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost.",
                profile.angelusMorningTime,
                intentId = 7003,
                enabled = profile.angelusEnabled && profile.notificationEnabled
            )
            schedulePrayerReminder(
                context,
                "The Angelus (Noon)",
                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost.",
                profile.angelusNoonTime,
                intentId = 7004,
                enabled = profile.angelusEnabled && profile.notificationEnabled
            )
            schedulePrayerReminder(
                context,
                "The Angelus (Evening)",
                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost.",
                profile.angelusEveningTime,
                intentId = 7005,
                enabled = profile.angelusEnabled && profile.notificationEnabled
            )

            // Sacred Heart of Jesus devotion (Every Friday & First Fridays)
            scheduleDevotionReminder(
                context,
                "SACRED_HEART",
                profile.sacredHeartTime,
                intentId = 7101,
                enabled = profile.sacredHeartEnabled && profile.notificationEnabled
            )

            // Immaculate Heart of Mary devotion (Every Saturday & First Saturdays)
            scheduleDevotionReminder(
                context,
                "IMMACULATE_HEART",
                profile.immaculateHeartTime,
                intentId = 7102,
                enabled = profile.immaculateHeartEnabled && profile.notificationEnabled
            )
        }


            // Liturgical Season Notifications (Daily check at 8:00 AM)
            scheduleDevotionReminder(
                context,
                "LITURGICAL_SEASON",
                "08:00",
                intentId = 7103,
                enabled = profile?.liturgicalSeasonNotificationsEnabled == true && profile.notificationEnabled == true,
                isDaily = true
            )
        // 3. Schedule Task Notifications for Daily, Weekly, Monthly, Yearly duties
        duties.forEach { duty ->
            scheduleDutyNotification(context, alarmManager, duty)
        }


    }

    fun scheduleDevotionReminder(
        context: Context,
        type: String,
        timeStr: String,
        intentId: Int,
        enabled: Boolean = true,
        isDaily: Boolean = false
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", type)
            putExtra("EXTRA_ID", intentId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            try {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        val (hour, minute) = parseHourAndMinute(timeStr)
        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        
        if (isDaily) {
            if (cal.timeInMillis <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            val targetDayOfWeek = if (type == "SACRED_HEART") Calendar.FRIDAY else Calendar.SATURDAY
            while (cal.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek || cal.timeInMillis <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                if (isDaily) AlarmManager.INTERVAL_DAY else AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun schedulePrayerReminder(
        context: Context,
        title: String,
        message: String,
        timeStr: String,
        intentId: Int,
        enabled: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "PRAYER")
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
            putExtra("EXTRA_ID", intentId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            try {
                alarmManager.cancel(pendingIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        val (hour, minute) = parseHourAndMinute(timeStr)

        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun parseHourAndMinute(timeStr: String): Pair<Int, Int> {
        val clean = timeStr.trim().uppercase(Locale.ROOT)
        val isPM = clean.contains("PM")
        val isAM = clean.contains("AM")
        val digitsOnly = clean.replace("AM", "").replace("PM", "").trim()
        val parts = digitsOnly.split(":")
        var hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        if (isPM && hour < 12) {
            hour += 12
        } else if (isAM && hour == 12) {
            hour = 0
        }
        return Pair(hour, minute)
    }

    fun scheduleSaintOfDay(context: Context, timeStr: String = "07:30") {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SAINT_DAY
            putExtra("EXTRA_TYPE", "SAINT")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (hour, minute) = parseHourAndMinute(timeStr)

        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelDutyNotification(context: Context, dutyId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "DUTY")
            putExtra("EXTRA_ID", dutyId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1000 + dutyId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            try {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun mapDayOfWeekToCalendar(dayStr: String): Int {
        val clean = dayStr.trim().lowercase(Locale.ROOT)
        return when {
            clean.contains("sun") -> Calendar.SUNDAY
            clean.contains("mon") -> Calendar.MONDAY
            clean.contains("tue") -> Calendar.TUESDAY
            clean.contains("wed") -> Calendar.WEDNESDAY
            clean.contains("thu") -> Calendar.THURSDAY
            clean.contains("fri") -> Calendar.FRIDAY
            clean.contains("sat") -> Calendar.SATURDAY
            else -> Calendar.MONDAY
        }
    }

    fun calculateNextTriggerTime(duty: SpiritualDuty, fromTimeMs: Long = System.currentTimeMillis()): Long {
        val (hour, minute) = parseHourAndMinute(duty.reminderTime)
        val freq = duty.frequency.uppercase(Locale.ROOT)

        val cal = Calendar.getInstance().apply {
            timeInMillis = fromTimeMs
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (freq) {
            "DAILY" -> {
                if (cal.timeInMillis <= fromTimeMs) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            "WEEKLY" -> {
                val dayStr = duty.scheduledDaysOfWeek.ifBlank { "Monday" }
                val targetDay = mapDayOfWeekToCalendar(dayStr)
                while (cal.get(Calendar.DAY_OF_WEEK) != targetDay || cal.timeInMillis <= fromTimeMs) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            "MONTHLY" -> {
                val rule = duty.scheduledDaysOfMonth.trim().ifBlank { "1" }
                if (rule.contains("_")) {
                    val nextCal = calculateNextRelativeMonthlyCalendar(rule, hour, minute, fromTimeMs)
                    return nextCal.timeInMillis
                } else {
                    val targetDay = rule.toIntOrNull() ?: 1
                    cal.set(Calendar.DAY_OF_MONTH, minOf(targetDay, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                    if (cal.timeInMillis <= fromTimeMs) {
                        cal.add(Calendar.MONTH, 1)
                        cal.set(Calendar.DAY_OF_MONTH, minOf(targetDay, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                    }
                }
            }
            "YEARLY" -> {
                val rule = duty.scheduledDaysOfYear.trim().ifBlank { "01-01" }
                if (rule.contains("_")) {
                    val nextCal = calculateNextRelativeYearlyCalendar(rule, hour, minute, fromTimeMs)
                    return nextCal.timeInMillis
                } else {
                    val parts = rule.split("-")
                    val month = parts.getOrNull(0)?.toIntOrNull() ?: 1
                    val day = parts.getOrNull(1)?.toIntOrNull() ?: 1

                    cal.set(Calendar.MONTH, month - 1)
                    cal.set(Calendar.DAY_OF_MONTH, minOf(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                    if (cal.timeInMillis <= fromTimeMs) {
                        cal.add(Calendar.YEAR, 1)
                        cal.set(Calendar.MONTH, month - 1)
                        cal.set(Calendar.DAY_OF_MONTH, minOf(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                    }
                }
            }
            else -> {
                if (cal.timeInMillis <= fromTimeMs) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        return cal.timeInMillis
    }

    private fun calculateNextRelativeMonthlyCalendar(rule: String, hour: Int, minute: Int, fromTimeMs: Long): Calendar {
        val parts = rule.split("_")
        val ordinal = parts.getOrNull(0)?.uppercase(Locale.ROOT) ?: "1ST"
        val dayOfWeekStr = parts.getOrNull(1) ?: "Monday"
        val targetDayOfWeek = mapDayOfWeekToCalendar(dayOfWeekStr)

        val cal = Calendar.getInstance().apply {
            timeInMillis = fromTimeMs
        }
        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)

        for (attempt in 0..12) {
            val candidate = findOccurrenceInMonth(year, month, ordinal, targetDayOfWeek, hour, minute)
            if (candidate.timeInMillis > fromTimeMs) {
                return candidate
            }
            month++
            if (month > 11) {
                month = 0
                year++
            }
        }
        val fallback = Calendar.getInstance()
        fallback.add(Calendar.MONTH, 1)
        return fallback
    }

    private fun calculateNextRelativeYearlyCalendar(rule: String, hour: Int, minute: Int, fromTimeMs: Long): Calendar {
        val parts = rule.split("_")
        val ordinal = parts.getOrNull(0)?.uppercase(Locale.ROOT) ?: "1ST"
        val dayOfWeekStr = parts.getOrNull(1) ?: "Sunday"
        val monthNum = parts.getOrNull(2)?.toIntOrNull() ?: 1
        val targetDayOfWeek = mapDayOfWeekToCalendar(dayOfWeekStr)
        val targetMonth = (monthNum - 1).coerceIn(0, 11)

        val cal = Calendar.getInstance().apply {
            timeInMillis = fromTimeMs
        }
        var year = cal.get(Calendar.YEAR)

        for (attempt in 0..5) {
            val candidate = findOccurrenceInMonth(year, targetMonth, ordinal, targetDayOfWeek, hour, minute)
            if (candidate.timeInMillis > fromTimeMs) {
                return candidate
            }
            year++
        }
        val fallback = Calendar.getInstance()
        fallback.add(Calendar.YEAR, 1)
        return fallback
    }

    private fun findOccurrenceInMonth(year: Int, month: Int, ordinal: String, targetDayOfWeek: Int, hour: Int, minute: Int): Calendar {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val targetIndex = when (ordinal) {
            "1ST" -> 1
            "2ND" -> 2
            "3RD" -> 3
            "4TH" -> 4
            else -> -1 // Last
        }

        if (targetIndex > 0) {
            var count = 0
            var lastMatch = 1
            for (d in 1..maxDays) {
                cal.set(Calendar.DAY_OF_MONTH, d)
                if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek) {
                    count++
                    lastMatch = d
                    if (count == targetIndex) {
                        return cal
                    }
                }
            }
            cal.set(Calendar.DAY_OF_MONTH, lastMatch)
            return cal
        } else {
            // LST
            for (d in maxDays downTo 1) {
                cal.set(Calendar.DAY_OF_MONTH, d)
                if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek) {
                    return cal
                }
            }
            cal.set(Calendar.DAY_OF_MONTH, maxDays)
            return cal
        }
    }

    fun scheduleDutyNotification(context: Context, alarmManager: AlarmManager, duty: SpiritualDuty) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "DUTY")
            putExtra("EXTRA_ID", duty.id)
            putExtra("EXTRA_TITLE", duty.title)
            putExtra("EXTRA_IS_COMPLETED", duty.isCompleted)
            putExtra("EXTRA_MESSAGE", duty.description.ifBlank { "Reminder: It is time to perform your task '${duty.title}'." })
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1000 + duty.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextTriggerMs = calculateNextTriggerTime(duty)
        setAlarm(alarmManager, nextTriggerMs, pendingIntent)
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerAtMs: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMs,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMs,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMs,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMs,
                    pendingIntent
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun triggerSaintNotificationNow(context: Context) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SAINT_DAY
            putExtra("EXTRA_TYPE", "SAINT")
        }
        context.sendBroadcast(intent)
    }

    fun triggerDutyNotificationNow(context: Context, title: String, message: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "TEST")
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
            putExtra("EXTRA_ID", (System.currentTimeMillis() % 10000).toInt())
        }
        context.sendBroadcast(intent)
    }
}
