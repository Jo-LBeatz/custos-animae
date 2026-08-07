with open('app/src/main/java/com/example/ui/screens/RuleOfLifeScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                            Text(
                                text = freq,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) GoldBright else TextMutedParchment,
                                maxLines = 1,
                                softWrap = false
                            )''',
'''                            com.example.ui.components.ResponsiveText(
                                text = freq,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) GoldBright else TextMutedParchment,
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )'''
)

with open('app/src/main/java/com/example/ui/screens/RuleOfLifeScreen.kt', 'w') as f:
    f.write(text)

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                            Text(
                                text = saint.location,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                softWrap = false
                            )''',
'''                            com.example.ui.components.ResponsiveText(
                                text = saint.location,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )'''
)

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'w') as f:
    f.write(text)
