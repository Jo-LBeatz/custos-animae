package com.example.notifications

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RescheduleManager {
    private const val PREFS_NAME = "rule_reschedule_prefs"
    private const val KEY_DATE_PREFIX = "reschedule_date_"
    private const val KEY_TIME_PREFIX = "reschedule_time_"

    fun setReschedule(context: Context, dutyId: Int, dateStr: String, timeStr: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("$KEY_DATE_PREFIX$dutyId", dateStr)
            .putString("$KEY_TIME_PREFIX$dutyId", timeStr)
            .apply()
    }

    fun getReschedule(context: Context, dutyId: Int): Pair<String, String>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateStr = prefs.getString("$KEY_DATE_PREFIX$dutyId", null)
        val timeStr = prefs.getString("$KEY_TIME_PREFIX$dutyId", null)
        return if (dateStr != null && timeStr != null) {
            Pair(dateStr, timeStr)
        } else {
            null
        }
    }

    fun clearReschedule(context: Context, dutyId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove("$KEY_DATE_PREFIX$dutyId")
            .remove("$KEY_TIME_PREFIX$dutyId")
            .apply()
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun hasActiveRescheduleForToday(context: Context, dutyId: Int): Boolean {
        val pair = getReschedule(context, dutyId) ?: return false
        val today = getTodayDateString()
        return pair.first == today
    }

    fun getRescheduledTimeForToday(context: Context, dutyId: Int): String? {
        val pair = getReschedule(context, dutyId) ?: return null
        val today = getTodayDateString()
        return if (pair.first == today) pair.second else null
    }
}
