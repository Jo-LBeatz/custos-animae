import re

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "r") as f:
    content = f.read()

# Dec 26-31
content = re.sub(r'"12-26".*', r'"12-26" to CompactFeast("St. Stephen, the First Martyr / Commemoration of the Octave of the Nativity", "Double of II Class", "St. Stephen", "Octave of the Nativity"),', content)
content = re.sub(r'"12-27".*', r'"12-27" to CompactFeast("St. John, Apostle & Evangelist / Commemoration of the Octaves", "Double of II Class", "St. John the Evangelist", "Octaves of Nativity & St. Stephen"),', content)
content = re.sub(r'"12-28".*', r'"12-28" to CompactFeast("Holy Innocents, Martyrs / Commemoration of the Octaves", "Double of II Class", "The Holy Innocents", "Octaves of Nativity, St. Stephen & St. John"),', content)
content = re.sub(r'"12-29".*', r'"12-29" to CompactFeast("St. Thomas of Canterbury, Bishop & Martyr / Commemoration of the Octaves", "Double", "St. Thomas of Canterbury", "Octaves of Nativity, St. Stephen, St. John, & Innocents"),', content)
content = re.sub(r'"12-30".*', r'"12-30" to CompactFeast("Day within the Octave of the Nativity", "Semi-Double", "Octave of the Nativity"),', content)
content = re.sub(r'"12-31".*', r'"12-31" to CompactFeast("St. Sylvester I, Pope & Confessor / Commemoration of the Octaves", "Double", "St. Sylvester I", "Octaves of Nativity, St. Stephen, St. John, & Innocents"),', content)

# Jan 1-5
content = re.sub(r'"01-01".*', r'"01-01" to CompactFeast("Circumcision of Our Lord and Octave of the Nativity", "Double of II Class", "Our Lord Jesus Christ"),', content)
content = re.sub(r'"01-02".*', r'"01-02" to CompactFeast("Octave of St. Stephen, Proto-Martyr", "Double", "St. Stephen"),', content)
content = re.sub(r'"01-03".*', r'"01-03" to CompactFeast("Octave of St. John, Apostle & Evangelist", "Double", "St. John"),', content)
content = re.sub(r'"01-04".*', r'"01-04" to CompactFeast("Octave of the Holy Innocents, Martyrs", "Double", "The Holy Innocents"),', content)

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "w") as f:
    f.write(content)
