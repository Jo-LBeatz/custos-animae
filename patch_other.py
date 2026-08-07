with open('app/src/main/java/com/example/ui/screens/PrayerLibraryScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                    Text(
                        text = prayer.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        maxLines = 1,
                        softWrap = false
                    )''',
'''                    com.example.ui.components.ResponsiveText(
                        text = prayer.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )'''
)

with open('app/src/main/java/com/example/ui/screens/PrayerLibraryScreen.kt', 'w') as f:
    f.write(text)

with open('app/src/main/java/com/example/ui/screens/StainedGlassDashboardScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                    Text(
                        text = "1954 Traditional Liturgical Calendar Season Tracker",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        maxLines = 1,
                        softWrap = false
                    )''',
'''                    com.example.ui.components.ResponsiveText(
                        text = "1954 Traditional Liturgical Calendar Season Tracker",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )'''
)

with open('app/src/main/java/com/example/ui/screens/StainedGlassDashboardScreen.kt', 'w') as f:
    f.write(text)
