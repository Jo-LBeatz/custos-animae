package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CalendarEntry
import com.example.data.calendar.Roman1954Calendar
import com.example.data.calendar.SaintData
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.IlluminatedDivider
import com.example.ui.components.LiturgicalBanner
import com.example.ui.theme.*
import java.util.Calendar


enum class CalendarViewMode { MONTHLY, DAILY }

@Composable
fun CalendarScreen(
    viewModel: MainViewModel
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val screenBg = MaterialTheme.colorScheme.background
    val cardBg = if (isLightTheme) MaterialTheme.colorScheme.surface else DarkCathedralSurface
    val cardBorder = if (isLightTheme) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else DarkCathedralCardBorder
    val textMuted = if (isLightTheme) Color(0xFF44403C) else TextMutedParchment
    val textNormal = if (isLightTheme) Color(0xFF1C1917) else ParchmentLight
    val goldAccent = if (isLightTheme) GoldDark else GoldBright

    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTHLY) }

    val calendarState by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()

    val year = calendarState.get(Calendar.YEAR)
    val month = calendarState.get(Calendar.MONTH) + 1
    val day = calendarState.get(Calendar.DAY_OF_MONTH)

    val info = remember(year, month, day) {
        Roman1954Calendar.getLiturgicalInfoForDate(year, month, day)
    }

    val saintProfile = remember(info.dateString) {
        SaintData.getSaintForDate(info.dateString)
    }

    val searchQuery by viewModel.calendarSearchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.calendarFilter.collectAsStateWithLifecycle()
    val searchResults by viewModel.calendarSearchResults.collectAsStateWithLifecycle()

    var activePrayerForDialog by remember { mutableStateOf<RecommendedPrayer?>(null) }
    var dialogIsLatin by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        val currentScroll = scrollState.value
        val maxScroll = scrollState.maxValue
        
        if (currentScroll <= 10) {
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        } else if (currentScroll > 30) {
            viewModel.setBottomNavVisible(false)
            viewModel.setTopHeaderVisible(false)
        }
        
        val progress = if (maxScroll > 0) currentScroll.toFloat() / maxScroll.toFloat() else 0f
        viewModel.setReadingProgress(progress)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {

        val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()

        androidx.compose.animation.AnimatedVisibility(
            visible = isTopHeaderVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Column {
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
                        contentColor = goldAccent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (viewMode == CalendarViewMode.MONTHLY) 0 else 1]),
                                color = goldAccent
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
            }
        }
        
        if (viewMode == CalendarViewMode.MONTHLY) {
            CustomCalendarAdapter(
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
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Legend", style = MaterialTheme.typography.labelMedium, color = goldAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        LegendItem(color = Color(0xFFE65100), label = "Fast/Abstinence")
                        LegendItem(color = Color(0xFF7B1FA2), label = "Ember Day")
                        LegendItem(color = GoldBright, label = "I Class Feast")
                    }
                }
            }
        } else {


        // Offline Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setCalendarSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calendar_search_input"),
            placeholder = { Text("Search 1954 Calendar (Feasts, Vigils, Saints)...", color = textMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = goldAccent) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setCalendarSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = goldAccent)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldSacred,
                unfocusedBorderColor = cardBorder,
                focusedContainerColor = cardBg,
                unfocusedContainerColor = cardBg,
                focusedTextColor = textNormal,
                unfocusedTextColor = textNormal
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                "ALL" to "All Feasts",
                "VIGILS" to "Vigils",
                "CLASS1" to "I Class Feasts",
                "EMBER" to "Ember Days",
                "FASTING" to "Fasting Days"
            )

            items(filters) { (code, label) ->
                val isSelected = activeFilter == code
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCalendarFilter(code) },
                    label = {
                        com.example.ui.components.ResponsiveText(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            maxLines = 2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldSacred,
                        selectedLabelColor = if (isLightTheme) Color.White else Color.Black,
                        containerColor = cardBg,
                        labelColor = textMuted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = cardBorder,
                        selectedBorderColor = goldAccent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search or Filter Results Panel
        if (searchQuery.isNotBlank() || activeFilter != "ALL") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Offline Calendar Search Results (${searchResults.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = goldAccent
                        )
                        if (searchQuery.isNotBlank() || activeFilter != "ALL") {
                            TextButton(onClick = {
                                viewModel.setCalendarSearchQuery("")
                                viewModel.setCalendarFilter("ALL")
                            }) {
                                Text("Reset", color = textMuted, fontSize = 12.sp)
                            }
                        }
                    }

                    IlluminatedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    if (searchResults.isEmpty()) {
                        Text(
                            text = "No matching feast days or vigils found in offline database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = textMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        searchResults.take(12).forEach { entry ->
                            CalendarSearchResultItem(
                                entry = entry,
                                onClick = {
                                    val newCal = Calendar.getInstance().apply {
                                        set(Calendar.MONTH, entry.month - 1)
                                        set(Calendar.DAY_OF_MONTH, entry.day)
                                    }
                                    viewModel.setCalendarDate(newCal)
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Date Stepper
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newCal = calendarState.clone() as Calendar
                        newCal.add(Calendar.DAY_OF_MONTH, -1)
                        viewModel.setCalendarDate(newCal)
                    },
                    modifier = Modifier.testTag("prev_day_btn")
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = goldAccent)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = textMuted
                    )
                    Text(
                        text = "${calendarState.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())} $day, $year",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = goldAccent
                    )
                }

                IconButton(
                    onClick = {
                        val newCal = calendarState.clone() as Calendar
                        newCal.add(Calendar.DAY_OF_MONTH, 1)
                        viewModel.setCalendarDate(newCal)
                    },
                    modifier = Modifier.testTag("next_day_btn")
                ) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Day", tint = goldAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Liturgical Day Banner
        LiturgicalBanner(info = info)

        Spacer(modifier = Modifier.height(14.dp))

        // Detailed Liturgical Day Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1954 Liturgical Framework Summary",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = goldAccent
                )

                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                DetailRow("Liturgical Season:", info.season.displayName)
                DetailRow("Rank / Feast Class:", info.rank)
                DetailRow("Vestment Color:", info.vestmentColor)
                DetailRow("Quattuor Tempora (Ember Day):", if (info.isEmberDay) "Yes (Traditional Fasting)" else "No")
                DetailRow("Rogation Day:", if (info.isRogationDay) "Yes" else "No")
                DetailRow("Fasting Requirement:", if (info.isFastingDay) "Fasting Required" else "None")
                DetailRow("Abstinence Requirement:", if (info.isAbstinenceDay) "Abstinence from Meat Required" else "None")

                if (saintProfile != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Commemorated Saint:",
                        style = MaterialTheme.typography.labelSmall,
                        color = textMuted
                    )
                    Text(
                        text = saintProfile.saintName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isLightTheme) GoldDark else GoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = saintProfile.biography,
                        style = MaterialTheme.typography.bodySmall,
                        color = textNormal
                    )
                    if (saintProfile.historicity.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = saintProfile.historicity,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (isLightTheme) GoldDark else GoldBright
                        )
                    }
                    if (saintProfile.quote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isLightTheme) Color(0xFFF7F4EB) else Color(0xFF1E222A)),
                            border = BorderStroke(0.5.dp, goldAccent.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Traditional Quote:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = goldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${saintProfile.quote}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = textNormal
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Related Traditional Prayers and Devotions Card
        val recommendations = remember(info, saintProfile, calendarState) {
            getRecommendedPrayers(info, saintProfile, calendarState)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Related Prayers & Devotions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = goldAccent
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Traditional liturgical devotions matched to today's season, weekday, or commemorated feast under the 1954 Rubrics.",
                    style = MaterialTheme.typography.bodySmall,
                    color = textMuted
                )

                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                recommendations.forEachIndexed { idx, prayer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prayer.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isLightTheme) GoldDark else GoldLight
                            )
                            Text(
                                text = prayer.reason,
                                style = MaterialTheme.typography.labelSmall,
                                color = textMuted
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pray button (opens the overlay dialog)
                            Button(
                                onClick = {
                                    activePrayerForDialog = prayer
                                    dialogIsLatin = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLightTheme) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF1E1E28),
                                    contentColor = goldAccent
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(1.dp, GoldSacred.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Pray", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // View in library button
                            IconButton(
                                onClick = {
                                    viewModel.setPrayerSearchQuery(prayer.libraryQuery)
                                    viewModel.setTab(3)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(CrimsonBurgundy, RoundedCornerShape(8.dp))
                                    .border(1.dp, GoldSacred.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Open in Library",
                                    tint = goldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (idx < recommendations.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = cardBorder.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

        }
    if (activePrayerForDialog != null) {
        val prayer = activePrayerForDialog!!
        AlertDialog(
            onDismissRequest = { activePrayerForDialog = null },
            containerColor = cardBg,
            titleContentColor = goldAccent,
            textContentColor = textNormal,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prayer.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = goldAccent
                        )
                        if (prayer.subtitle.isNotBlank()) {
                            Text(
                                text = prayer.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = textMuted
                            )
                        }
                    }

                    if (prayer.latinText.isNotBlank()) {
                        IconButton(
                            onClick = { dialogIsLatin = !dialogIsLatin },
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isLightTheme) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF1E1E28), RoundedCornerShape(8.dp))
                                .border(1.dp, GoldSacred, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = if (dialogIsLatin) "LAT" else "ENG",
                                color = goldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val textToDisplay = if (dialogIsLatin && prayer.latinText.isNotBlank()) {
                        prayer.latinText
                    } else {
                        prayer.englishText
                    }

                    Text(
                        text = textToDisplay,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Serif,
                            lineHeight = 22.sp
                        ),
                        color = textNormal
                    )

                    if (dialogIsLatin && prayer.latinText.isBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(Latin text unavailable for this collect)",
                            style = MaterialTheme.typography.labelSmall,
                            color = textMuted,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setPrayerSearchQuery(prayer.libraryQuery)
                        viewModel.setTab(3) // Switch to Prayers tab
                        activePrayerForDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                    border = BorderStroke(1.dp, GoldSacred)
                ) {
                    Text("Open in Library", color = goldAccent, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { activePrayerForDialog = null }) {
                    Text("Close", color = textMuted)
                }
            }
        )
    }
}

@Composable
fun CalendarSearchResultItem(
    entry: CalendarEntry,
    onClick: () -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val cardBg = if (isLightTheme) MaterialTheme.colorScheme.surface else DarkCathedralSurface
    val cardBorder = if (isLightTheme) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else DarkCathedralCardBorder
    val textNormal = if (isLightTheme) Color(0xFF1C1917) else ParchmentLight
    val textMuted = if (isLightTheme) Color(0xFF44403C) else TextMutedParchment
    val goldAccent = if (isLightTheme) GoldDark else GoldBright

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (isLightTheme) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF1E1E28)),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.dateString,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = goldAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.feastRank,
                        style = MaterialTheme.typography.labelSmall,
                        color = textMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = entry.feastTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textNormal
                )

                if (entry.saintName.isNotBlank() && entry.saintName != entry.feastTitle) {
                    Text(
                        text = entry.saintName,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isLightTheme) GoldDark else GoldLight
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (entry.isVigil) {
                        BadgeTag(text = "VIGIL", color = Color(0xFFD32F2F))
                    }
                    if (entry.isEmberDay) {
                        BadgeTag(text = "EMBER DAY", color = Color(0xFF7B1FA2))
                    }
                    if (entry.isFastingDay) {
                        BadgeTag(text = "FAST", color = Color(0xFFE65100))
                    }
                }
            }

            Text(
                text = "View >",
                style = MaterialTheme.typography.labelSmall,
                color = GoldSacred
            )
        }
    }
}

@Composable
fun BadgeTag(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        contentColor = color,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val labelColor = if (isLightTheme) Color(0xFF55504A) else TextMutedParchment
    val valueColor = if (isLightTheme) Color(0xFF1C1917) else ParchmentLight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = labelColor)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

data class RecommendedPrayer(
    val id: String,
    val title: String,
    val reason: String,
    val category: String,
    val englishText: String,
    val latinText: String = "",
    val subtitle: String = "",
    val libraryQuery: String = ""
)

private data class RosaryRecommendation(
    val title: String,
    val days: String,
    val mysteriesText: String,
    val libraryQuery: String
)

fun getRecommendedPrayers(
    info: com.example.data.calendar.LiturgicalDayInfo,
    saintProfile: com.example.data.calendar.SaintProfile?,
    calendarState: java.util.Calendar
): List<RecommendedPrayer> {
    val list = mutableListOf<RecommendedPrayer>()
    val dayOfWeek = calendarState.get(java.util.Calendar.DAY_OF_WEEK)

    // 1. Seasonal Marian Prayer (Angelus or Regina Caeli)
    if (info.season == com.example.data.calendar.LiturgicalSeason.PASCHALTIDE) {
        val rc = com.example.data.prayers.TraditionalPrayerLibrary.angelusAndRegina.find { it.id == "regina_caeli" }
        if (rc != null) {
            list.add(
                RecommendedPrayer(
                    id = rc.id,
                    title = rc.title,
                    reason = "Paschal Season Marian Antiphon (Easter to Pentecost)",
                    category = rc.category,
                    englishText = rc.englishText,
                    latinText = rc.latinText,
                    subtitle = rc.subtitle,
                    libraryQuery = rc.title
                )
            )
        }
    } else {
        val ang = com.example.data.prayers.TraditionalPrayerLibrary.angelusAndRegina.find { it.id == "angelus" }
        if (ang != null) {
            list.add(
                RecommendedPrayer(
                    id = ang.id,
                    title = ang.title,
                    reason = "Standard Liturgical Season Marian Antiphon (Morning, Noon, Evening)",
                    category = ang.category,
                    englishText = ang.englishText,
                    latinText = ang.latinText,
                    subtitle = ang.subtitle,
                    libraryQuery = ang.title
                )
            )
        }
    }

    // 2. Rosary Mysteries of the Day
    val rec = when {
        info.season == com.example.data.calendar.LiturgicalSeason.LENT || 
        info.season == com.example.data.calendar.LiturgicalSeason.PASSIONTIDE || 
        dayOfWeek == java.util.Calendar.FRIDAY || 
        dayOfWeek == java.util.Calendar.TUESDAY -> {
            val set = com.example.data.prayers.TraditionalPrayerLibrary.rosarySets.find { it.category.contains("Sorrowful") }
            val mysteriesText = set?.mysteries?.joinToString("\n\n") { "${it.number}. ${it.title} (${it.latinTitle})\nFruit: ${it.fruit}\nMeditation: ${it.meditation}" } ?: ""
            RosaryRecommendation("Sorrowful Mysteries", "Tuesdays, Fridays & Lent", mysteriesText, "Sorrowful Mysteries")
        }
        dayOfWeek == java.util.Calendar.SUNDAY || 
        dayOfWeek == java.util.Calendar.SATURDAY || 
        dayOfWeek == java.util.Calendar.WEDNESDAY -> {
            val set = com.example.data.prayers.TraditionalPrayerLibrary.rosarySets.find { it.category.contains("Glorious") }
            val mysteriesText = set?.mysteries?.joinToString("\n\n") { "${it.number}. ${it.title} (${it.latinTitle})\nFruit: ${it.fruit}\nMeditation: ${it.meditation}" } ?: ""
            RosaryRecommendation("Glorious Mysteries", "Wednesdays, Saturdays & Sundays", mysteriesText, "Glorious Mysteries")
        }
        else -> {
            val set = com.example.data.prayers.TraditionalPrayerLibrary.rosarySets.find { it.category.contains("Joyful") }
            val mysteriesText = set?.mysteries?.joinToString("\n\n") { "${it.number}. ${it.title} (${it.latinTitle})\nFruit: ${it.fruit}\nMeditation: ${it.meditation}" } ?: ""
            RosaryRecommendation("Joyful Mysteries", "Mondays & Thursdays", mysteriesText, "Joyful Mysteries")
        }
    }

    list.add(
        RecommendedPrayer(
            id = "rosary_today",
            title = "Holy Rosary: ${rec.title}",
            reason = "Recommended traditional mysteries for today (${rec.days})",
            category = "Holy Rosary",
            englishText = rec.mysteriesText,
            latinText = "",
            subtitle = "Wield the spiritual sword of Mary",
            libraryQuery = rec.libraryQuery
        )
    )

    // 3. St. Michael Prayer for protection (Friday or fast days or general protection)
    if (dayOfWeek == java.util.Calendar.FRIDAY || info.isFastingDay) {
        list.add(
            RecommendedPrayer(
                id = "st_michael",
                title = "Prayer to St. Michael",
                reason = "Spiritual warfare & protection on Friday/fasting days",
                category = "Protection & Spiritual Warfare",
                englishText = "St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.",
                latinText = "Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen.",
                subtitle = "Sancte Michael Archangele",
                libraryQuery = "Prayer to St. Michael"
            )
        )
    }

    // 4. Consecration to the Immaculate Heart of Mary (Saturdays or Marian feasts)
    val isMaryFeast = info.feastName.contains("Mary", ignoreCase = true) || 
                     info.feastName.contains("Nativity", ignoreCase = true) || 
                     info.feastName.contains("Assumption", ignoreCase = true) || 
                     info.feastName.contains("Immaculate", ignoreCase = true) ||
                     info.feastName.contains("Carmel", ignoreCase = true) ||
                     info.feastName.contains("Rosary", ignoreCase = true)

    if (dayOfWeek == java.util.Calendar.SATURDAY || isMaryFeast) {
        val maryPr = com.example.data.prayers.TraditionalPrayerLibrary.devotionsImmaculateHeart.firstOrNull()
        if (maryPr != null) {
            list.add(
                RecommendedPrayer(
                    id = maryPr.id,
                    title = maryPr.title,
                    reason = "Consecrate this Saturday or Marian Feast day to Her Heart",
                    category = maryPr.category,
                    englishText = maryPr.englishText,
                    latinText = maryPr.latinText,
                    subtitle = maryPr.subtitle,
                    libraryQuery = maryPr.title
                )
            )
        }
    }

    // 5. Litany of St. Joseph (Wednesdays, as Wednesday is traditionally dedicated to St. Joseph, or March)
    if (dayOfWeek == java.util.Calendar.WEDNESDAY || info.feastName.contains("Joseph", ignoreCase = true)) {
        list.add(
            RecommendedPrayer(
                id = "litany_st_joseph",
                title = "Litany of St. Joseph",
                reason = "Wednesday is traditionally dedicated to St. Joseph, Patron of Holy Families",
                category = "Litanies & Devotions",
                englishText = "St. Joseph, renowned offspring of David, pray for us. Light of Patriarchs, pray for us. Spouse of the Mother of God, pray for us. Chaste guardian of the Virgin, pray for us. Foster father of the Son of God, pray for us. Diligent protector of Christ, pray for us. Head of the Holy Family, pray for us. Joseph most just, pray for us. Joseph most chaste, pray for us. Joseph most prudent, pray for us. Joseph most courageous, pray for us. Joseph most obedient, pray for us. Mirror of patience, pray for us. Lover of poverty, pray for us. Model of artisans, pray for us. Glory of home life, pray for us. Guardian of virgins, pray for us. Solace of the wretched, pray for us. Hope of the sick, pray for us. Patron of the dying, pray for us. Terror of demons, pray for us. Protector of Holy Church, pray for us.",
                latinText = "Sancte Ioseph, proles David inclyta, ora pro nobis. Lumen Patriarcharum, ora pro nobis. Dei Genetricis Sponse, ora pro nobis. Custos pudice Virginis, ora pro nobis. Altoris Filii Dei nutrix sedule, ora pro nobis. Sedulus defensor Christi, ora pro nobis. Almae Familiae praeses, ora pro nobis. Ioseph iustissime, ora pro nobis. Ioseph castissime, ora pro nobis. Ioseph prudentissime, ora pro nobis. Ioseph fortissime, ora pro nobis. Ioseph oboedientissime, ora pro nobis. Speculum patientiae, ora pro nobis. Amator paupertatis, ora pro nobis. Exemplar opificum, ora pro nobis. Domesticae vitae decus, ora pro nobis. Custos virginum, ora pro nobis. Solacium miserorum, ora pro nobis. Spes aegrotantium, ora pro nobis. Patronus morientium, ora pro nobis. Terror daemonum, ora pro nobis. Protector Sanctae Ecclesiae, ora pro nobis.",
                subtitle = "Patron of the Universal Church",
                libraryQuery = "Litany of St. Joseph"
            )
        )
    }

    // 6. Act of Contrition (Fasting days, Ember days, or Lent)
    if (info.isFastingDay || info.isEmberDay || info.season == com.example.data.calendar.LiturgicalSeason.LENT) {
        val cont = com.example.data.prayers.TraditionalPrayerLibrary.nightPrayers.find { it.id == "act_contrition" }
        if (cont != null) {
            list.add(
                RecommendedPrayer(
                    id = cont.id,
                    title = cont.title,
                    reason = "Deepen your daily penitence and fasting on this day",
                    category = cont.category,
                    englishText = cont.englishText,
                    latinText = cont.latinText,
                    subtitle = cont.subtitle,
                    libraryQuery = cont.title
                )
            )
        }
    }

    // 7. Devotion of the Sacred Heart of Jesus (Fridays)
    if (dayOfWeek == java.util.Calendar.FRIDAY) {
        val sh = com.example.data.prayers.TraditionalPrayerLibrary.devotionsSacredHeart.firstOrNull()
        if (sh != null) {
            list.add(
                RecommendedPrayer(
                    id = sh.id,
                    title = sh.title,
                    reason = "Friday reparation to the Sacred Heart of Jesus",
                    category = sh.category,
                    englishText = sh.englishText,
                    latinText = sh.latinText,
                    subtitle = sh.subtitle,
                    libraryQuery = sh.title
                )
            )
        }
    }

    // 8. Commemorated Saint's Intercessory Prayer (if available)
    if (saintProfile != null && saintProfile.intercessoryPrayer.isNotBlank()) {
        list.add(
            RecommendedPrayer(
                id = "saint_intercession_${saintProfile.saintId}",
                title = "Intercessory Collect to ${saintProfile.saintName}",
                reason = "Intercessory prayer to today's commemorated patron",
                category = "Saint Intercessions",
                englishText = saintProfile.intercessoryPrayer,
                latinText = "",
                subtitle = "Collect from today's liturgical commemoration",
                libraryQuery = saintProfile.saintName
            )
        )
    }

    return list
}

@Composable
fun LegendItem(color: Color, label: String) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val labelColor = if (isLightTheme) Color(0xFF1C1917) else ParchmentLight
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color, androidx.compose.foundation.shape.CircleShape))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = labelColor)
    }
}
