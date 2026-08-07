import re

with open('app/src/main/java/com/example/ui/screens/RuleOfLifeScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
'''                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) GoldBright else TextMutedParchment,
                                maxLines = 1,
                                softWrap = false
                            )''',
'''                            com.example.ui.components.ResponsiveText(
                                text = filter,
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
