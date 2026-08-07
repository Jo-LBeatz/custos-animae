import re

with open('app/src/main/java/com/example/notifications/NotificationScheduler.kt', 'r') as f:
    text = f.read()

liturgical_schedule = """
            // Liturgical Season Notifications (Daily check at 8:00 AM)
            scheduleDevotionReminder(
                context,
                "LITURGICAL_SEASON",
                "08:00",
                intentId = 7103,
                enabled = profile.liturgicalSeasonNotificationsEnabled && profile.notificationEnabled,
                isDaily = true
            )
"""

text = text.replace('        // 3. Schedule Task Notifications', liturgical_schedule + '        // 3. Schedule Task Notifications')

text = text.replace('fun scheduleDevotionReminder(', 'fun scheduleDevotionReminder(\n        context: Context,\n        type: String, \n        timeStr: String,\n        intentId: Int,\n        enabled: Boolean = true,\n        isDaily: Boolean = false\n    ) { // ')

text = re.sub(
    r'val targetDayOfWeek = if \(type == "SACRED_HEART"\) Calendar\.FRIDAY else Calendar\.SATURDAY\s*// Advance to the next correct day of the week\s*while \(cal\.get\(Calendar\.DAY_OF_WEEK\) != targetDayOfWeek \|\| cal\.timeInMillis <= System\.currentTimeMillis\(\)\) \{\s*cal\.add\(Calendar\.DAY_OF_YEAR, 1\)\s*\}',
    """
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
    """,
    text
)

text = re.sub(
    r'AlarmManager\.INTERVAL_DAY \* 7,',
    """if (isDaily) AlarmManager.INTERVAL_DAY else AlarmManager.INTERVAL_DAY * 7,""",
    text
)

with open('app/src/main/java/com/example/notifications/NotificationScheduler.kt', 'w') as f:
    f.write(text)
