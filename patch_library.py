import re

with open("app/src/main/java/com/example/ui/screens/PrayerLibraryScreen.kt", "r") as f:
    content = f.read()

target = "    var manualIntention by rememberSaveable { mutableStateOf(\"\") }"
replacement = "    val manualIntention by viewModel.manualDevotionalIntention.collectAsStateWithLifecycle()"

content = content.replace(target, replacement)
content = content.replace("onManualIntentionChange = { manualIntention = it }", "onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }")

with open("app/src/main/java/com/example/ui/screens/PrayerLibraryScreen.kt", "w") as f:
    f.write(content)
print("Patched PrayerLibraryScreen.kt.")
