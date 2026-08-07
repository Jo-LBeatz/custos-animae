import re

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "r") as f:
    content = f.read()

# First we remove the assignments in the movable block
new_block = """
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
"""
content = re.sub(r'        when \(dayOfYear\) \{.*?christTheKingDayOfYear -> \{ feastName = "Our Lord Jesus Christ the King"; rank = "Double of I Class"; vestmentColorStr = "White" \}\n        \}', new_block, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "w") as f:
    f.write(content)
