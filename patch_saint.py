with open('app/src/main/java/com/example/ui/screens/SaintMeditationScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                            Text(
                                text = virtue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                maxLines = 1,
                                softWrap = false
                            )''',
'''                            com.example.ui.components.ResponsiveText(
                                text = virtue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )'''
)

with open('app/src/main/java/com/example/ui/screens/SaintMeditationScreen.kt', 'w') as f:
    f.write(text)
