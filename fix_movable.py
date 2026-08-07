import re

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "r") as f:
    content = f.read()

movable_logic = """        val isEmberDay = checkEmberDay(year, month, day, dayOfYear, ashWednesdayDayOfYear, pentecostDayOfYear)
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
        when (dayOfYear) {
            easterDayOfYear -> { feastName = "Easter Sunday"; rank = "Double of I Class" }
            ascensionDayOfYear - 1 -> { feastName = "Vigil of the Ascension"; rank = "Vigil" }
            ascensionDayOfYear -> { feastName = "Ascension of Our Lord"; rank = "Double of I Class" }
            pentecostDayOfYear - 1 -> { feastName = "Vigil of Pentecost"; rank = "Vigil of I Class" }
            pentecostDayOfYear -> { feastName = "Pentecost Sunday"; rank = "Double of I Class" }
            trinitySundayDayOfYear -> { feastName = "Trinity Sunday"; rank = "Double of I Class" }
            corpusChristiDayOfYear -> { feastName = "Corpus Christi"; rank = "Double of I Class" }
            sacredHeartDayOfYear -> { feastName = "Feast of the Sacred Heart of Jesus"; rank = "Double of I Class" }
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
        }"""

content = re.sub(r'        val isEmberDay = checkEmberDay\(year, month, day, dayOfYear, ashWednesdayDayOfYear, pentecostDayOfYear\).*?rank = "Time after Pentecost"\n            }\n        }', movable_logic, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "w") as f:
    f.write(content)
