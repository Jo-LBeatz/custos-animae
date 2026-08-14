package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Parses "HH:mm" 24-hr string into 12-hr hour (1..12), minute (0..59), and isAm (Boolean).
 */
fun parseTimeTo12Hour(timeStr: String): Triple<Int, Int, Boolean> {
    val clean = timeStr.trim()
    val parts = clean.split(":")
    var h24 = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    h24 = h24.coerceIn(0, 23)
    val minute = m.coerceIn(0, 59)

    val isAm = h24 < 12
    var h12 = h24 % 12
    if (h12 == 0) h12 = 12

    return Triple(h12, minute, isAm)
}

/**
 * Converts 12-hr hour (1..12), minute (0..59), and isAm (Boolean) into "HH:mm" 24-hr string.
 */
fun format12HourTo24Hour(hour12: Int, minute: Int, isAm: Boolean): String {
    var h24 = hour12 % 12
    if (!isAm) {
        h24 += 12
    }
    return String.format("%02d:%02d", h24, minute)
}

/**
 * Interactive Scrollable Clock Component.
 * Displays an illuminated analog clock face with rotating hands,
 * paired with 3 scrollable wheel columns (Hour, Minute, AM/PM).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableClockPicker(
    initialTime: String,
    onTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (parsedH, parsedM, parsedAm) = remember(initialTime) { parseTimeTo12Hour(initialTime) }

    var selectedHour12 by remember(initialTime) { mutableStateOf(parsedH) }
    var selectedMinute by remember(initialTime) { mutableStateOf(parsedM) }
    var selectedIsAm by remember(initialTime) { mutableStateOf(parsedAm) }

    fun updateAndNotify(h: Int, m: Int, am: Boolean) {
        selectedHour12 = h
        selectedMinute = m
        selectedIsAm = am
        val time24 = format12HourTo24Hour(h, m, am)
        onTimeChanged(time24)
    }

    val displayTime24 = remember(selectedHour12, selectedMinute, selectedIsAm) {
        format12HourTo24Hour(selectedHour12, selectedMinute, selectedIsAm)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Digital Header Display
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCathedralSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldSacred),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Clock",
                        tint = GoldBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selected Time",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                        color = TextMutedParchment
                    )
                }

                Text(
                    text = String.format("%02d:%02d %s", selectedHour12, selectedMinute, if (selectedIsAm) "AM" else "PM") + " ($displayTime24)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
            }
        }

        // Analog Clock Canvas Face
        AnalogClockCanvas(
            hour12 = selectedHour12,
            minute = selectedMinute,
            isAm = selectedIsAm,
            modifier = Modifier
                .size(170.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Scrollable Wheel Columns Container
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCathedralSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCathedralCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Highlight Center Row Indicator Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldSacred.copy(alpha = 0.18f))
                        .border(1.dp, GoldSacred.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours Wheel (1 to 12)
                    ScrollableWheelColumn(
                        items = (1..12).map { String.format("%02d", it) },
                        selectedIndex = selectedHour12 - 1,
                        onItemSelected = { index ->
                            updateAndNotify(index + 1, selectedMinute, selectedIsAm)
                        },
                        label = "HOUR",
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // Minutes Wheel (00 to 59)
                    ScrollableWheelColumn(
                        items = (0..59).map { String.format("%02d", it) },
                        selectedIndex = selectedMinute,
                        onItemSelected = { index ->
                            updateAndNotify(selectedHour12, index, selectedIsAm)
                        },
                        label = "MIN",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // AM/PM Wheel
                    ScrollableWheelColumn(
                        items = listOf("AM", "PM"),
                        selectedIndex = if (selectedIsAm) 0 else 1,
                        onItemSelected = { index ->
                            updateAndNotify(selectedHour12, selectedMinute, index == 0)
                        },
                        label = "PERIOD",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Animated Analog Clock Face Canvas
 */
@Composable
fun AnalogClockCanvas(
    hour12: Int,
    minute: Int,
    isAm: Boolean,
    modifier: Modifier = Modifier
) {
    // Calculate rotation angles
    val hourAngleDegree = ((hour12 % 12) + (minute / 60f)) * 30f - 90f
    val minuteAngleDegree = (minute * 6f) - 90f

    val animatedHourAngle by animateFloatAsState(
        targetValue = hourAngleDegree,
        animationSpec = tween(300),
        label = "HourHandAngle"
    )

    val animatedMinuteAngle by animateFloatAsState(
        targetValue = minuteAngleDegree,
        animationSpec = tween(300),
        label = "MinuteHandAngle"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 8.dp.toPx()

        // Clock Outer Rim
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(DarkCathedralSurfaceVariant, DarkCathedralSurface),
                center = center,
                radius = radius
            ),
            center = center,
            radius = radius
        )

        drawCircle(
            color = GoldSacred,
            center = center,
            radius = radius,
            style = Stroke(width = 3.dp.toPx())
        )

        // Inner decorative ring
        drawCircle(
            color = GoldDark.copy(alpha = 0.4f),
            center = center,
            radius = radius * 0.85f,
            style = Stroke(width = 1.dp.toPx())
        )

        // Ticks for 12 hours
        for (i in 0 until 12) {
            val angleRad = (i * 30 - 90) * (PI / 180.0)
            val outerPoint = Offset(
                x = center.x + (radius - 6.dp.toPx()) * cos(angleRad).toFloat(),
                y = center.y + (radius - 6.dp.toPx()) * sin(angleRad).toFloat()
            )
            val innerPoint = Offset(
                x = center.x + (radius - 16.dp.toPx()) * cos(angleRad).toFloat(),
                y = center.y + (radius - 16.dp.toPx()) * sin(angleRad).toFloat()
            )
            val isMajor = i % 3 == 0
            drawLine(
                color = if (isMajor) GoldBright else TextMutedParchment,
                start = innerPoint,
                end = outerPoint,
                strokeWidth = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Hour Hand
        val hourHandLength = radius * 0.50f
        val hourRad = animatedHourAngle * (PI / 180.0)
        val hourHandEnd = Offset(
            x = center.x + hourHandLength * cos(hourRad).toFloat(),
            y = center.y + hourHandLength * sin(hourRad).toFloat()
        )
        drawLine(
            color = GoldBright,
            start = center,
            end = hourHandEnd,
            strokeWidth = 4.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Minute Hand
        val minuteHandLength = radius * 0.72f
        val minuteRad = animatedMinuteAngle * (PI / 180.0)
        val minuteHandEnd = Offset(
            x = center.x + minuteHandLength * cos(minuteRad).toFloat(),
            y = center.y + minuteHandLength * sin(minuteRad).toFloat()
        )
        drawLine(
            color = ParchmentLight,
            start = center,
            end = minuteHandEnd,
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center Pivot Dot
        drawCircle(
            color = GoldBright,
            center = center,
            radius = 5.dp.toPx()
        )
        drawCircle(
            color = DarkCathedralSurface,
            center = center,
            radius = 2.dp.toPx()
        )
    }
}

/**
 * Scrollable Wheel Column using LazyColumn & SnapFlingBehavior
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableWheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val visibleItemsCount = 3
    val itemHeight = 44.dp

    // We pad top and bottom with empty items so selected item rests exactly in center
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, items.size - 1))
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Synchronize scroll state with selected index changes
    LaunchedEffect(selectedIndex) {
        if (listState.firstVisibleItemIndex != selectedIndex && !listState.isScrollInProgress) {
            listState.animateScrollToItem(selectedIndex.coerceIn(0, items.size - 1))
        }
    }

    // Monitor center item as user scrolls
    val currentCenteredIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
        }
    }

    LaunchedEffect(currentCenteredIndex) {
        if (currentCenteredIndex != selectedIndex) {
            onItemSelected(currentCenteredIndex)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = GoldBright,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * visibleItemsCount)
        ) {
            items(items.size) { index ->
                val isSelected = index == currentCenteredIndex
                val text = items[index]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onItemSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) GoldBright else TextMutedParchment,
                        fontSize = if (isSelected) 20.sp else 15.sp,
                        modifier = Modifier
                            .scale(if (isSelected) 1.15f else 0.88f)
                            .alpha(if (isSelected) 1.0f else 0.45f)
                    )
                }
            }
        }
    }
}

/**
 * Scrollable Clock Dialog allowing users to choose a schedule time.
 */
@Composable
fun ScrollableClockDialog(
    initialTime: String,
    title: String = "Select Schedule Time",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentTime by remember { mutableStateOf(initialTime) }

    val presetTimes = listOf(
        "06:00" to "06:00 Morning",
        "08:00" to "08:00 Duties",
        "12:00" to "12:00 Angelus",
        "15:00" to "15:00 Mercy",
        "18:00" to "18:00 Evening",
        "21:00" to "21:00 Night"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCathedralSurface,
        titleContentColor = GoldBright,
        textContentColor = ParchmentLight,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scroll the hours, minutes, and period wheels or clock to set the precise time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ScrollableClockPicker(
                    initialTime = currentTime,
                    onTimeChanged = { newTime ->
                        currentTime = newTime
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Presets Title & Scrollable Chips
                Text(
                    text = "Monastic Hour Shortcuts:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedParchment,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTimes.take(3).forEach { (timeVal, label) ->
                        FilterChip(
                            selected = currentTime == timeVal,
                            onClick = { currentTime = timeVal },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSacred,
                                selectedLabelColor = DarkCathedralSurface,
                                containerColor = DarkCathedralSurfaceVariant,
                                labelColor = ParchmentLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTimes.drop(3).forEach { (timeVal, label) ->
                        FilterChip(
                            selected = currentTime == timeVal,
                            onClick = { currentTime = timeVal },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSacred,
                                selectedLabelColor = DarkCathedralSurface,
                                containerColor = DarkCathedralSurfaceVariant,
                                labelColor = ParchmentLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentTime) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred)
            ) {
                Text("Set Time ($currentTime)", color = DarkCathedralSurface, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        }
    )
}

/**
 * Clickable Input Field component that triggers the Scrollable Clock Dialog.
 */
@Composable
fun ScrollableClockField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Schedule Time (HH:mm)",
    dialogTitle: String = "Select Schedule Time",
    modifier: Modifier = Modifier
) {
    var showClockDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { showClockDialog = true },
                    modifier = Modifier.testTag("open_clock_picker_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Open Scrollable Clock",
                        tint = GoldBright
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldBright,
                unfocusedBorderColor = DarkCathedralCardBorder,
                focusedLabelColor = GoldBright
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Overlay transparent clickable zone so clicking anywhere on the field opens the scrollable clock dialog
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showClockDialog = true }
        )
    }

    if (showClockDialog) {
        ScrollableClockDialog(
            initialTime = value.ifBlank { "08:00" },
            title = dialogTitle,
            onConfirm = { selected ->
                onValueChange(selected)
                showClockDialog = false
            },
            onDismiss = { showClockDialog = false }
        )
    }
}
