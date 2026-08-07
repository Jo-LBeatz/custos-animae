import re

with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'r') as f:
    text = f.read()

# Add the enum and viewMode state
enum_code = """
enum class CalendarViewMode { MONTHLY, DAILY }

"""

text = text.replace(
'''@Composable
fun CalendarScreen(
    viewModel: MainViewModel
) {''',
enum_code + '''@Composable
fun CalendarScreen(
    viewModel: MainViewModel
) {
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTHLY) }
'''
)

# Replace the beginning of the Column to include the toggle button and the view mode switch
# Wait, the whole Column holds the GothicHeader and then the content. We can swap the content below GothicHeader.
# Wait, the current offline search and filters might be applicable to both?
# If we are in Monthly, we show the grid.
# If we are in Daily, we show the daily details.

header_replacement = """
        GothicHeader(
            title = "1954 ROMAN CALENDAR",
            subtitle = "Pre-1955 Liturgical Seasons, Ember Days & Vigils",
            badgeText = "${String.format("%02d/%02d", month, day)}/$year"
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TabRow(
                selectedTabIndex = if (viewMode == CalendarViewMode.MONTHLY) 0 else 1,
                containerColor = Color.Transparent,
                contentColor = GoldBright,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (viewMode == CalendarViewMode.MONTHLY) 0 else 1]),
                        color = GoldBright
                    )
                },
                divider = { }
            ) {
                Tab(
                    selected = viewMode == CalendarViewMode.MONTHLY,
                    onClick = { viewMode = CalendarViewMode.MONTHLY },
                    text = { Text("MONTHLY GRID") }
                )
                Tab(
                    selected = viewMode == CalendarViewMode.DAILY,
                    onClick = { viewMode = CalendarViewMode.DAILY },
                    text = { Text("DAILY DETAILS") }
                )
            }
        }
        
        if (viewMode == CalendarViewMode.MONTHLY) {
            MonthlyCalendarView(
                calendarState = calendarState,
                onDateSelected = { newCal ->
                    viewModel.setCalendarDate(newCal)
                    viewMode = CalendarViewMode.DAILY
                },
                onMonthChanged = { offset ->
                    val newCal = calendarState.clone() as Calendar
                    newCal.add(Calendar.MONTH, offset)
                    viewModel.setCalendarDate(newCal)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(1.dp, DarkCathedralCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Legend", style = MaterialTheme.typography.labelMedium, color = GoldBright)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        LegendItem(color = Color(0xFFE65100), label = "Fast/Abstinence")
                        LegendItem(color = Color(0xFF7B1FA2), label = "Ember Day")
                        LegendItem(color = GoldBright, label = "I Class Feast")
                    }
                }
            }
        } else {
"""

text = text.replace(
'''        GothicHeader(
            title = "1954 ROMAN CALENDAR",
            subtitle = "Pre-1955 Liturgical Seasons, Ember Days & Vigils",
            badgeText = "${String.format("%02d/%02d", month, day)}/$year"
        )''',
header_replacement
)

# Close the else block right before `if (activePrayerForDialog != null) {`
text = text.replace(
'''    if (activePrayerForDialog != null) {''',
'''        }
    if (activePrayerForDialog != null) {'''
)


with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'w') as f:
    f.write(text)

