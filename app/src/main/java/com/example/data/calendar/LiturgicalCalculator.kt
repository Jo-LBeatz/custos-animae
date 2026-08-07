package com.example.data.calendar

import java.util.Calendar

/**
 * 1954 Roman Rite Movable Feasts Container.
 */
data class MovableFeasts1954(
    val year: Int,
    val easter: Calendar,
    val septuagesima: Calendar,
    val sexagesima: Calendar,
    val quinquagesima: Calendar,
    val ashWednesday: Calendar,
    val passionSunday: Calendar,
    val palmSunday: Calendar,
    val holyThursday: Calendar,
    val goodFriday: Calendar,
    val holySaturday: Calendar,
    val lowSunday: Calendar,
    val ascension: Calendar,
    val pentecost: Calendar,
    val trinitySunday: Calendar,
    val corpusChristi: Calendar,
    val sacredHeart: Calendar,
    val christTheKing: Calendar,
    val advent1: Calendar,
    val advent2: Calendar,
    val advent3: Calendar,
    val advent4: Calendar
)

/**
 * Precedence ranks according to 1954 Roman Rite Rubrics.
 */
enum class PrecedenceClass(val rank: Int, val description: String) {
    CLASS_I_PRIVILEGED(1, "Double of I Class / Privileged Temporal or Major Feast"),
    CLASS_I_SANCTORAL(2, "Double of I Class Sanctoral Feast"),
    CLASS_II(3, "Double of II Class / Major Sunday or Feast"),
    CLASS_III(4, "Greater Double or Double"),
    SIMPLE_OR_FERIA(5, "Simple Feast or Feria")
}

/**
 * Engine for calculating 1954 Roman Rite liturgical movable feasts, precedence rules,
 * and selecting the correct Proprium de Tempore for any given date.
 */
class LiturgicalCalculator {

    companion object {

        /**
         * Calculates Easter Sunday for a given Gregorian calendar year
         * using the Meeus/Jones/Butcher Anonymous Gregorian algorithm.
         */
        fun calculateEaster(year: Int): Calendar {
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
            val month = (h + l - 7 * m + 114) / 31 // 3 = March, 4 = April
            val day = ((h + l - 7 * m + 114) % 31) + 1

            return Calendar.getInstance().apply {
                clear()
                set(year, month - 1, day)
            }
        }

        fun calculateSeptuagesima(year: Int): Calendar = offsetCalendar(calculateEaster(year), -63)
        fun calculateSexagesima(year: Int): Calendar = offsetCalendar(calculateEaster(year), -56)
        fun calculateQuinquagesima(year: Int): Calendar = offsetCalendar(calculateEaster(year), -49)
        fun calculateAshWednesday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -46)
        fun calculatePassionSunday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -14)
        fun calculatePalmSunday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -7)
        fun calculateHolyThursday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -3)
        fun calculateGoodFriday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -2)
        fun calculateHolySaturday(year: Int): Calendar = offsetCalendar(calculateEaster(year), -1)
        fun calculateLowSunday(year: Int): Calendar = offsetCalendar(calculateEaster(year), 7)
        fun calculateAscension(year: Int): Calendar = offsetCalendar(calculateEaster(year), 39)
        fun calculatePentecost(year: Int): Calendar = offsetCalendar(calculateEaster(year), 49)
        fun calculateTrinitySunday(year: Int): Calendar = offsetCalendar(calculateEaster(year), 56)
        fun calculateCorpusChristi(year: Int): Calendar = offsetCalendar(calculateEaster(year), 60)
        fun calculateSacredHeart(year: Int): Calendar = offsetCalendar(calculateEaster(year), 68)

        /**
         * Christ the King = Last Sunday of October (1925 / 1954 Rubrics)
         */
        fun calculateChristTheKing(year: Int): Calendar {
            val cal = Calendar.getInstance().apply {
                clear()
                set(year, Calendar.OCTOBER, 31)
            }
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }
            return cal
        }

        /**
         * First Sunday of Advent = Sunday nearest St. Andrew's Day (Nov 30).
         * Always falls between Nov 27 and Dec 3.
         */
        fun calculateAdventSunday(year: Int): Calendar {
            for (day in 27..30) {
                val cal = Calendar.getInstance().apply {
                    clear()
                    set(year, Calendar.NOVEMBER, day)
                }
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) return cal
            }
            for (day in 1..3) {
                val cal = Calendar.getInstance().apply {
                    clear()
                    set(year, Calendar.DECEMBER, day)
                }
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) return cal
            }
            return Calendar.getInstance().apply { set(year, Calendar.NOVEMBER, 27) }
        }

        private fun offsetCalendar(cal: Calendar, days: Int): Calendar {
            return (cal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, days)
            }
        }

        /**
         * Returns all 1954 Roman Rite movable feasts for a given Gregorian year.
         */
        fun calculateMovableFeasts(year: Int): MovableFeasts1954 {
            val easter = calculateEaster(year)
            val adv1 = calculateAdventSunday(year)
            return MovableFeasts1954(
                year = year,
                easter = easter,
                septuagesima = calculateSeptuagesima(year),
                sexagesima = calculateSexagesima(year),
                quinquagesima = calculateQuinquagesima(year),
                ashWednesday = calculateAshWednesday(year),
                passionSunday = calculatePassionSunday(year),
                palmSunday = calculatePalmSunday(year),
                holyThursday = calculateHolyThursday(year),
                goodFriday = calculateGoodFriday(year),
                holySaturday = calculateHolySaturday(year),
                lowSunday = calculateLowSunday(year),
                ascension = calculateAscension(year),
                pentecost = calculatePentecost(year),
                trinitySunday = calculateTrinitySunday(year),
                corpusChristi = calculateCorpusChristi(year),
                sacredHeart = calculateSacredHeart(year),
                christTheKing = calculateChristTheKing(year),
                advent1 = adv1,
                advent2 = offsetCalendar(adv1, 7),
                advent3 = offsetCalendar(adv1, 14),
                advent4 = offsetCalendar(adv1, 21)
            )
        }

        private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                    c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
        }

        /**
         * Determines the proper code for offline database lookups for a given calendar date,
         * applying 1954 precedence rules (Temporal vs. Sanctoral).
         */
        fun getProperCodeForDate(calendar: Calendar): String {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val mmdd = String.format("%02d-%02d", month, day)

            val feats = calculateMovableFeasts(year)

            // 1. Privileged Temporal / Movable Feasts (Double I Class)
            when {
                isSameDay(calendar, feats.easter) -> return "EASTER"
                isSameDay(calendar, feats.lowSunday) -> return "LOW_SUNDAY"
                isSameDay(calendar, feats.pentecost) -> return "PENTECOST"
                isSameDay(calendar, feats.trinitySunday) -> return "TRINITY_SUNDAY"
                isSameDay(calendar, feats.corpusChristi) -> return "CORPUS_CHRISTI"
                isSameDay(calendar, feats.sacredHeart) -> return "SACRED_HEART"
                isSameDay(calendar, feats.ashWednesday) -> return "ASH_WEDNESDAY"
                isSameDay(calendar, feats.ascension) -> return "ASCENSION"
                isSameDay(calendar, feats.christTheKing) -> return "CHRIST_THE_KING"
                isSameDay(calendar, feats.advent1) -> return "ADVENT_1"
                isSameDay(calendar, feats.advent2) -> return "ADVENT_2"
                isSameDay(calendar, feats.advent3) -> return "ADVENT_3"
                isSameDay(calendar, feats.advent4) -> return "ADVENT_4"
                isSameDay(calendar, feats.septuagesima) -> return "SEPTUAGESIMA"
                isSameDay(calendar, feats.passionSunday) -> return "PASSION_SUNDAY"
            }

            // 2. Fixed Privileged Solemnities (Class I)
            if (month == 12 && day in 24..25) return "CHRISTMAS"
            if (month == 1 && day == 6) return "EPIPHANY"
            if (mmdd == "12-08") return "12-08" // Immaculate Conception
            if (mmdd == "08-15") return "08-15" // Assumption
            if (mmdd == "11-01") return "11-01" // All Saints
            if (mmdd == "11-02") return "11-02" // All Souls
            if (mmdd == "03-19") return "03-19" // St. Joseph
            if (mmdd == "03-25") return "03-25" // Annunciation
            if (mmdd == "06-24") return "06-24" // St. John the Baptist
            if (mmdd == "06-29") return "06-29" // SS. Peter & Paul
            if (mmdd == "07-31") return "07-31" // St. Ignatius of Loyola

            // 3. Fallback to fixed MMDD code
            return mmdd
        }

        fun getLiturgicalCode(calendar: Calendar): String = getProperCodeForDate(calendar)

        /**
         * Evaluates precedence rank for a given date under 1954 Rubrics.
         */
        fun getPrecedenceClass(calendar: Calendar): PrecedenceClass {
            val code = getProperCodeForDate(calendar)
            return when (code) {
                "EASTER", "CHRISTMAS", "EPIPHANY", "PENTECOST", "CORPUS_CHRISTI", "ASCENSION", "TRINITY_SUNDAY", "ASH_WEDNESDAY" -> PrecedenceClass.CLASS_I_PRIVILEGED
                "12-08", "08-15", "11-01", "03-19", "03-25", "06-24", "06-29" -> PrecedenceClass.CLASS_I_SANCTORAL
                "LOW_SUNDAY", "SACRED_HEART", "CHRIST_THE_KING", "ADVENT_1", "ADVENT_2", "ADVENT_3", "ADVENT_4", "SEPTUAGESIMA", "PASSION_SUNDAY" -> PrecedenceClass.CLASS_II
                else -> PrecedenceClass.CLASS_III
            }
        }
    }
}

