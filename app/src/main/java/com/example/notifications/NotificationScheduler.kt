package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.SpiritualDuty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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



    fun parseHourAndMinute(timeStr: String): Pair<Int, Int> {
        val normalized = timeStr.map { ch ->
            when (ch) {
                in '0'..'9' -> ch
                in '\u0660'..'\u0669' -> '0' + (ch - '\u0660')
                in '\u06F0'..'\u06F9' -> '0' + (ch - '\u06F0')
                in '\uFF10'..'\uFF19' -> '0' + (ch - '\uFF10')
                else -> ch
            }
        }.joinToString("")

        val clean = normalized.trim().uppercase(Locale.ROOT)
        val isPM = clean.contains("PM")
        val isAM = clean.contains("AM")
        val digitsOnly = clean.replace("AM", "").replace("PM", "").trim()
        val parts = digitsOnly.split(":")
        var hour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.trim()?.take(2)?.toIntOrNull() ?: 0

        if (isPM && hour < 12) {
            hour += 12
        } else if (isAM && hour == 12) {
            hour = 0
        }
        return Pair(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            try {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Dismiss active notification if any
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        notificationManager?.cancel(1000 + dutyId)

        // Stop ringing active call for this specific duty ID
        RuleOfLifeCallService.stopCall(context, 1000 + dutyId)

        // Clear reschedule state
        RescheduleManager.clearReschedule(context, dutyId)
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

    fun calculateNextTriggerTime(context: Context?, duty: SpiritualDuty, fromTimeMs: Long = System.currentTimeMillis()): Long {
        var targetTime = duty.reminderTime

        if (context != null) {
            val rescheduledTime = RescheduleManager.getRescheduledTimeForToday(context, duty.id)
            if (rescheduledTime != null) {
                targetTime = rescheduledTime
            }
        }

        val (hour, minute) = parseHourAndMinute(targetTime)
        val freq = duty.frequency.uppercase(Locale.ROOT)

        val cal = Calendar.getInstance().apply {
            timeInMillis = fromTimeMs
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val nowCal = Calendar.getInstance().apply { timeInMillis = fromTimeMs }

        val targetHour = cal.get(Calendar.HOUR_OF_DAY)
        val targetMin = cal.get(Calendar.MINUTE)
        val nowHour = nowCal.get(Calendar.HOUR_OF_DAY)
        val nowMin = nowCal.get(Calendar.MINUTE)

        // Check if the scheduled day matches today for weekly/monthly/yearly
        val isScheduledForToday = when (freq) {
            "DAILY", "" -> true
            "WEEKLY" -> {
                val daysList = duty.scheduledDaysOfWeek.split(",")
                    .map { it.trim().lowercase(Locale.ROOT) }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("monday") }
                val currentDayStr = when (nowCal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "sunday"
                    Calendar.MONDAY -> "monday"
                    Calendar.TUESDAY -> "tuesday"
                    Calendar.WEDNESDAY -> "wednesday"
                    Calendar.THURSDAY -> "thursday"
                    Calendar.FRIDAY -> "friday"
                    Calendar.SATURDAY -> "saturday"
                    else -> "monday"
                }
                daysList.any { currentDayStr.contains(it) }
            }
            "MONTHLY" -> {
                val daysList = duty.scheduledDaysOfMonth.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("1") }
                val currentDay = nowCal.get(Calendar.DAY_OF_MONTH).toString()
                daysList.contains(currentDay)
            }
            "YEARLY" -> {
                val rules = duty.scheduledDaysOfYear.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("01-01") }
                val todayMmDd = String.format(Locale.US, "%02d-%02d", nowCal.get(Calendar.MONTH) + 1, nowCal.get(Calendar.DAY_OF_MONTH))
                rules.contains(todayMmDd)
            }
            else -> true
        }

        if (isScheduledForToday) {
            if (targetHour == nowHour && targetMin == nowMin) {
                // Scheduled for the current minute: fire immediately in 2 seconds
                Log.d("NotificationScheduler", "Close-interval trigger: rule is scheduled for current minute ($targetHour:$targetMin), firing in 2s")
                return fromTimeMs + 2000L
            }

            val targetTimeMillis = cal.timeInMillis
            val diffMs = targetTimeMillis - fromTimeMs

            if (diffMs in 1L..60_000L) {
                // Scheduled within the next 60 seconds
                if (diffMs < 5000L) {
                    Log.d("NotificationScheduler", "Close-interval trigger: target time is in $diffMs ms (<5s), firing in 5s")
                    return fromTimeMs + 5000L
                } else {
                    Log.d("NotificationScheduler", "Close-interval trigger: target time is in $diffMs ms, scheduling exact target")
                    return targetTimeMillis
                }
            }
        }

        // If a rescheduled time for today was used but has passed (and wasn't current/next minute), clear it
        if (context != null && RescheduleManager.hasActiveRescheduleForToday(context, duty.id)) {
            val rTime = RescheduleManager.getRescheduledTimeForToday(context, duty.id)
            if (rTime != null) {
                val (rH, rM) = parseHourAndMinute(rTime)
                if (rH < nowHour || (rH == nowHour && rM < nowMin)) {
                    RescheduleManager.clearReschedule(context, duty.id)
                }
            }
        }

        when (freq) {
            "DAILY" -> {
                if (cal.timeInMillis <= fromTimeMs) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            "WEEKLY" -> {
                val daysList = duty.scheduledDaysOfWeek.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("Monday") }

                val candidates = daysList.map { dayStr ->
                    val targetDay = mapDayOfWeekToCalendar(dayStr)
                    val c = Calendar.getInstance().apply {
                        timeInMillis = fromTimeMs
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    while (c.get(Calendar.DAY_OF_WEEK) != targetDay || c.timeInMillis <= fromTimeMs) {
                        c.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    c.timeInMillis
                }
                return candidates.minOrNull() ?: cal.timeInMillis
            }
            "MONTHLY" -> {
                val rules = duty.scheduledDaysOfMonth.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("1") }

                val candidates = rules.map { rule ->
                    if (rule.contains("_")) {
                        calculateNextRelativeMonthlyCalendar(rule, hour, minute, fromTimeMs).timeInMillis
                    } else {
                        val targetDay = rule.toIntOrNull() ?: 1
                        val c = Calendar.getInstance().apply {
                            timeInMillis = fromTimeMs
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                            set(Calendar.DAY_OF_MONTH, minOf(targetDay, getActualMaximum(Calendar.DAY_OF_MONTH)))
                        }
                        val isToday = c.get(Calendar.DAY_OF_MONTH) == targetDay
                        val timeDiff = fromTimeMs - c.timeInMillis
                        if (isToday && timeDiff in 0..59_999L) {
                            fromTimeMs + 2000L
                        } else {
                            if (c.timeInMillis <= fromTimeMs) {
                                c.add(Calendar.MONTH, 1)
                                c.set(Calendar.DAY_OF_MONTH, minOf(targetDay, c.getActualMaximum(Calendar.DAY_OF_MONTH)))
                            }
                            c.timeInMillis
                        }
                    }
                }
                return candidates.minOrNull() ?: cal.timeInMillis
            }
            "YEARLY" -> {
                val rules = duty.scheduledDaysOfYear.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("01-01") }

                val candidates = rules.map { rule ->
                    if (rule.contains("_")) {
                        calculateNextRelativeYearlyCalendar(rule, hour, minute, fromTimeMs).timeInMillis
                    } else {
                        val parts = rule.split("-")
                        val month = parts.getOrNull(0)?.toIntOrNull() ?: 1
                        val day = parts.getOrNull(1)?.toIntOrNull() ?: 1
                        val c = Calendar.getInstance().apply {
                            timeInMillis = fromTimeMs
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                            set(Calendar.MONTH, month - 1)
                            set(Calendar.DAY_OF_MONTH, minOf(day, getActualMaximum(Calendar.DAY_OF_MONTH)))
                        }
                        if (c.timeInMillis <= fromTimeMs) {
                            c.add(Calendar.YEAR, 1)
                            c.set(Calendar.MONTH, month - 1)
                            c.set(Calendar.DAY_OF_MONTH, minOf(day, c.getActualMaximum(Calendar.DAY_OF_MONTH)))
                        }
                        c.timeInMillis
                    }
                }
                return candidates.minOrNull() ?: cal.timeInMillis
            }
            else -> {
                if (cal.timeInMillis <= fromTimeMs) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        return maxOf(cal.timeInMillis, fromTimeMs + 1000L)
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

    fun scheduleDutyNotification(
        context: Context,
        alarmManager: AlarmManager,
        duty: SpiritualDuty,
        fromTimeMs: Long = System.currentTimeMillis()
    ) {
        if (duty.isCompleted) {
            // Duty is marked completed (early or on time): disarm active alarms, notifications, and call service for today
            cancelDutyNotification(context, duty.id)

            // Schedule the duty for the next cycle (after today)
            val endOfToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_DUTY_REMINDER
                putExtra("EXTRA_TYPE", "DUTY")
                putExtra("EXTRA_ID", duty.id)
                putExtra("EXTRA_TITLE", duty.title)
                putExtra("EXTRA_STYLE", duty.notificationStyle)
                putExtra("EXTRA_IS_COMPLETED", true)
                putExtra("EXTRA_MESSAGE", duty.description.ifBlank { "Reminder: It is time to perform your task '${duty.title}'." })
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + duty.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val nextTriggerMs = calculateNextTriggerTime(context, duty, fromTimeMs = endOfToday)
            setAlarm(context, alarmManager, nextTriggerMs, pendingIntent, requestCode = 1000 + duty.id)
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "DUTY")
            putExtra("EXTRA_ID", duty.id)
            putExtra("EXTRA_TITLE", duty.title)
            putExtra("EXTRA_STYLE", duty.notificationStyle)
            putExtra("EXTRA_IS_COMPLETED", duty.isCompleted)
            putExtra("EXTRA_MESSAGE", duty.description.ifBlank { "Reminder: It is time to perform your task '${duty.title}'." })
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1000 + duty.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextTriggerMs = calculateNextTriggerTime(context, duty, fromTimeMs = fromTimeMs)
        setAlarm(context, alarmManager, nextTriggerMs, pendingIntent, requestCode = 1000 + duty.id)
    }

    private fun setAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerAtMs: Long,
        pendingIntent: PendingIntent,
        requestCode: Int = 0
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val targetFormatted = sdf.format(Date(triggerAtMs))
        val diffSec = (triggerAtMs - System.currentTimeMillis()) / 1000
        Log.d("NotificationScheduler", "Attempting to schedule alarm for $targetFormatted (in ${diffSec}s) [reqCode=$requestCode]")

        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        try {
            if (canExact) {
                val showIntent = Intent(context, com.example.MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    showIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMs, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                Log.d("NotificationScheduler", "Scheduled via setAlarmClock for $targetFormatted [reqCode=$requestCode]")
                return
            }
        } catch (e: Exception) {
            Log.w("NotificationScheduler", "setAlarmClock failed, falling back", e)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canExact) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMs,
                        pendingIntent
                    )
                    Log.d("NotificationScheduler", "Scheduled via setExactAndAllowWhileIdle for $targetFormatted")
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMs,
                        pendingIntent
                    )
                    Log.d("NotificationScheduler", "Scheduled via setAndAllowWhileIdle for $targetFormatted")
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMs,
                    pendingIntent
                )
                Log.d("NotificationScheduler", "Scheduled via alarmManager.set for $targetFormatted")
            }
        } catch (ex: Exception) {
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMs,
                    pendingIntent
                )
                Log.d("NotificationScheduler", "Final fallback scheduled via setAndAllowWhileIdle for $targetFormatted")
            } catch (inner: Exception) {
                Log.e("NotificationScheduler", "All alarm scheduling attempts failed", inner)
            }
        }
    }

    fun triggerSaintNotificationNow(context: Context) {
        try {
            val cal = Calendar.getInstance()
            val mmdd = String.format(Locale.US, "%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            val saint = com.example.data.calendar.SaintData.getSaintForDate(mmdd) ?: com.example.data.calendar.SaintData.saintList.first()
            val title = "Saint of the Day: ${saint.saintName}"
            val message = "${saint.feastTitle} • \"${saint.quote}\""
            NotificationHelper.showNotification(context, title, message, notificationId = 9999)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_SAINT_DAY
                putExtra("EXTRA_TYPE", "SAINT")
            }
            context.sendBroadcast(intent)
        }
    }

    fun scheduleOneOffReminder(
        context: Context,
        title: String,
        message: String,
        timeStr: String,
        notificationId: Int = 12345
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val (hour, minute) = parseHourAndMinute(timeStr)
        val nowCal = Calendar.getInstance()
        val triggerMs = if (hour == nowCal.get(Calendar.HOUR_OF_DAY) && minute == nowCal.get(Calendar.MINUTE)) {
            System.currentTimeMillis() + 2000L
        } else {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            cal.timeInMillis
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DUTY_REMINDER
            putExtra("EXTRA_TYPE", "TEST")
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
            putExtra("EXTRA_ID", notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setAlarm(context, alarmManager, triggerMs, pendingIntent)
    }

    fun triggerDutyNotificationNow(context: Context, title: String, message: String, style: String? = null) {
        val notifId = (System.currentTimeMillis() % 10000).toInt() + 1000
        RuleOfLifeCallStateHelper.markScheduledEventTriggered(context)
        if (style == "BANNER") {
            NotificationHelper.showNotification(context, title, message, notifId)
        } else if (style == "CALL_OVERLAY") {
            NotificationHelper.showIncomingCallNotification(context, title, message, notifId)
        } else {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_DUTY_REMINDER
                putExtra("EXTRA_TYPE", "TEST")
                putExtra("EXTRA_TITLE", title)
                putExtra("EXTRA_MESSAGE", message)
                if (style != null) {
                    putExtra("EXTRA_STYLE", style)
                }
                putExtra("EXTRA_ID", notifId)
            }
            context.sendBroadcast(intent)
        }
    }
}
