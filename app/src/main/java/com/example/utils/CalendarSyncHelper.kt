package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.example.data.SpiritualDuty
import java.util.Calendar
import java.util.TimeZone

data class PhoneCalendar(
    val id: Long,
    val displayName: String,
    val accountName: String
)

object CalendarSyncHelper {

    fun getPhoneCalendars(context: Context): List<PhoneCalendar> {
        val list = mutableListOf<PhoneCalendar>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        val uri = CalendarContract.Calendars.CONTENT_URI
        try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val nameCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Calendar $id"
                    val account = cursor.getString(accCol) ?: "Local"
                    list.add(PhoneCalendar(id, name, account))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun cloneDutyToCalendar(
        context: Context,
        calendarId: Long,
        duty: SpiritualDuty
    ): Boolean {
        // 1. Handling YEARLY (can be list of specific dates like "10-15" OR relative like "1ST_Monday_10")
        if (duty.frequency == "YEARLY" && duty.scheduledDaysOfYear.isNotBlank()) {
            val dates = duty.scheduledDaysOfYear.split(",")
            var allSuccess = true
            for (dateStr in dates) {
                val trimmed = dateStr.trim()
                if (trimmed.isBlank()) continue

                val calendar = Calendar.getInstance()
                val timeParts = duty.reminderTime.split(":")
                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
                val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val values = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.TITLE, "☩ " + duty.title)
                    put(CalendarContract.Events.DESCRIPTION, duty.description + "\n\nCategory: ${duty.category}\nPriority: ${duty.priority}\nSynced from Custos Animae")
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

                    if (trimmed.contains("_")) {
                        // Relative yearly: e.g. "1ST_Monday_10"
                        val subParts = trimmed.split("_")
                        val ordinalStr = subParts.getOrNull(0) ?: "1ST"
                        val dayOfWeekStr = subParts.getOrNull(1) ?: "Monday"
                        val monthStr = subParts.getOrNull(2) ?: "1"

                        val monthVal = monthStr.toIntOrNull() ?: 1
                        calendar.set(Calendar.MONTH, monthVal - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)

                        val ordinal = when (ordinalStr) {
                            "1ST" -> "1"
                            "2ND" -> "2"
                            "3RD" -> "3"
                            "4TH" -> "4"
                            "LST" -> "-1"
                            else -> "1"
                        }
                        val dayOfWeek = when (dayOfWeekStr.lowercase(java.util.Locale.ROOT)) {
                            "monday" -> "MO"
                            "tuesday" -> "TU"
                            "wednesday" -> "WE"
                            "thursday" -> "TH"
                            "friday" -> "FR"
                            "saturday" -> "SA"
                            "sunday" -> "SU"
                            else -> "MO"
                        }

                        val startTime = calendar.timeInMillis
                        put(CalendarContract.Events.DTSTART, startTime)
                        put(CalendarContract.Events.DURATION, "PT30M")
                        put(CalendarContract.Events.RRULE, "FREQ=YEARLY;BYMONTH=$monthVal;BYDAY=$ordinal$dayOfWeek")
                    } else {
                        // Specific yearly: e.g. "10-15"
                        val parts = trimmed.split("-")
                        if (parts.size == 2) {
                            val month = parts[0].toIntOrNull() ?: 1
                            val day = parts[1].toIntOrNull() ?: 1

                            calendar.set(Calendar.MONTH, month - 1)
                            calendar.set(Calendar.DAY_OF_MONTH, day)

                            val startTime = calendar.timeInMillis
                            put(CalendarContract.Events.DTSTART, startTime)
                            put(CalendarContract.Events.DURATION, "PT30M")
                            put(CalendarContract.Events.RRULE, "FREQ=YEARLY")
                        } else {
                            // fallback
                            val startTime = calendar.timeInMillis
                            put(CalendarContract.Events.DTSTART, startTime)
                            put(CalendarContract.Events.DURATION, "PT30M")
                            put(CalendarContract.Events.RRULE, "FREQ=YEARLY")
                        }
                    }
                }
                try {
                    val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                    if (uri == null) allSuccess = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    allSuccess = false
                }
            }
            return allSuccess
        }

        // 2. Handling MONTHLY (can be list of specific days like "1,15" OR relative like "1ST_Monday")
        if (duty.frequency == "MONTHLY" && duty.scheduledDaysOfMonth.isNotBlank()) {
            val parts = duty.scheduledDaysOfMonth.split(",")
            val hasRelative = parts.any { it.contains("_") }
            if (hasRelative) {
                var allSuccess = true
                for (part in parts) {
                    val trimmed = part.trim()
                    if (trimmed.isBlank()) continue

                    val calendar = Calendar.getInstance()
                    val timeParts = duty.reminderTime.split(":")
                    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
                    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val startTime = calendar.timeInMillis

                    val values = ContentValues().apply {
                        put(CalendarContract.Events.CALENDAR_ID, calendarId)
                        put(CalendarContract.Events.TITLE, "☩ " + duty.title)
                        put(CalendarContract.Events.DESCRIPTION, duty.description + "\n\nCategory: ${duty.category}\nPriority: ${duty.priority}\nSynced from Custos Animae")
                        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                        put(CalendarContract.Events.DTSTART, startTime)
                        put(CalendarContract.Events.DURATION, "PT30M")

                        val rrule = if (trimmed.contains("_")) {
                            val subParts = trimmed.split("_")
                            val ordinal = when (subParts.getOrNull(0)) {
                                "1ST" -> "1"
                                "2ND" -> "2"
                                "3RD" -> "3"
                                "4TH" -> "4"
                                "LST" -> "-1"
                                else -> "1"
                            }
                            val dayOfWeek = when (subParts.getOrNull(1)?.lowercase(java.util.Locale.ROOT)) {
                                "monday" -> "MO"
                                "tuesday" -> "TU"
                                "wednesday" -> "WE"
                                "thursday" -> "TH"
                                "friday" -> "FR"
                                "saturday" -> "SA"
                                "sunday" -> "SU"
                                else -> "MO"
                            }
                            "FREQ=MONTHLY;BYDAY=$ordinal$dayOfWeek"
                        } else {
                            "FREQ=MONTHLY;BYMONTHDAY=$trimmed"
                        }
                        put(CalendarContract.Events.RRULE, rrule)
                    }
                    try {
                        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                        if (uri == null) allSuccess = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                        allSuccess = false
                    }
                }
                return allSuccess
            }
        }

        // 3. Handling DAILY, WEEKLY, or simple MONTHLY with specific numbers
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, "☩ " + duty.title)
            put(CalendarContract.Events.DESCRIPTION, duty.description + "\n\nCategory: ${duty.category}\nPriority: ${duty.priority}\nSynced from Custos Animae")
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

            val calendar = Calendar.getInstance()
            val timeParts = duty.reminderTime.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            val startTime = calendar.timeInMillis
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DURATION, "PT30M")

            val rrule = when (duty.frequency) {
                "DAILY" -> "FREQ=DAILY"
                "WEEKLY" -> {
                    if (duty.scheduledDaysOfWeek.isNotBlank()) {
                        val daysMapped = duty.scheduledDaysOfWeek.split(",")
                            .mapNotNull { day ->
                                when (day.trim().lowercase(java.util.Locale.ROOT)) {
                                    "monday" -> "MO"
                                    "tuesday" -> "TU"
                                    "wednesday" -> "WE"
                                    "thursday" -> "TH"
                                    "friday" -> "FR"
                                    "saturday" -> "SA"
                                    "sunday" -> "SU"
                                    else -> null
                                }
                            }
                        if (daysMapped.isNotEmpty()) {
                            "FREQ=WEEKLY;BYDAY=${daysMapped.joinToString(",")}"
                        } else {
                            "FREQ=WEEKLY"
                        }
                    } else {
                        "FREQ=WEEKLY"
                    }
                }
                "MONTHLY" -> {
                    if (duty.scheduledDaysOfMonth.isNotBlank()) {
                        "FREQ=MONTHLY;BYMONTHDAY=${duty.scheduledDaysOfMonth}"
                    } else {
                        "FREQ=MONTHLY"
                    }
                }
                "YEARLY" -> "FREQ=YEARLY"
                else -> "FREQ=DAILY"
            }
            put(CalendarContract.Events.RRULE, rrule)
        }

        return try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
