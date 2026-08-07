import re

with open('app/src/main/java/com/example/notifications/NotificationReceiver.kt', 'r') as f:
    text = f.read()

liturgical_check = """
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
                NotificationHelper.showNotification(context, title, message, 7103)
            }
"""

text = text.replace('        } else {', liturgical_check + '        } else {')

with open('app/src/main/java/com/example/notifications/NotificationReceiver.kt', 'w') as f:
    f.write(text)
