import re

with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "r") as f:
    content = f.read()

# Replace Jan 7-13
content = re.sub(r'"01-07".*', r'"01-07" to CompactFeast("Day within the Octave of the Epiphany", "Semi-Double", "Epiphany Octave"),', content)
content = re.sub(r'"01-08".*', r'"01-08" to CompactFeast("Day within the Octave of the Epiphany", "Semi-Double", "Epiphany Octave"),', content)
content = re.sub(r'"01-09".*', r'"01-09" to CompactFeast("Day within the Octave of the Epiphany", "Semi-Double", "Epiphany Octave"),', content)
content = re.sub(r'"01-10".*', r'"01-10" to CompactFeast("Day within the Octave of the Epiphany", "Semi-Double", "Epiphany Octave"),', content)
content = re.sub(r'"01-11".*', r'"01-11" to CompactFeast("Day within the Octave of the Epiphany / Commemoration of St. Hyginus", "Semi-Double", "Epiphany Octave", "St. Hyginus"),', content)
content = re.sub(r'"01-12".*', r'"01-12" to CompactFeast("Day within the Octave of the Epiphany", "Semi-Double", "Epiphany Octave"),', content)
content = re.sub(r'"01-13".*', r'"01-13" to CompactFeast("Octave Day of the Epiphany", "Double Major", "Epiphany Octave"),', content)

# SS Peter and Paul (Jun 29 - Jul 6)
content = re.sub(r'"06-30".*', r'"06-30" to CompactFeast("Commemoration of St. Paul, Apostle / Commemoration of the Octave", "Double Major", "St. Paul", "St. Peter & the Octave"),', content)
content = re.sub(r'"07-01".*', r'"07-01" to CompactFeast("Most Precious Blood of Our Lord Jesus Christ / Commemoration of the Octave", "Double of I Class", "Most Precious Blood", "Octave of SS. Peter & Paul"),', content)
content = re.sub(r'"07-02".*', r'"07-02" to CompactFeast("Visitation of the Blessed Virgin Mary / Commemoration of the Octave", "Double of II Class", "Visitation", "SS. Processus & Martinian, & the Octave"),', content)
content = re.sub(r'"07-03".*', r'"07-03" to CompactFeast("St. Irenaeus, Bishop & Martyr / Commemoration of the Octave", "Double", "St. Irenaeus", "Octave of SS. Peter & Paul"),', content)
content = re.sub(r'"07-04".*', r'"07-04" to CompactFeast("Day within the Octave of SS. Peter and Paul", "Semi-Double", "Octave of SS. Peter & Paul"),', content)
content = re.sub(r'"07-05".*', r'"07-05" to CompactFeast("St. Anthony Mary Zaccaria, Confessor / Commemoration of the Octave", "Double", "St. Anthony Mary Zaccaria", "Octave of SS. Peter & Paul"),', content)
content = re.sub(r'"07-06".*', r'"07-06" to CompactFeast("Octave Day of SS. Peter and Paul, Apostles", "Double Major", "Octave of SS. Peter & Paul"),', content)

# St. Lawrence (Aug 10 - Aug 17)
content = re.sub(r'"08-11".*', r'"08-11" to CompactFeast("Day within the Octave of St. Lawrence / Commemoration of SS. Tiburtius & Susanna", "Semi-Double", "Octave of St. Lawrence", "SS. Tiburtius & Susanna"),', content)
content = re.sub(r'"08-12".*', r'"08-12" to CompactFeast("St. Clare, Virgin / Commemoration of the Octave", "Double", "St. Clare", "Octave of St. Lawrence"),', content)
content = re.sub(r'"08-13".*', r'"08-13" to CompactFeast("Day within the Octave of St. Lawrence / Commemoration of SS. Hippolytus & Cassian", "Semi-Double", "Octave of St. Lawrence", "SS. Hippolytus & Cassian"),', content)
content = re.sub(r'"08-14".*', r'"08-14" to CompactFeast("Vigil of the Assumption / Commemoration of the Octave", "Vigil", "Vigil of the Assumption", "Octave of St. Lawrence & St. Eusebius"),', content)
content = re.sub(r'"08-15".*', r'"08-15" to CompactFeast("Assumption of the Blessed Virgin Mary", "Double of I Class", "Assumption of Mary"),', content) # Comm. of St. Lawrence octave is omitted on Doubles of I Class
content = re.sub(r'"08-16".*', r'"08-16" to CompactFeast("St. Joachim, Father of the Blessed Virgin / Commemoration of the Octaves", "Double of II Class", "St. Joachim", "Octave of the Assumption & St. Lawrence"),', content)
content = re.sub(r'"08-17".*', r'"08-17" to CompactFeast("St. Hyacinth, Confessor / Commemoration of the Octaves", "Double", "St. Hyacinth", "Octave of the Assumption & St. Lawrence"),', content) # Also Octave Day of St. Lawrence

# Assumption (Aug 15 - Aug 22)
content = re.sub(r'"08-18".*', r'"08-18" to CompactFeast("Day within the Octave of the Assumption / Commemoration of St. Agapitus", "Semi-Double", "Octave of the Assumption", "St. Agapitus"),', content)
content = re.sub(r'"08-19".*', r'"08-19" to CompactFeast("St. John Eudes, Confessor / Commemoration of the Octave", "Double", "St. John Eudes", "Octave of the Assumption"),', content)
content = re.sub(r'"08-20".*', r'"08-20" to CompactFeast("St. Bernard, Abbot & Doctor / Commemoration of the Octave", "Double", "St. Bernard", "Octave of the Assumption"),', content)
content = re.sub(r'"08-21".*', r'"08-21" to CompactFeast("St. Jane Frances Fremiot de Chantal, Widow / Commemoration of the Octave", "Double", "St. Jane Frances", "Octave of the Assumption"),', content)
content = re.sub(r'"08-22".*', r'"08-22" to CompactFeast("Octave Day of the Assumption / Commemoration of SS. Timothy, Hippolytus, & Symphorian", "Double Major", "Octave of the Assumption", "SS. Timothy, Hippolytus, & Symphorian"),', content)

# Immaculate Heart of Mary in pre-1955 is NOT Aug 22 (it's Aug 22 in 1962). Wait! Immaculate Heart is Aug 22 in 1962, but it's on Aug 22 in 1944. Wait, Pius XII instituted the Immaculate Heart on Aug 22 in 1944! So it is Aug 22 pre-1955!
# Wait! Let me double check if Octave of Assumption was suppressed before 1955. Yes, in 1955 all octaves except Christmas, Easter, Pentecost were abolished. So pre-1955 (e.g. 1954) had the Octave of Assumption. But Immaculate Heart was on Aug 22. Wait! Immaculate Heart of Mary replaced the Octave Day of the Assumption in 1944. 
# So on Aug 22: "Immaculate Heart of Mary / Commemoration of SS. Timothy, Hippolytus, & Symphorian". Let me adjust that.
content = re.sub(r'"08-22".*', r'"08-22" to CompactFeast("Immaculate Heart of Mary / Commemoration of SS. Timothy, Hippolytus, & Symphorian", "Double of II Class", "Immaculate Heart of Mary", "SS. Timothy, Hippolytus, & Symphorian"),', content)

# Nativity of BVM (Sep 8 - Sep 15)
content = re.sub(r'"09-09".*', r'"09-09" to CompactFeast("Day within the Octave of the Nativity of the BVM / Commemoration of St. Gorgonius", "Semi-Double", "Octave of the Nativity", "St. Gorgonius"),', content)
content = re.sub(r'"09-10".*', r'"09-10" to CompactFeast("St. Nicholas of Tolentino, Confessor / Commemoration of the Octave", "Double", "St. Nicholas of Tolentino", "Octave of the Nativity"),', content)
content = re.sub(r'"09-11".*', r'"09-11" to CompactFeast("Day within the Octave of the Nativity of the BVM / Commemoration of SS. Protus & Hyacinth", "Semi-Double", "Octave of the Nativity", "SS. Protus & Hyacinth"),', content)
content = re.sub(r'"09-12".*', r'"09-12" to CompactFeast("The Holy Name of Mary / Commemoration of the Octave", "Double Major", "Holy Name of Mary", "Octave of the Nativity"),', content)
content = re.sub(r'"09-13".*', r'"09-13" to CompactFeast("Day within the Octave of the Nativity of the BVM", "Semi-Double", "Octave of the Nativity"),', content)
content = re.sub(r'"09-14".*', r'"09-14" to CompactFeast("Exaltation of the Holy Cross / Commemoration of the Octave", "Double Major", "Exaltation of the Holy Cross", "Octave of the Nativity"),', content)
content = re.sub(r'"09-15".*', r'"09-15" to CompactFeast("Seven Sorrows of the Blessed Virgin Mary / Commemoration of St. Nicomedes", "Double of II Class", "Seven Sorrows of Mary", "St. Nicomedes"),', content) # Also Octave Day

# All Saints (Nov 1 - Nov 8)
content = re.sub(r'"11-02".*', r'"11-02" to CompactFeast("Commemoration of All Souls", "Double", "All Souls", "Octave of All Saints"),', content)
content = re.sub(r'"11-03".*', r'"11-03" to CompactFeast("Day within the Octave of All Saints", "Semi-Double", "Octave of All Saints"),', content)
content = re.sub(r'"11-04".*', r'"11-04" to CompactFeast("St. Charles Borromeo, Bishop & Confessor / Commemoration of the Octave", "Double", "St. Charles Borromeo", "Octave of All Saints & SS. Vitalis & Agricola"),', content)
content = re.sub(r'"11-05".*', r'"11-05" to CompactFeast("Day within the Octave of All Saints", "Semi-Double", "Octave of All Saints"),', content)
content = re.sub(r'"11-06".*', r'"11-06" to CompactFeast("Day within the Octave of All Saints", "Semi-Double", "Octave of All Saints"),', content)
content = re.sub(r'"11-07".*', r'"11-07" to CompactFeast("Day within the Octave of All Saints", "Semi-Double", "Octave of All Saints"),', content)
content = re.sub(r'"11-08".*', r'"11-08" to CompactFeast("Octave Day of All Saints / Commemoration of the Four Crowned Martyrs", "Double Major", "Octave of All Saints", "Four Crowned Martyrs"),', content)

# Immaculate Conception (Dec 8 - Dec 15)
content = re.sub(r'"12-09".*', r'"12-09" to CompactFeast("Day within the Octave of the Immaculate Conception", "Semi-Double", "Octave of Immaculate Conception"),', content)
content = re.sub(r'"12-10".*', r'"12-10" to CompactFeast("Day within the Octave of the Immaculate Conception / Commemoration of St. Melchiades", "Semi-Double", "Octave of Immaculate Conception", "St. Melchiades"),', content)
content = re.sub(r'"12-11".*', r'"12-11" to CompactFeast("St. Damasus I, Pope & Confessor / Commemoration of the Octave", "Double", "St. Damasus I", "Octave of Immaculate Conception"),', content)
content = re.sub(r'"12-12".*', r'"12-12" to CompactFeast("Day within the Octave of the Immaculate Conception", "Semi-Double", "Octave of Immaculate Conception"),', content)
content = re.sub(r'"12-13".*', r'"12-13" to CompactFeast("St. Lucy, Virgin & Martyr / Commemoration of the Octave", "Double", "St. Lucy", "Octave of Immaculate Conception"),', content)
content = re.sub(r'"12-14".*', r'"12-14" to CompactFeast("Day within the Octave of the Immaculate Conception", "Semi-Double", "Octave of Immaculate Conception"),', content)
content = re.sub(r'"12-15".*', r'"12-15" to CompactFeast("Octave Day of the Immaculate Conception", "Double Major", "Octave of Immaculate Conception"),', content)


with open("app/src/main/java/com/example/data/calendar/SaintData.kt", "w") as f:
    f.write(content)
