package com.example.data.calendar

import java.util.Calendar

enum class LiturgicalSeason(val displayName: String, val colorHex: String) {
    ADVENT("Advent", "#8E24AA"), // Violet
    CHRISTMASTIDE("Christmastide & Epiphanytide", "#FFD700"), // Gold
    SEPTUAGESIMA("Septuagesima", "#8E24AA"), // Violet
    LENT("Lent", "#8E24AA"), // Violet
    PASSIONTIDE("Passiontide", "#8E24AA"), // Violet
    PASCHALTIDE("Paschaltide", "#FFD700"), // White/Gold
    PENTECOST_SEASON("Time after Pentecost", "#2E7D32") // Green
}

data class LiturgicalDayInfo(
    val dateString: String, // MM-DD
    val season: LiturgicalSeason,
    val feastName: String,
    val rank: String, // Double of I Class, Double of II Class, Simple, Feria, etc.
    val isEmberDay: Boolean = false,
    val isRogationDay: Boolean = false,
    val isFastingDay: Boolean = false,
    val isAbstinenceDay: Boolean = false,
    val vestmentColor: String = "",
    val notes: String = ""
)

object Roman1954Calendar {

    fun getLiturgicalInfoForDate(year: Int, month: Int, day: Int): LiturgicalDayInfo {
        val cal = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // Sun=1, Mon=2, ... Sat=7
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        val easterCal = getEasterDate(year)
        val easterDayOfYear = easterCal.get(Calendar.DAY_OF_YEAR)

        val septuagesimaDayOfYear = easterDayOfYear - 63
        val ashWednesdayDayOfYear = easterDayOfYear - 46
        val passionSundayDayOfYear = easterDayOfYear - 14
        val pentecostDayOfYear = easterDayOfYear + 49
        val trinitySundayDayOfYear = easterDayOfYear + 56
        val corpusChristiDayOfYear = easterDayOfYear + 60
        val sacredHeartDayOfYear = easterDayOfYear + 68

        val mmdd = String.format("%02d-%02d", month, day)

        // Check Ember Days
        val isEmberDay = checkEmberDay(year, month, day, dayOfYear, ashWednesdayDayOfYear, pentecostDayOfYear)
        val isFriday = (dayOfWeek == Calendar.FRIDAY)
        val season: LiturgicalSeason
        var feastName = ""
        var rank = "Feria"

        when {
            // Advent: 4th Sunday before Christmas to Dec 24
            month == 12 && day <= 24 -> {
                season = LiturgicalSeason.ADVENT
                rank = "Advent Season"
            }
            // Christmas to Jan 13 (Baptism)
            (month == 12 && day >= 25) || (month == 1 && day <= 13) -> {
                season = LiturgicalSeason.CHRISTMASTIDE
                rank = "Christmastide"
            }
            // Septuagesima to Ash Wednesday
            dayOfYear in septuagesimaDayOfYear until ashWednesdayDayOfYear -> {
                season = LiturgicalSeason.SEPTUAGESIMA
                rank = "Septuagesima Season"
            }
            // Lent to Passion Sunday
            dayOfYear in ashWednesdayDayOfYear until passionSundayDayOfYear -> {
                season = LiturgicalSeason.LENT
                rank = "Lenten Season"
            }
            // Passiontide: Passion Sunday to Holy Saturday
            dayOfYear in passionSundayDayOfYear until easterDayOfYear -> {
                season = LiturgicalSeason.PASSIONTIDE
                rank = "Passiontide"
            }
            // Paschaltide: Easter Sunday to Pentecost
            dayOfYear in easterDayOfYear..pentecostDayOfYear -> {
                season = LiturgicalSeason.PASCHALTIDE
                rank = "Paschaltide"
            }
            // Time after Pentecost
            else -> {
                season = LiturgicalSeason.PENTECOST_SEASON
                rank = "Time after Pentecost"
            }
        }
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
            easterDayOfYear -> { feastName = "Easter Sunday"; rank = "Double of I Class" }
            ascensionDayOfYear - 1 -> { feastName = "Vigil of the Ascension"; rank = "Vigil" }
            ascensionDayOfYear -> { feastName = "Ascension of Our Lord"; rank = "Double of I Class" }
            pentecostDayOfYear - 1 -> { feastName = "Vigil of Pentecost"; rank = "Vigil of I Class" }
            pentecostDayOfYear -> { feastName = "Pentecost Sunday"; rank = "Double of I Class" }
            trinitySundayDayOfYear -> { feastName = "Trinity Sunday"; rank = "Double of I Class" }
            corpusChristiDayOfYear -> { feastName = "Corpus Christi"; rank = "Double of I Class" }
            sacredHeartDayOfYear -> { feastName = "Feast of the Sacred Heart of Jesus"; rank = "Double of I Class" }
            holyNameDayOfYear -> { feastName = "Most Holy Name of Jesus"; rank = "Double of II Class" }
            holyFamilyDayOfYear -> { feastName = "The Holy Family"; rank = "Double Major" }
            christTheKingDayOfYear -> { feastName = "Our Lord Jesus Christ the King"; rank = "Double of I Class" }
        }


        
        // Check Octaves
        if (feastName.isEmpty()) {
            when (dayOfYear) {
                in (easterDayOfYear + 1)..(easterDayOfYear + 7) -> { feastName = "Octave of Easter"; rank = "Octave" }
                in (ascensionDayOfYear + 1)..(ascensionDayOfYear + 7) -> { feastName = "Octave of the Ascension"; rank = "Octave" }
                in (pentecostDayOfYear + 1)..(pentecostDayOfYear + 7) -> { feastName = "Octave of Pentecost"; rank = "Octave" }
                in (corpusChristiDayOfYear + 1)..(corpusChristiDayOfYear + 7) -> { feastName = "Octave of Corpus Christi"; rank = "Octave" }
                in (sacredHeartDayOfYear + 1)..(sacredHeartDayOfYear + 7) -> { feastName = "Octave of the Sacred Heart"; rank = "Octave" }
            }
        }

        var vestmentColorStr = "Green"
        when (season) {
            LiturgicalSeason.ADVENT, LiturgicalSeason.SEPTUAGESIMA, LiturgicalSeason.LENT, LiturgicalSeason.PASSIONTIDE -> vestmentColorStr = "Violet"
            LiturgicalSeason.CHRISTMASTIDE, LiturgicalSeason.PASCHALTIDE -> vestmentColorStr = "White"
            LiturgicalSeason.PENTECOST_SEASON -> vestmentColorStr = "Green"
        }

        val saintInfo = SaintData.getSaintForDate(mmdd)
        if (saintInfo != null) {
            feastName = saintInfo.feastTitle
            rank = saintInfo.feastRank
            
            val lowerTitle = feastName.lowercase(java.util.Locale.ROOT)
            if (lowerTitle.contains("martyr") || lowerTitle.contains("apostle") || lowerTitle.contains("evangelist") || lowerTitle.contains("holy innocents") || lowerTitle.contains("peter and paul")) {
                vestmentColorStr = "Red"
            } else if (lowerTitle.contains("virgin") || lowerTitle.contains("confessor") || lowerTitle.contains("pope") || lowerTitle.contains("bishop") || lowerTitle.contains("abbot") || lowerTitle.contains("widow") || lowerTitle.contains("mary") || lowerTitle.contains("angel") || lowerTitle.contains("gabriel") || lowerTitle.contains("raphael") || lowerTitle.contains("michael") || lowerTitle.contains("john the baptist") || lowerTitle.contains("all saints") || lowerTitle.contains("chair of st. peter")) {
                vestmentColorStr = "White"
            }
        } else {
            if (feastName.isEmpty()) {
                feastName = when (dayOfWeek) {
                    Calendar.SUNDAY -> "Sunday in ${season.displayName}"
                    else -> "Feria in ${season.displayName}"
                }
            } else {
                val lowerFeast = feastName.lowercase(java.util.Locale.ROOT)
                if (lowerFeast.contains("martyr") || lowerFeast.contains("pentecost")) {
                    vestmentColorStr = "Red"
                } else if (lowerFeast.contains("sacred heart") || lowerFeast.contains("christ") || lowerFeast.contains("family") || lowerFeast.contains("name") || lowerFeast.contains("trinity") || lowerFeast.contains("ascension") || lowerFeast.contains("easter")) {
                    vestmentColorStr = "White"
                }
            }
        }

        // Fasting & Abstinence logic (1954 discipline)
        val isVigilFast = (month == 12 && day == 24) || // Christmas Eve
                (dayOfYear == pentecostDayOfYear - 1) || // Pentecost Vigil
                (month == 8 && day == 14) || // Assumption Vigil
                (month == 10 && day == 31) // All Saints Vigil

        val isFasting = isEmberDay || isVigilFast || (dayOfYear in ashWednesdayDayOfYear until easterDayOfYear && !isSunday(dayOfWeek))
        val isAbstinence = isFriday || isEmberDay || isVigilFast || (dayOfYear in ashWednesdayDayOfYear until easterDayOfYear && !isSunday(dayOfWeek))

        return LiturgicalDayInfo(
            dateString = mmdd,
            season = season,
            feastName = feastName,
            rank = rank,
            isEmberDay = isEmberDay,
            isRogationDay = (dayOfYear == easterDayOfYear + 36 || dayOfYear == easterDayOfYear + 37 || dayOfYear == easterDayOfYear + 38),
            isFastingDay = isFasting,
            isAbstinenceDay = isAbstinence,
            vestmentColor = vestmentColorStr,
            notes = when {
                isEmberDay -> "Quattuor Tempora (Ember Day) - Traditional Fasting & Abstinence"
                isVigilFast -> "Vigil Fast - Traditional Fasting & Abstinence"
                (dayOfYear in ashWednesdayDayOfYear until easterDayOfYear && !isSunday(dayOfWeek)) -> "Lenten Fast - Traditional Fasting & Abstinence"
                else -> ""
            }
        )
    }

    private fun isSunday(dayOfWeek: Int) = dayOfWeek == Calendar.SUNDAY

    private fun checkEmberDay(
        year: Int,
        month: Int,
        day: Int,
        dayOfYear: Int,
        ashWednesdayDayOfYear: Int,
        pentecostDayOfYear: Int
    ): Boolean {
        // Lenten Ember Days: Wed, Fri, Sat after 1st Sunday of Lent (Ash Wednesday + 4 to +7)
        val lentEmberStart = ashWednesdayDayOfYear + 4
        if (dayOfYear in lentEmberStart..(lentEmberStart + 3)) {
            val dow = getDayOfWeek(year, dayOfYear)
            if (dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY || dow == Calendar.SATURDAY) return true
        }

        // Whitsun (Pentecost) Ember Days: Wed, Fri, Sat of Pentecost Octave
        if (dayOfYear in pentecostDayOfYear..(pentecostDayOfYear + 6)) {
            val dow = getDayOfWeek(year, dayOfYear)
            if (dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY || dow == Calendar.SATURDAY) return true
        }

        // September Ember Days: Wed, Fri, Sat after Holy Cross (Sept 14)
        if (month == 9 && day in 15..24) {
            val dow = getDayOfWeek(year, dayOfYear)
            if (day > 14 && (dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY || dow == Calendar.SATURDAY)) return true
        }

        // Advent Ember Days: Wed, Fri, Sat after St. Lucy (Dec 13)
        if (month == 12 && day in 14..23) {
            val dow = getDayOfWeek(year, dayOfYear)
            if (day > 13 && (dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY || dow == Calendar.SATURDAY)) return true
        }

        return false
    }

    private fun getDayOfWeek(year: Int, dayOfYear: Int): Int {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_YEAR, dayOfYear)
        }
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    // Anonymous Gregorian algorithm for Easter
    fun getEasterDate(year: Int): Calendar {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31 // 3=March, 4=April
        val day = ((h + l - 7 * m + 114) % 31) + 1

        return Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
    }
}
