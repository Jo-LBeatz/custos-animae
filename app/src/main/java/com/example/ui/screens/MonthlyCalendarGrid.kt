package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calendar.LiturgicalDayInfo
import com.example.data.calendar.LiturgicalSeason
import com.example.data.calendar.Roman1954Calendar
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun CustomCalendarAdapter(
    calendarState: Calendar,
    onDateSelected: (Calendar) -> Unit,
    onMonthChanged: (Int) -> Unit // +1 for next, -1 for prev
) {
    val year = calendarState.get(Calendar.YEAR)
    val month = calendarState.get(Calendar.MONTH)
    
    val daysInMonth = remember(year, month) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    
    val firstDayOfWeek = remember(year, month) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.get(Calendar.DAY_OF_WEEK)
    }
    
    val days = remember(year, month) {
        val list = mutableListOf<CalendarCellData?>()
        // Add empty slots for the offset
        for (i in 1 until firstDayOfWeek) {
            list.add(null)
        }
        for (i in 1..daysInMonth) {
            val info = Roman1954Calendar.getLiturgicalInfoForDate(year, month + 1, i)
            list.add(CalendarCellData(i, info))
        }
        list
    }

    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val containerBg = if (isLightTheme) MaterialTheme.colorScheme.surface else DarkCathedralSurface
    val cardBorder = if (isLightTheme) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else GoldSacred
    val headerTextColor = if (isLightTheme) CrimsonBurgundy else GoldBright
    val dayOfWeekColor = if (isLightTheme) GoldDark else GoldSacred
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Month/Year and Chevrons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChanged(-1) }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = headerTextColor)
                }
                
                Text(
                    text = "${calendarState.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())} $year",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = headerTextColor
                )
                
                IconButton(onClick = { onMonthChanged(1) }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month", tint = headerTextColor)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days of week header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
                daysOfWeek.forEach { d ->
                    Text(
                        text = d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = dayOfWeekColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            val today = remember { Calendar.getInstance() }
            val isCurrentMonthAndYear = remember(year, month, today) {
                today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month
            }
            val todayDay = if (isCurrentMonthAndYear) today.get(Calendar.DAY_OF_MONTH) else -1
            val selectedDay = calendarState.get(Calendar.DAY_OF_MONTH)

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(days.size, key = { index -> days[index]?.info?.dateString ?: "empty_$index" }) { index ->
                    val cell = days[index]
                    if (cell == null) {
                        Spacer(modifier = Modifier.aspectRatio(0.75f))
                    } else {
                        val isSelected = cell.day == selectedDay
                        val isToday = cell.day == todayDay
                        CalendarCell(
                            data = cell,
                            isSelected = isSelected,
                            isToday = isToday,
                            onClick = {
                                val newCal = calendarState.clone() as Calendar
                                newCal.set(Calendar.DAY_OF_MONTH, cell.day)
                                onDateSelected(newCal)
                            }
                        )
                    }
                }
            }
        }
    }
}

data class CalendarCellData(
    val day: Int,
    val info: LiturgicalDayInfo
)

@Composable
fun CalendarCell(
    data: CalendarCellData,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f

    val vestmentColor = when (data.info.vestmentColor.lowercase()) {
        "white" -> if (isLightTheme) Color(0xFFEFECE5) else Color.White
        "red" -> LiturgicalRed
        "green" -> LiturgicalGreen
        "violet" -> LiturgicalViolet
        "black" -> Color(0xFF1C1D21) // Slate Black
        "rose" -> LiturgicalRose
        "gold" -> GoldBright
        else -> if (isLightTheme) Color(0xFFDFD9C6) else DarkCathedralCardBorder
    }

    val isProperOrHolyDay = data.info.feastName.isNotBlank() || data.info.rank.contains("I Class", ignoreCase = true) || data.info.rank.contains("Holy Day", ignoreCase = true)

    val cellBgColor = if (isLightTheme) {
        when {
            isSelected -> Color(0xFFDFD9C4)
            isToday -> Color(0xFFFCE6E6)
            isProperOrHolyDay -> Color(0xFFFAF2CE)
            else -> Color(0xFFF6F2E7)
        }
    } else {
        when {
            isSelected -> Color(0xFF1E2631) // Slightly highlighted slate-blue surface
            isToday -> Color(0xFF221317) // Subtle warm crimson/burgundy tint
            isProperOrHolyDay -> Color(0xFF2B2013) // Distinct golden-brown accent color for proper feasts
            else -> Color(0xFF131920) // Deep dark cathedral slate
        }
    }

    val cellBorder = if (isLightTheme) {
        when {
            isSelected -> BorderStroke(1.5.dp, GoldSacred)
            isToday -> BorderStroke(1.5.dp, CrimsonBurgundy)
            isProperOrHolyDay -> BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
            else -> BorderStroke(0.5.dp, Color(0xFFDFD9C6))
        }
    } else {
        when {
            isSelected -> BorderStroke(1.5.dp, GoldSacred)
            isToday -> BorderStroke(1.5.dp, CrimsonBurgundy)
            isProperOrHolyDay -> BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
            else -> BorderStroke(0.5.dp, DarkCathedralCardBorder.copy(alpha = 0.6f))
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .background(cellBgColor, RoundedCornerShape(8.dp))
            .border(cellBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Liturgical ribbon marker on the left edge
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight()
                    .background(
                        vestmentColor,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            )

            // Main cell content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                // Header row: Day Number + Special Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isToday) {
                        // Distinct circular border for the current day
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .border(1.5.dp, CrimsonBurgundy, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.day.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = CrimsonBurgundy
                            )
                        }
                    } else {
                        Text(
                            text = data.day.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) {
                                    if (isLightTheme) GoldDark else GoldBright
                                } else {
                                    if (isLightTheme) Color(0xFF1C1917) else ParchmentLight
                                }
                            )
                        )
                    }

                    // Top-right indicator row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (data.info.isFastingDay || data.info.isAbstinenceDay) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color(0xFFE65100), CircleShape)
                            )
                        }
                        if (data.info.isEmberDay) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color(0xFF7B1FA2), CircleShape)
                            )
                        }
                        if (data.info.feastName.isNotBlank() && data.info.rank.contains("I Class", ignoreCase = true)) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(GoldBright, CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Feast Name Text, taking all available space
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val feastText = if (data.info.feastName.isNotBlank()) {
                        data.info.feastName
                    } else if (data.info.notes.isNotBlank()) {
                        data.info.notes
                    } else {
                        data.info.rank
                    }
                    
                    Text(
                        text = feastText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 7.5.sp,
                            lineHeight = 8.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        ),
                        softWrap = true,
                        color = if (isSelected) {
                            if (isLightTheme) GoldDark else GoldLight
                        } else {
                            if (isLightTheme) Color(0xFF44403C) else TextMutedParchment
                        }
                    )
                }
            }
        }
    }
}
