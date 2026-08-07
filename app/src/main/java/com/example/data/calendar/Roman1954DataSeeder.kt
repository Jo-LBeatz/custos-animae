package com.example.data.calendar

import com.example.data.CalendarEntry

object Roman1954DataSeeder {

    fun getInitialCalendarEntries(): List<CalendarEntry> {
        val seededList = mutableListOf<CalendarEntry>()

        // Seed entries for all 365 days using SaintData and Roman1954Calendar framework
        val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        for (month in 1..12) {
            for (day in 1..daysInMonth[month - 1]) {
                val dateString = String.format("%02d-%02d", month, day)
                val info = Roman1954Calendar.getLiturgicalInfoForDate(2026, month, day)
                val saint = SaintData.getSaintForDate(dateString)

                val isVigilDay = info.rank.contains("Vigil", ignoreCase = true) ||
                        info.feastName.contains("Vigil", ignoreCase = true) ||
                        isMajor1954Vigil(month, day, 2026)
                val isMovableFeast = info.feastName in listOf(
                    "Easter Sunday", "Ascension of Our Lord", "Pentecost Sunday", 
                    "Trinity Sunday", "Corpus Christi", "Feast of the Sacred Heart of Jesus",
                    "Most Holy Name of Jesus", "The Holy Family", "Our Lord Jesus Christ the King"
                )
                
                val feastTitle = when {
                    isMovableFeast -> info.feastName + (if (saint != null) " / Commemoration of ${saint.feastTitle}" else "")
                    isMajor1954Vigil(month, day, 2026) -> getMajorVigilTitle(month, day, 2026) + (if (saint != null) " / Commemoration of ${saint.feastTitle}" else "")
                    saint != null -> saint.feastTitle
                    else -> info.feastName
                }

                val feastRank = when {
                    isMovableFeast -> info.rank
                    isMajor1954Vigil(month, day, 2026) -> "Vigil"
                    saint != null -> saint.feastRank
                    else -> info.rank
                }


                seededList.add(
                    CalendarEntry(
                        id = dateString,
                        dateString = dateString,
                        month = month,
                        day = day,
                        feastTitle = feastTitle,
                        feastRank = feastRank,
                        season = info.season.displayName,
                        isVigil = isVigilDay,
                        isEmberDay = info.isEmberDay,
                        isRogationDay = info.isRogationDay,
                        isFastingDay = info.isFastingDay,
                        isAbstinenceDay = info.isAbstinenceDay,
                        saintName = saint?.saintName ?: "",
                        commemorated = saint?.commemorated ?: "",
                        biography = saint?.biography ?: "",
                        historicityNote = saint?.historicity ?: "",
                        virtues = saint?.virtues?.joinToString(", ") ?: "",
                        quote = saint?.quote ?: "",
                        meditation = saint?.meditation ?: "",
                        resolution = saint?.resolution ?: "",
                        intercessoryPrayer = saint?.intercessoryPrayer ?: "",
                        notes = info.notes.ifBlank { if (isVigilDay) "Liturgical Vigil in General Roman Calendar of Pope Pius XII" else "" }
                    )
                )
            }
        }

        return seededList
    }

    private fun isMajor1954Vigil(month: Int, day: Int, year: Int): Boolean {
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
    }
}
