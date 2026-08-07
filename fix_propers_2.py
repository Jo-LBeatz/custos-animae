import re

with open("app/src/main/java/com/example/data/missal/DailyPropers1954.kt", "r") as f:
    content = f.read()

new_get = """    fun getTodayProper(cal: Calendar? = null): MassProper {
        val calendar = cal ?: Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        
        val lookupStr = String.format("%02d-%02d", month + 1, day)

        if (propersMap.containsKey(lookupStr)) {
            return propersMap[lookupStr]!!
        }

        // Easter/Pentecost logic
        val easterCal = com.example.data.calendar.Roman1954Calendar.getEasterDate(year)
        val pentecostCal = Calendar.getInstance().apply {
            timeInMillis = easterCal.timeInMillis
            add(Calendar.DAY_OF_YEAR, 49)
        }
        
        val isEaster = month == easterCal.get(Calendar.MONTH) && day == easterCal.get(Calendar.DAY_OF_MONTH)
        val isPentecost = month == pentecostCal.get(Calendar.MONTH) && day == pentecostCal.get(Calendar.DAY_OF_MONTH)

        return when {
            month == Calendar.DECEMBER && day in 24..25 -> propersMap["CHRISTMAS"]!!
            isEaster -> propersMap["EASTER"]!!
            isPentecost -> propersMap["PENTECOST"]!!
            else -> propersMap["GENERAL"]!!
        }
    }"""

content = re.sub(r'    fun getTodayProper\(dateStr: String\? = null\): MassProper \{.*?    \}', new_get, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/missal/DailyPropers1954.kt", "w") as f:
    f.write(content)
