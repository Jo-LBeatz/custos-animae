import re

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "r") as f:
    content = f.read()

# Make a bunch of specific replacements for pre-1955
replacements = [
    # January
    (r'"01-11".*', r'"01-11" to CompactFeast("St. Hyginus, Pope & Martyr", "Simple", "St. Hyginus"),'),
    (r'"01-18".*', r'"01-18" to CompactFeast("St. Peter\'s Chair at Rome", "Double Major", "St. Peter\'s Chair", "St. Paul & St. Prisca"),'),
    # February
    (r'"02-22".*', r'"02-22" to CompactFeast("St. Peter\'s Chair at Antioch", "Double Major", "St. Peter\'s Chair at Antioch"),'),
    (r'"02-24".*', r'"02-24" to CompactFeast("St. Matthias, Apostle", "Double of II Class", "St. Matthias"),'),
    # March
    (r'"03-24".*', r'"03-24" to CompactFeast("St. Gabriel the Archangel", "Double Major", "St. Gabriel"),'),
    # May
    (r'"05-01".*', r'"05-01" to CompactFeast("SS. Philip & James, Apostles", "Double of II Class", "SS. Philip & James"),'),
    (r'"05-03".*', r'"05-03" to CompactFeast("Finding of the Holy Cross", "Double of II Class", "Holy Cross", "SS. Alexander, Eventius, Theodolus, & Juvenal"),'),
    (r'"05-06".*', r'"05-06" to CompactFeast("St. John before the Latin Gate", "Double Major", "St. John the Apostle"),'),
    (r'"05-08".*', r'"05-08" to CompactFeast("Apparition of St. Michael the Archangel", "Double Major", "St. Michael"),'),
    (r'"05-11".*', r'"05-11" to CompactFeast("Feria", "Feria", ""),'),
    # July
    (r'"07-01".*', r'"07-01" to CompactFeast("Most Precious Blood of Our Lord Jesus Christ", "Double of I Class", "Most Precious Blood"),'),
    (r'"07-29".*', r'"07-29" to CompactFeast("St. Martha, Virgin", "Semi-Double", "St. Martha", "Ss. Felix II, Pope, Simplicius, Faustinus, & Beatrix, Martyrs"),'),
    # August
    (r'"08-01".*', r'"08-01" to CompactFeast("St. Peter in Chains", "Double Major", "St. Peter in Chains", "Holy Machabees"),'),
    # October
    (r'"10-11".*', r'"10-11" to CompactFeast("Maternity of the Blessed Virgin Mary", "Double of II Class", "Maternity of Mary"),'),
    (r'"10-24".*', r'"10-24" to CompactFeast("St. Raphael the Archangel", "Double Major", "St. Raphael"),'),
]

for pattern, repl in replacements:
    content = re.sub(pattern, repl, content)

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "w") as f:
    f.write(content)
