import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    text = f.read()

liturgical_card = """
        Spacer(modifier = Modifier.height(16.dp))

        // Liturgical Season Notifications Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Liturgical Seasons",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                    }
                    Switch(
                        checked = liturgicalSeasonEnabled,
                        onCheckedChange = { enabled ->
                            liturgicalSeasonEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                notificationsEnabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                angelusEnabled,
                                sacredHeartTimeInput,
                                sacredHeartEnabled,
                                immaculateHeartTimeInput,
                                immaculateHeartEnabled,
                                enabled
                            )
                            Toast.makeText(context, "Liturgical season notifications ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Automated announcements at 8:00 AM on the first day of major traditional liturgical seasons (e.g., Advent, Lent, Paschaltide).",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )
            }
        }
"""

text = text.replace('        // Background Execution Card', liturgical_card + '\n        // Background Execution Card')

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(text)
