import re
with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "r") as f:
    content = f.read()

content = re.sub(r'"08-22".*', r'"08-22" to CompactFeast("Immaculate Heart of Mary / Commemoration of the Octave", "Double of II Class", "Immaculate Heart of Mary", "Octave of the Assumption & SS. Timothy, Hippolytus, & Symphorian"),', content)

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "w") as f:
    f.write(content)
