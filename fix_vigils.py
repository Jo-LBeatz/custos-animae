import re

with open("app/src/main/java/com/example/data/calendar/Roman1954DataSeeder.kt", "r") as f:
    content = f.read()

# Replace isMajor1954Vigil to include all pre-1955 fixed Vigils
new_vigil_logic = """    private fun isMajor1954Vigil(month: Int, day: Int, year: Int): Boolean {
        // Handle leap year for St. Matthias Vigil (Feb 23 ordinarily, Feb 24 in leap year)
        val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val matthiasVigilDay = if (isLeap) 24 else 23

        return (month == 1 && day == 5) || // Vigil of Epiphany
                (month == 2 && day == matthiasVigilDay) || // Vigil of St. Matthias
                (month == 6 && day == 23) || // Vigil of St. John the Baptist
                (month == 6 && day == 28) || // Vigil of SS. Peter and Paul
                (month == 7 && day == 24) || // Vigil of St. James
                (month == 8 && day == 9) || // Vigil of St. Lawrence
                (month == 8 && day == 14) || // Vigil of Assumption of B.V.M.
                (month == 8 && day == 23) || // Vigil of St. Bartholomew
                (month == 9 && day == 20) || // Vigil of St. Matthew
                (month == 10 && day == 27) || // Vigil of SS. Simon & Jude
                (month == 10 && day == 31) || // Vigil of All Saints
                (month == 11 && day == 29) || // Vigil of St. Andrew
                (month == 12 && day == 7) || // Vigil of Immaculate Conception
                (month == 12 && day == 20) || // Vigil of St. Thomas
                (month == 12 && day == 24) // Vigil of Nativity
    }

    private fun getMajorVigilTitle(month: Int, day: Int, year: Int): String {
        val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val matthiasVigilDay = if (isLeap) 24 else 23

        return when {
            month == 1 && day == 5 -> "Vigil of the Epiphany of Our Lord"
            month == 2 && day == matthiasVigilDay -> "Vigil of St. Matthias, Apostle"
            month == 6 && day == 23 -> "Vigil of the Nativity of St. John the Baptist"
            month == 6 && day == 28 -> "Vigil of Holy Apostles Peter and Paul"
            month == 7 && day == 24 -> "Vigil of St. James, Apostle"
            month == 8 && day == 9 -> "Vigil of St. Lawrence, Martyr"
            month == 8 && day == 14 -> "Vigil of the Assumption of the Blessed Virgin Mary"
            month == 8 && day == 23 -> "Vigil of St. Bartholomew, Apostle"
            month == 9 && day == 20 -> "Vigil of St. Matthew, Apostle & Evangelist"
            month == 10 && day == 27 -> "Vigil of SS. Simon & Jude, Apostles"
            month == 10 && day == 31 -> "Vigil of All Saints (All Hallows' Eve)"
            month == 11 && day == 29 -> "Vigil of St. Andrew, Apostle"
            month == 12 && day == 7 -> "Vigil of the Immaculate Conception of the B.V.M."
            month == 12 && day == 20 -> "Vigil of St. Thomas, Apostle"
            month == 12 && day == 24 -> "Vigil of the Nativity of Our Lord (Christmas Eve)"
            else -> "Liturgical Vigil"
        }
    }"""

content = re.sub(r'    private fun isMajor1954Vigil\(month: Int, day: Int\): Boolean \{.*    private fun getMajorVigilTitle\(month: Int, day: Int\): String \{.*    }', new_vigil_logic, content, flags=re.DOTALL)

# Update the calls to isMajor1954Vigil(month, day) to isMajor1954Vigil(month, day, 2026)
content = content.replace("isMajor1954Vigil(month, day)", "isMajor1954Vigil(month, day, 2026)")
content = content.replace("getMajorVigilTitle(month, day)", "getMajorVigilTitle(month, day, 2026)")

with open("app/src/main/java/com/example/data/calendar/Roman1954DataSeeder.kt", "w") as f:
    f.write(content)
