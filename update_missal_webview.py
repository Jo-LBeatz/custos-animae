import re

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "r") as f:
    content = f.read()

new_missal_content = """@Composable
private fun MissalContent(
    viewModel: MainViewModel?,
    subTab: Int,
    onSubTabChange: (Int) -> Unit,
    languageMode: String
) {
    val selectedCalDate by (viewModel?.selectedCalendarDate ?: kotlinx.coroutines.flow.MutableStateFlow(java.util.Calendar.getInstance())).androidx.lifecycle.compose.collectAsStateWithLifecycle()
    
    val month = selectedCalDate.get(java.util.Calendar.MONTH) + 1
    val day = selectedCalDate.get(java.util.Calendar.DAY_OF_MONTH)
    val year = selectedCalDate.get(java.util.Calendar.YEAR)
    val dateStr = String.format("%02d-%02d-%04d", month, day, year)
    val url = "https://divinumofficium.com/cgi-bin/missa/missa.pl?date1=$dateStr&rubric=1954&interlinear=0"
    
    Column(modifier = Modifier.fillMaxSize()) {
        WebViewMissalContent(url = url)
    }
}
"""

content = re.sub(r'@Composable\s*private fun MissalContent.*?^@Composable\s*private fun OfficeContent', new_missal_content + '\n@Composable\nprivate fun OfficeContent', content, flags=re.DOTALL | re.MULTILINE)

with open("app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt", "w") as f:
    f.write(content)
