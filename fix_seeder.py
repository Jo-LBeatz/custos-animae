import re

with open("app/src/main/java/com/example/data/calendar/Roman1954DataSeeder.kt", "r") as f:
    content = f.read()

new_logic = """
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
"""

content = re.sub(r'\n\s*val feastTitle = when \{.*?else -> info\.rank\n\s*\}', new_logic, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/calendar/Roman1954DataSeeder.kt", "w") as f:
    f.write(content)
