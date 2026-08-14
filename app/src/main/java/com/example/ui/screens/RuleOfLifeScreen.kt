package com.example.ui.screens

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.ui.components.ScrollableClockField
import com.example.ui.components.ScrollableClockPicker
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SpiritualDuty
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper

import com.example.ui.components.TrackLazyListScroll
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun RuleOfLifeScreen(
    viewModel: MainViewModel,
    duties: List<SpiritualDuty>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val exportTextLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val success = viewModel.exportRuleOfLifeHistoryToText(outputStream)
                        if (success) {
                            Toast.makeText(context, "Rule of Life report exported successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to write report.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Could not open storage location.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Export Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    var selectedFrequency by remember { mutableStateOf("DAILY") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingDuty by remember { mutableStateOf<SpiritualDuty?>(null) }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val todayInfo = viewModel.getTodayLiturgicalInfo()
    val filteredDuties = duties.filter { it.frequency == selectedFrequency }

    val listState = remember(selectedFrequency) { androidx.compose.foundation.lazy.LazyListState() }

    LaunchedEffect(selectedFrequency) {
        viewModel.setReadingProgress(0f)
        viewModel.setBottomNavVisible(true)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    TrackLazyListScroll(listState = listState, viewModel = viewModel)

    val pendingGuardDuty by viewModel.pendingReflectionDuty.collectAsStateWithLifecycle()

    // Dialog for Reflection Guard
    pendingGuardDuty?.let { pending ->
        ReflectionGuardDialog(
            duty = pending,
            onConfirmKeep = { viewModel.confirmPendingDuty() },
            onRevise = { viewModel.dismissPendingDuty() },
            onDismiss = { viewModel.dismissPendingDuty() }
        )
    }

    // Add / Edit Duty Dialog
    if (showAddDialog || editingDuty != null) {
        DutyEditDialog(
            duty = editingDuty,
            initialFrequency = selectedFrequency,
            onSave = { newDuty ->
                viewModel.checkAndSaveDuty(newDuty)
                showAddDialog = false
                editingDuty = null
            },
            onDismiss = {
                showAddDialog = false
                editingDuty = null
            }
        )
    }


    val isBottomNavVisible by viewModel.isBottomNavVisible.collectAsStateWithLifecycle()
    val pageViewMode by viewModel.pageViewMode.collectAsStateWithLifecycle()

    val dynamicFabBottomPadding by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isBottomNavVisible) 80.dp else 16.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "fab_bottom_padding"
    )

    Scaffold(
        floatingActionButton = {
            val dynamicActionLabel = when (selectedFrequency) {
                "DAILY" -> "Add Daily Rule"
                "WEEKLY" -> "Add Weekly Rule"
                "MONTHLY" -> "Add Monthly Rule"
                "YEARLY" -> "Add Yearly Rule"
                else -> "Add ${selectedFrequency.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} Rule"
            }
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = dynamicActionLabel
                    )
                },
                text = {
                    androidx.compose.animation.AnimatedContent(
                        targetState = dynamicActionLabel,
                        transitionSpec = {
                            (androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it / 2 })
                                .togetherWith(androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it / 2 })
                        },
                        label = "fab_text_anim"
                    ) { labelText ->
                        Text(
                            text = labelText,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                containerColor = GoldSacred,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = dynamicFabBottomPadding)
                    .testTag("add_duty_fab")
            )
        },
        containerColor = DarkCathedralBackground
    ) { padding ->
        val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 640.dp)
                .padding(horizontal = 16.dp)
                .fullScreenToggleOnTap(viewModel)
                .nestedScroll(directionalScrollConnection)
        ) {
            val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()

            androidx.compose.animation.AnimatedVisibility(
                visible = isTopHeaderVisible,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
            ) {
                Column {
                    GothicHeader(
                        title = "RULE OF LIFE",
                        subtitle = "Daily, Weekly, Monthly & Yearly Spiritual Duties",
                        badgeText = "${filteredDuties.count { it.isCompleted }}/${filteredDuties.size} Completed",
                        trailingContent = {
                            Button(
                                onClick = {
                                    val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                                    exportTextLauncher.launch("rule_of_life_report_$dateStr.txt")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkCathedralSurface,
                                    contentColor = GoldBright
                                ),
                                border = BorderStroke(1.dp, GoldSacred),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("export_report_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export Report",
                                        tint = GoldBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Export",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    )

                    // Liturgical Banner
                    LiturgicalBanner(info = todayInfo, appLanguage = appLanguage)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Frequency Filter Tabs
                    TabRow(
                        selectedTabIndex = when (selectedFrequency) {
                            "DAILY" -> 0
                            "WEEKLY" -> 1
                            "MONTHLY" -> 2
                            else -> 3
                        },
                        containerColor = DarkCathedralSurface,
                        contentColor = GoldBright,
                        indicator = {}
                    ) {
                        listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY").forEachIndexed { index, freq ->
                            val isSelected = selectedFrequency == freq
                            Tab(
                                selected = isSelected,
                                onClick = { selectedFrequency = freq },
                                text = {
                                    com.example.ui.components.ResponsiveText(
                                        text = freq,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) GoldBright else TextMutedParchment,
                                        maxLines = 2,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }


            Spacer(modifier = Modifier.height(10.dp))
            
            // Duties List
            if (filteredDuties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "☩ No spiritual duties under $selectedFrequency rule",
                            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif),
                            color = TextMutedParchment
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set a holy routine for your ${selectedFrequency.lowercase(Locale.getDefault())} rule of life.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldSacred,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("empty_state_add_duty_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (selectedFrequency) {
                                    "DAILY" -> "Add Daily Rule"
                                    "WEEKLY" -> "Add Weekly Rule"
                                    "MONTHLY" -> "Add Monthly Rule"
                                    "YEARLY" -> "Add Yearly Rule"
                                    else -> "Add Rule"
                                },
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = if (isBottomNavVisible) 160.dp else 100.dp)
                ) {
                    items(filteredDuties, key = { it.id }) { duty ->
                        SpiritualDutyCard(
                            duty = duty,
                            onToggleComplete = { viewModel.toggleDutyCompletion(duty) },
                            onEdit = { editingDuty = duty },
                            onDelete = { viewModel.deleteDuty(duty.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DutyEditDialog(
    duty: SpiritualDuty?,
    initialFrequency: String,
    onSave: (SpiritualDuty) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(duty?.title ?: "") }
    var description by remember { mutableStateOf(duty?.description ?: "") }
    var frequency by remember { mutableStateOf(duty?.frequency ?: initialFrequency) }
    var reminderTime by remember { mutableStateOf(duty?.reminderTime ?: "08:00") }
    var priority by remember { mutableStateOf(duty?.priority ?: "SACRED") }
    var category by remember { mutableStateOf(duty?.category ?: "PRAYER") }

    var monthlyMode by remember { mutableStateOf("DATES") }
    var yearlyMode by remember { mutableStateOf("DATES") }

    var scheduledDaysOfWeekList by remember {
        mutableStateOf(
            duty?.scheduledDaysOfWeek?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }
    var scheduledDaysOfMonthList by remember {
        mutableStateOf(
            duty?.scheduledDaysOfMonth?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }
    var scheduledDaysOfYearList by remember {
        mutableStateOf(
            duty?.scheduledDaysOfYear?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var showMonthlyDatePicker by remember { mutableStateOf(false) }
    var showYearlyDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            val dialogTitle = if (duty == null) {
                when (frequency) {
                    "DAILY" -> "New Daily Rule"
                    "WEEKLY" -> "New Weekly Rule"
                    "MONTHLY" -> "New Monthly Rule"
                    "YEARLY" -> "New Yearly Rule"
                    else -> "New Spiritual Rule"
                }
            } else {
                "Edit Spiritual Rule"
            }
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldBright
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Duty Title (e.g., Pray Rosary)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("duty_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Personal Intentions") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScrollableClockField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = "Time (HH:mm)",
                        dialogTitle = "Select Duty Schedule Time",
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it.uppercase() },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "Priority Level:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SACRED", "MEDIUM").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSacred,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = DarkCathedralCardBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Frequency Rule:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY").forEach { f ->
                        FilterChip(
                            selected = frequency == f,
                            onClick = { frequency = f },
                            label = { com.example.ui.components.ResponsiveText(f, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldBright,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E1E28)
                            )
                        )
                    }
                }

                // Conditional scheduling options
                when (frequency) {
                    "WEEKLY" -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Select Scheduled Day of Week (Single Day):",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                        val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            weekDays.chunked(2).forEach { rowDays ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    rowDays.forEach { day ->
                                        val isSelected = scheduledDaysOfWeekList.contains(day)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                scheduledDaysOfWeekList = listOf(day)
                                            },
                                            label = { com.example.ui.components.ResponsiveText(day, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = GoldSacred,
                                                selectedLabelColor = Color.Black,
                                                containerColor = Color(0xFF1E1E28)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "MONTHLY" -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "How would you like to schedule Monthly?",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            FilterChip(
                                selected = monthlyMode == "DATES",
                                onClick = { monthlyMode = "DATES" },
                                label = { com.example.ui.components.ResponsiveText("Specific Date(s)", fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0xFF1E1E28)
                                )
                            )
                            FilterChip(
                                selected = monthlyMode == "RELATIVE",
                                onClick = { monthlyMode = "RELATIVE" },
                                label = { com.example.ui.components.ResponsiveText("Relative Day(s)", fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0xFF1E1E28)
                                )
                            )
                        }

                        if (monthlyMode == "DATES") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f)), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF161622))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "SPECIFIC DAYS OF THE MONTH",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = GoldBright
                                            )
                                            Text(
                                                text = "Tap 📅 to select calendar days to add",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = TextMutedParchment.copy(alpha = 0.7f)
                                            )
                                        }
                                        IconButton(
                                            onClick = { showMonthlyDatePicker = true },
                                            modifier = Modifier
                                                .background(CrimsonBurgundy, RoundedCornerShape(8.dp))
                                                .size(36.dp)
                                        ) {
                                            Text("📅", fontSize = 16.sp, color = GoldBright)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (scheduledDaysOfMonthList.isEmpty()) {
                                        Text(
                                            text = "No days selected yet.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                            color = TextMutedParchment,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            scheduledDaysOfMonthList.sortedBy { it.toIntOrNull() ?: 0 }.forEach { day ->
                                                Box(
                                                    modifier = Modifier
                                                        .background(CrimsonBurgundy.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                                        .border(1.dp, GoldSacred.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                                        .clickable {
                                                            scheduledDaysOfMonthList = scheduledDaysOfMonthList - day
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "Day $day",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = GoldBright
                                                        )
                                                        Text(
                                                            text = "✕",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = CrimsonLight
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Schedule by Relative Days (e.g., First Monday):",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight
                            )

                            var monthlyOrdinal by remember { mutableStateOf("1ST") }
                            var monthlyDayOfWeek by remember { mutableStateOf("Monday") }

                            val ordinals = listOf("1ST" to "First", "2ND" to "Second", "3RD" to "Third", "4TH" to "Fourth", "LST" to "Last")
                            val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

                            Text("Occurrence:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                items(ordinals) { (code, label) ->
                                    FilterChip(
                                        selected = monthlyOrdinal == code,
                                        onClick = { monthlyOrdinal = code },
                                        label = { com.example.ui.components.ResponsiveText(label, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldSacred,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF1E1E28)
                                        )
                                    )
                                }
                            }

                            Text("Day of Week:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                items(weekDays) { day ->
                                    FilterChip(
                                        selected = monthlyDayOfWeek == day,
                                        onClick = { monthlyDayOfWeek = day },
                                        label = { com.example.ui.components.ResponsiveText(day, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldSacred,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF1E1E28)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    val relRule = "${monthlyOrdinal}_${monthlyDayOfWeek}"
                                    scheduledDaysOfMonthList = listOf(relRule)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).align(Alignment.End)
                            ) {
                                Text("Set Relative Day", fontSize = 10.sp, color = GoldBright)
                            }
                        }

                        val relativeDays = scheduledDaysOfMonthList.filter { it.contains("_") }
                        if (relativeDays.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Selected Relative Days:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                items(relativeDays) { relRule ->
                                    val parts = relRule.split("_")
                                    val ordinalsList = listOf("1ST" to "First", "2ND" to "Second", "3RD" to "Third", "4TH" to "Fourth", "LST" to "Last")
                                    val ordinalLabel = ordinalsList.find { it.first == parts.getOrNull(0) }?.second ?: parts.getOrNull(0)
                                    val dayLabel = parts.getOrNull(1) ?: ""
                                    InputChip(
                                        selected = true,
                                        onClick = {},
                                        label = { Text("$ordinalLabel $dayLabel", fontSize = 10.sp, color = GoldBright) },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { scheduledDaysOfMonthList = scheduledDaysOfMonthList - relRule },
                                                modifier = Modifier.size(16.dp)
                                            ) {
                                                Text("×", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    "YEARLY" -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "How would you like to schedule Yearly?",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            FilterChip(
                                selected = yearlyMode == "DATES",
                                onClick = { yearlyMode = "DATES" },
                                label = { com.example.ui.components.ResponsiveText("Specific Date", fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0xFF1E1E28)
                                )
                            )
                            FilterChip(
                                selected = yearlyMode == "RELATIVE",
                                onClick = { yearlyMode = "RELATIVE" },
                                label = { com.example.ui.components.ResponsiveText("Relative Day of Month", fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0xFF1E1E28)
                                )
                            )
                        }

                        if (yearlyMode == "DATES") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f)), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF161622))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "ANNUAL FESTIVAL DATE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = GoldBright
                                            )
                                            Text(
                                                text = "Tap 📅 to choose the annual date",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = TextMutedParchment.copy(alpha = 0.7f)
                                            )
                                        }
                                        IconButton(
                                            onClick = { showYearlyDatePicker = true },
                                            modifier = Modifier
                                                .background(CrimsonBurgundy, RoundedCornerShape(8.dp))
                                                .size(36.dp)
                                        ) {
                                            Text("📅", fontSize = 16.sp, color = GoldBright)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val currentYearlyDate = scheduledDaysOfYearList.firstOrNull { !it.contains("_") }
                                    if (currentYearlyDate == null) {
                                        Text(
                                            text = "No date selected yet.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                            color = TextMutedParchment,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        val parts = currentYearlyDate.split("-")
                                        val monthNames = listOf(
                                            "January", "February", "March", "April", "May", "June",
                                            "July", "August", "September", "October", "November", "December"
                                        )
                                        val monthVal = parts.getOrNull(0)?.toIntOrNull() ?: 1
                                        val dayVal = parts.getOrNull(1)?.toIntOrNull() ?: 1
                                        val formattedDate = "${monthNames.getOrNull(monthVal - 1) ?: ""} $dayVal"

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .background(CrimsonBurgundy.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .border(1.dp, GoldSacred.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                                .fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "🔔",
                                                fontSize = 14.sp
                                            )
                                            Column {
                                                Text(
                                                    text = formattedDate.uppercase(),
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Serif
                                                    ),
                                                    color = GoldBright
                                                )
                                                Text(
                                                    text = "Recurs annually on this date",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMutedParchment,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Schedule by Relative Day of Month of the Year:",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight
                            )

                            var yearlyOrdinal by remember { mutableStateOf("1ST") }
                            var yearlyDayOfWeek by remember { mutableStateOf("Monday") }
                            var yearlyMonth by remember { mutableStateOf(1) }

                            val ordinals = listOf("1ST" to "First", "2ND" to "Second", "3RD" to "Third", "4TH" to "Fourth", "LST" to "Last")
                            val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

                            Text("Occurrence:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                items(ordinals) { (code, label) ->
                                    FilterChip(
                                        selected = yearlyOrdinal == code,
                                        onClick = { yearlyOrdinal = code },
                                        label = { com.example.ui.components.ResponsiveText(label, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldSacred,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF1E1E28)
                                        )
                                    )
                                }
                            }

                            Text("Day of Week:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                items(weekDays) { day ->
                                    FilterChip(
                                        selected = yearlyDayOfWeek == day,
                                        onClick = { yearlyDayOfWeek = day },
                                        label = { com.example.ui.components.ResponsiveText(day, fontSize = 10.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldSacred,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF1E1E28)
                                        )
                                    )
                                }
                            }

                            Text("Month of Year:", fontSize = 10.sp, color = TextMutedParchment)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(
                                        onClick = { if (yearlyMonth > 1) yearlyMonth-- },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("-", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Text(
                                        text = monthNames.getOrNull(yearlyMonth - 1) ?: yearlyMonth.toString(),
                                        color = ParchmentLight,
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = { if (yearlyMonth < 12) yearlyMonth++ },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("+", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        val relYearlyRule = "${yearlyOrdinal}_${yearlyDayOfWeek}_${yearlyMonth}"
                                        scheduledDaysOfYearList = listOf(relYearlyRule)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Set Relative Day", fontSize = 10.sp, color = GoldBright)
                                }
                            }
                        }

                        if (scheduledDaysOfYearList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Selected Yearly Recurrences:", fontSize = 10.sp, color = TextMutedParchment)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                items(scheduledDaysOfYearList) { dateStr ->
                                    val ordinals = listOf("1ST" to "First", "2ND" to "Second", "3RD" to "Third", "4TH" to "Fourth", "LST" to "Last")
                                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                                    val formattedDate = if (dateStr.contains("_")) {
                                        val parts = dateStr.split("_")
                                        val ord = ordinals.find { it.first == parts.getOrNull(0) }?.second ?: parts.getOrNull(0)
                                        val dow = parts.getOrNull(1) ?: ""
                                        val mNum = parts.getOrNull(2)?.toIntOrNull() ?: 1
                                        val mName = monthNames.getOrNull(mNum - 1) ?: parts.getOrNull(2)
                                        "$ord $dow of $mName"
                                    } else {
                                        val parts = dateStr.split("-")
                                        if (parts.size == 2) {
                                            val mIdx = parts[0].toIntOrNull()?.minus(1) ?: 0
                                            val day = parts[1]
                                            "${monthNames.getOrNull(mIdx) ?: parts[0]} $day"
                                        } else dateStr
                                    }

                                    InputChip(
                                        selected = true,
                                        onClick = {},
                                        label = { Text(formattedDate, fontSize = 10.sp, color = GoldBright) },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { scheduledDaysOfYearList = scheduledDaysOfYearList - dateStr },
                                                modifier = Modifier.size(16.dp)
                                            ) {
                                                Text("×", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            val saveButtonText = if (duty == null) {
                when (frequency) {
                    "DAILY" -> "Save Daily Rule"
                    "WEEKLY" -> "Save Weekly Rule"
                    "MONTHLY" -> "Save Monthly Rule"
                    "YEARLY" -> "Save Yearly Rule"
                    else -> "Save Rule"
                }
            } else {
                "Save Changes"
            }
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val updated = (duty ?: SpiritualDuty(title = title)).copy(
                            title = title.trim(),
                            description = description.trim(),
                            frequency = frequency,
                            reminderTime = reminderTime.ifBlank { "08:00" },
                            priority = priority,
                            category = category.ifBlank { "PRAYER" },
                            scheduledDaysOfWeek = if (frequency == "WEEKLY") scheduledDaysOfWeekList.joinToString(",") else "",
                            scheduledDaysOfMonth = if (frequency == "MONTHLY") scheduledDaysOfMonthList.joinToString(",") else "",
                            scheduledDaysOfYear = if (frequency == "YEARLY") scheduledDaysOfYearList.joinToString(",") else ""
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                modifier = Modifier.testTag("save_duty_button")
            ) {
                Text(saveButtonText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(16.dp)
    )

    if (showMonthlyDatePicker) {
        GothicM3DatePickerDialog(
            onDateSelected = { selectedMillis ->
                selectedMillis?.let {
                    val calendar = java.util.Calendar.getInstance().apply {
                        timeInMillis = it
                    }
                    val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString()
                    if (!scheduledDaysOfMonthList.contains(dayOfMonth)) {
                        scheduledDaysOfMonthList = scheduledDaysOfMonthList + dayOfMonth
                    }
                }
            },
            onDismiss = { showMonthlyDatePicker = false }
        )
    }

    if (showYearlyDatePicker) {
        GothicM3DatePickerDialog(
            onDateSelected = { selectedMillis ->
                selectedMillis?.let {
                    val calendar = java.util.Calendar.getInstance().apply {
                        timeInMillis = it
                    }
                    val month = (calendar.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
                    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    scheduledDaysOfYearList = listOf("$month-$day")
                }
            },
            onDismiss = { showYearlyDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GothicM3DatePickerDialog(
    initialSelectedDateMillis: Long? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis ?: System.currentTimeMillis()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("CONFIRM", color = GoldBright, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextMutedParchment)
            }
        },
        colors = DatePickerDefaults.colors().copy(
            containerColor = DarkCathedralSurface
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = DarkCathedralSurface,
                titleContentColor = GoldBright,
                headlineContentColor = GoldBright,
                selectedDayContainerColor = CrimsonBurgundy,
                selectedDayContentColor = ParchmentLight,
                todayContentColor = GoldBright,
                todayDateBorderColor = GoldBright,
                dayContentColor = TextMutedParchment,
                weekdayContentColor = GoldLight,
                navigationContentColor = GoldBright
            )
        )
    }
}


