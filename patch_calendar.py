with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            maxLines = 1,
                            softWrap = false
                        )''',
'''                        com.example.ui.components.ResponsiveText(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            maxLines = 2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )'''
)

with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'w') as f:
    f.write(text)
