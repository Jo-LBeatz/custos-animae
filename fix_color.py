import re

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "r") as f:
    content = f.read()

# Add a check for movable feasts colors in the else block
new_else = """        } else {
            if (feastName.isEmpty()) {
                feastName = when (dayOfWeek) {
                    Calendar.SUNDAY -> "Sunday in ${season.displayName}"
                    else -> "Feria in ${season.displayName}"
                }
            } else {
                val lowerFeast = feastName.lowercase()
                if (lowerFeast.contains("martyr") || lowerFeast.contains("pentecost")) {
                    vestmentColorStr = "Red"
                } else if (lowerFeast.contains("sacred heart") || lowerFeast.contains("christ") || lowerFeast.contains("family") || lowerFeast.contains("name") || lowerFeast.contains("trinity") || lowerFeast.contains("ascension") || lowerFeast.contains("easter")) {
                    vestmentColorStr = "White"
                }
            }
        }"""
        
content = re.sub(r'        } else \{\n            if \(feastName\.isEmpty\(\)\) \{\n                feastName = when \(dayOfWeek\) \{\n                    Calendar\.SUNDAY -> "Sunday in \$\{season\.displayName\}"\n                    else -> "Feria in \$\{season\.displayName\}"\n                \}\n            \}\n        \}', new_else, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/calendar/Roman1954Calendar.kt", "w") as f:
    f.write(content)
