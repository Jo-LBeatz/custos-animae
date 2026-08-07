import re

with open('app/src/main/java/com/example/notifications/NotificationScheduler.kt', 'r') as f:
    text = f.read()

# Fix the duplicate signature
text = re.sub(
    r'fun scheduleDevotionReminder\(\s*context: Context,\s*type: String,\s*timeStr: String,\s*intentId: Int,\s*enabled: Boolean = true,\s*isDaily: Boolean = false\s*\) \{ // \s*context: Context,\s*type: String, // "SACRED_HEART" or "IMMACULATE_HEART"\s*timeStr: String,\s*intentId: Int,\s*enabled: Boolean = true\s*\)',
    """fun scheduleDevotionReminder(
        context: Context,
        type: String,
        timeStr: String,
        intentId: Int,
        enabled: Boolean = true,
        isDaily: Boolean = false
    )""", text)

with open('app/src/main/java/com/example/notifications/NotificationScheduler.kt', 'w') as f:
    f.write(text)
