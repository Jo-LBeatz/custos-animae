import re
with open('app/src/main/java/com/example/notifications/NotificationReceiver.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "else if (type == \"SACRED_HEART\") {" in line:
        skip = True
        
    if skip:
        if "val dutyId = intent.getIntExtra(\"EXTRA_ID\", -1)" in line:
            skip = False
            # insert our corrected block
            new_lines.append('        } else if (type == "SACRED_HEART") {\n')
            new_lines.append('            val cal = Calendar.getInstance()\n')
            new_lines.append('            val isFirstFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7\n')
            new_lines.append('            val title = if (isFirstFriday) "First Friday Devotion: Sacred Heart of Jesus" else "Friday Devotion: Sacred Heart of Jesus"\n')
            new_lines.append('            val message = if (isFirstFriday) {\n')
            new_lines.append('                "Offer your Holy Communion, prayers, and acts of reparation to the Sacred Heart of Jesus today."\n')
            new_lines.append('            } else {\n')
            new_lines.append('                "In honor of Christ\'s Passion, dedicate today\'s prayers and mortifications to His Most Sacred Heart."\n')
            new_lines.append('            }\n')
            new_lines.append('            val notificationId = intent.getIntExtra("EXTRA_ID", 7101)\n')
            new_lines.append('            NotificationHelper.showNotification(context, title, message, notificationId)\n')
            
            new_lines.append('        } else if (type == "IMMACULATE_HEART") {\n')
            new_lines.append('            val cal = Calendar.getInstance()\n')
            new_lines.append('            val isFirstSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7\n')
            new_lines.append('            val title = if (isFirstSaturday) "First Saturday Devotion: Immaculate Heart of Mary" else "Saturday Devotion: Immaculate Heart of Mary"\n')
            new_lines.append('            val message = if (isFirstSaturday) {\n')
            new_lines.append('                "Make reparation to the Immaculate Heart of Mary with the Rosary, meditation, and confession."\n')
            new_lines.append('            } else {\n')
            new_lines.append('                "Dedicate today\'s prayers, Rosary, and offerings in loving reparation to the Immaculate Heart of Mary."\n')
            new_lines.append('            }\n')
            new_lines.append('            val notificationId = intent.getIntExtra("EXTRA_ID", 7102)\n')
            new_lines.append('            NotificationHelper.showNotification(context, title, message, notificationId)\n')
            
            new_lines.append('        } else if (type == "LITURGICAL_SEASON") {\n')
            new_lines.append('            val today = Calendar.getInstance()\n')
            new_lines.append('            val year = today.get(Calendar.YEAR)\n')
            new_lines.append('            val month = today.get(Calendar.MONTH) + 1\n')
            new_lines.append('            val day = today.get(Calendar.DAY_OF_MONTH)\n')
            new_lines.append('            val todayInfo = com.example.data.calendar.Roman1954Calendar.getLiturgicalInfoForDate(year, month, day)\n')
            new_lines.append('            \n')
            new_lines.append('            val yesterday = Calendar.getInstance()\n')
            new_lines.append('            yesterday.add(Calendar.DAY_OF_YEAR, -1)\n')
            new_lines.append('            val yYear = yesterday.get(Calendar.YEAR)\n')
            new_lines.append('            val yMonth = yesterday.get(Calendar.MONTH) + 1\n')
            new_lines.append('            val yDay = yesterday.get(Calendar.DAY_OF_MONTH)\n')
            new_lines.append('            val yesterdayInfo = com.example.data.calendar.Roman1954Calendar.getLiturgicalInfoForDate(yYear, yMonth, yDay)\n')
            new_lines.append('            \n')
            new_lines.append('            if (todayInfo.season != yesterdayInfo.season) {\n')
            new_lines.append('                val title = "Liturgical Season: ${todayInfo.season.displayName}"\n')
            new_lines.append('                val message = "Today begins the ${todayInfo.season.displayName}. Vestment color: ${todayInfo.vestmentColor}."\n')
            new_lines.append('                NotificationHelper.showNotification(context, title, message, 7103)\n')
            new_lines.append('            }\n')
            new_lines.append('        } else {\n')
            new_lines.append(line)
        continue
    
    if not skip:
        new_lines.append(line)

with open('app/src/main/java/com/example/notifications/NotificationReceiver.kt', 'w') as f:
    f.writelines(new_lines)
