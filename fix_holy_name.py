import re

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "r") as f:
    content = f.read()

# I need to add logic for Feast of the Holy Name and Christ the King.
# Holy Name: Sunday between Jan 2 and Jan 5. If none, Jan 2.
# Holy Family: 1st Sunday after Epiphany
# Christ the King: Last Sunday of October.

new_logic = """
        // Movable Feasts & Vigils (Overriding Feria)
        val ascensionDayOfYear = easterDayOfYear + 39
        
        // Holy Name of Jesus (Sunday between Jan 2 and Jan 5, or Jan 2 if none)
        val hasSundayJan2to5 = (2..5).any { getDayOfWeek(year, it) == Calendar.SUNDAY }
        val holyNameDayOfYear = if (hasSundayJan2to5) {
            (2..5).first { getDayOfWeek(year, it) == Calendar.SUNDAY }
        } else {
            2
        }

        // Holy Family (1st Sunday after Epiphany, which is Jan 6)
        val holyFamilyDayOfYear = (7..13).first { getDayOfWeek(year, it) == Calendar.SUNDAY }

        // Christ the King (Last Sunday of October)
        // Oct 31 is day 304 (or 305 in leap year).
        val oct31DayOfYear = Calendar.getInstance().apply { set(year, 9, 31) }.get(Calendar.DAY_OF_YEAR)
        var christTheKingDayOfYear = oct31DayOfYear
        while (getDayOfWeek(year, christTheKingDayOfYear) != Calendar.SUNDAY) {
            christTheKingDayOfYear--
        }

        when (dayOfYear) {
            easterDayOfYear -> { feastName = "Easter Sunday"; rank = "Double of I Class"; vestmentColorStr = "White" }
            ascensionDayOfYear - 1 -> { feastName = "Vigil of the Ascension"; rank = "Vigil"; vestmentColorStr = "Violet" }
            ascensionDayOfYear -> { feastName = "Ascension of Our Lord"; rank = "Double of I Class"; vestmentColorStr = "White" }
            pentecostDayOfYear - 1 -> { feastName = "Vigil of Pentecost"; rank = "Vigil of I Class"; vestmentColorStr = "Red" }
            pentecostDayOfYear -> { feastName = "Pentecost Sunday"; rank = "Double of I Class"; vestmentColorStr = "Red" }
            trinitySundayDayOfYear -> { feastName = "Trinity Sunday"; rank = "Double of I Class"; vestmentColorStr = "White" }
            corpusChristiDayOfYear -> { feastName = "Corpus Christi"; rank = "Double of I Class"; vestmentColorStr = "White" }
            sacredHeartDayOfYear -> { feastName = "Feast of the Sacred Heart of Jesus"; rank = "Double of I Class"; vestmentColorStr = "White" }
            holyNameDayOfYear -> { feastName = "Most Holy Name of Jesus"; rank = "Double of II Class"; vestmentColorStr = "White" }
            holyFamilyDayOfYear -> { feastName = "The Holy Family"; rank = "Double of Greater-Major"; vestmentColorStr = "White" }
            christTheKingDayOfYear -> { feastName = "Our Lord Jesus Christ the King"; rank = "Double of I Class"; vestmentColorStr = "White" }
        }
"""

content = re.sub(r'\n\s*// Movable Feasts & Vigils \(Overriding Feria\).*?sacredHeartDayOfYear -> \{ feastName = "Feast of the Sacred Heart of Jesus"; rank = "Double of I Class" \}\n\s*\}', new_logic, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "w") as f:
    f.write(content)
