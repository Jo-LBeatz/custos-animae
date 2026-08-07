import re

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "r") as f:
    content = f.read()

new_content = """    val selectedCalDate by (viewModel?.selectedCalendarDate ?: kotlinx.coroutines.flow.MutableStateFlow(java.util.Calendar.getInstance())).androidx.lifecycle.compose.collectAsStateWithLifecycle()
    val propers = DailyPropers1954.getTodayProper(selectedCalDate)"""

content = re.sub(r'    val dbPropersState by \(viewModel\?\.selectedMissalEntry \?: MutableStateFlow\(null\)\)\.collectAsStateWithLifecycle\(\)\n    val propers = dbPropersState\?\.toMassProper\(\) \?: DailyPropers1954\.getTodayProper\(\)', new_content, content)

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "w") as f:
    f.write(content)
