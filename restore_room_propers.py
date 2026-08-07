import re

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "r") as f:
    content = f.read()

new_content = """    val dbPropersState by (viewModel?.selectedMissalEntry ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()
    val propers = dbPropersState?.toMassProper() ?: DailyPropers1954.getTodayProper()"""

content = re.sub(r'    val selectedCalDate by \(viewModel\?\.selectedCalendarDate \?: kotlinx\.coroutines\.flow\.MutableStateFlow\(java\.util\.Calendar\.getInstance\(\)\)\)\.collectAsStateWithLifecycle\(\)\n    val propers = DailyPropers1954\.getTodayProper\(selectedCalDate\)', new_content, content)

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "w") as f:
    f.write(content)
