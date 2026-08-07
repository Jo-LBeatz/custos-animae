import re

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                                Text(
                                    text = currentHour.timeOfDay,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )''',
'''                                com.example.ui.components.ResponsiveText(
                                    text = currentHour.timeOfDay,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 2,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )'''
)

text = text.replace(
'''                            Text(
                                text = entry.season,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                softWrap = false
                            )''',
'''                            com.example.ui.components.ResponsiveText(
                                text = entry.season,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )'''
)

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'w') as f:
    f.write(text)
