with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'r') as f:
    text = f.read()

if 'import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset' not in text:
    text = text.replace('import androidx.compose.material3.*', 'import androidx.compose.material3.*\nimport androidx.compose.material3.TabRowDefaults.tabIndicatorOffset')

with open('app/src/main/java/com/example/ui/screens/CalendarScreen.kt', 'w') as f:
    f.write(text)

with open('app/src/main/java/com/example/ui/screens/MonthlyCalendarGrid.kt', 'r') as f:
    text = f.read()

if 'import androidx.compose.foundation.BorderStroke' not in text:
    text = text.replace('import androidx.compose.foundation.background', 'import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.background')

with open('app/src/main/java/com/example/ui/screens/MonthlyCalendarGrid.kt', 'w') as f:
    f.write(text)
