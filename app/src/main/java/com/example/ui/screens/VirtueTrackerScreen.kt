package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RelapseLog
import com.example.ui.MainViewModel
import com.example.ui.theme.CrimsonBurgundy
import com.example.ui.theme.DarkCathedralBackground
import com.example.ui.theme.DarkCathedralSurface
import com.example.ui.theme.DarkCathedralSurfaceVariant
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldSacred
import com.example.ui.theme.ParchmentLight
import com.example.ui.theme.TextMutedParchment
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TrackLazyListScroll
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection

@Composable
fun VirtueTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val cleanSinceTimestamp by viewModel.virtueCleanSinceTimestamp.collectAsStateWithLifecycle()
    val longestStreakDays by viewModel.virtueLongestStreakDays.collectAsStateWithLifecycle()
    val relapseLogs by viewModel.virtueRelapseLogs.collectAsStateWithLifecycle()

    val currentDays = remember(cleanSinceTimestamp) {
        TimeUnit.MILLISECONDS.toDays(maxOf(0L, System.currentTimeMillis() - cleanSinceTimestamp))
    }
    val currentHours = remember(cleanSinceTimestamp) {
        TimeUnit.MILLISECONDS.toHours(maxOf(0L, System.currentTimeMillis() - cleanSinceTimestamp)) % 24
    }

    var showResetDialog by remember { mutableStateOf(false) }
    var showAdjustDateDialog by remember { mutableStateOf(false) }
    var expandedEmergencyPrayer by remember { mutableStateOf(false) }

    var showConfirmationModal by remember { mutableStateOf(false) }
    var pendingTriggerNote by remember { mutableStateOf("") }
    var pendingResolutionNote by remember { mutableStateOf("") }
    var isTransitioningReset by remember { mutableStateOf(false) }
    var transitionStep by remember { mutableIntStateOf(0) }

    // Reset exit transition effect sequence
    LaunchedEffect(isTransitioningReset) {
        if (isTransitioningReset) {
            transitionStep = 1
            delay(600L)
            viewModel.resetVirtueTimer(
                triggerNote = pendingTriggerNote,
                resolutionNote = pendingResolutionNote
            )
            transitionStep = 2
            delay(900L)
            isTransitioningReset = false
            transitionStep = 0
            pendingTriggerNote = ""
            pendingResolutionNote = ""
        }
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }
    val cleanSinceFormatted = remember(cleanSinceTimestamp) {
        dateFormat.format(Date(cleanSinceTimestamp))
    }

    val listState = rememberLazyListState()
    TrackLazyListScroll(listState = listState, viewModel = viewModel)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()
    val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
        // Header Banner
        item {
            AnimatedVisibility(
                visible = isTopHeaderVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("virtue_header_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield Icon",
                                tint = GoldBright,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chastity & Virtue Counter",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldBright
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track your days in the State of Grace and perseverance over sin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Main Live Timer Display
        item {
            LiveTimerCard(
                cleanSinceTimestamp = cleanSinceTimestamp,
                cleanSinceFormatted = cleanSinceFormatted
            )
        }

        // Actions Bar (Log Fall / Reset Timer & Adjust Start Date)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = ParchmentLight),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reset_timer_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I Relapsed / Reset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = { showAdjustDateDialog = true },
                    border = BorderStroke(1.dp, GoldSacred),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("adjust_date_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Adjust Start Date",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calibrate",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Stats & Milestones Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("virtue_stats_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Milestones & Personal Record",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox(
                            title = "Current Streak",
                            value = "$currentDays Days",
                            icon = Icons.Default.Shield,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        StatBox(
                            title = "Longest Record",
                            value = "$longestStreakDays Days",
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Virtue Badges Unlocked",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ParchmentLight
                        )
                    )

                    val milestones = listOf(
                        Triple(1, "1 Day", "Determination"),
                        Triple(3, "3 Days", "Triduum"),
                        Triple(7, "7 Days", "1 Week"),
                        Triple(14, "14 Days", "Fortnight"),
                        Triple(30, "30 Days", "1 Month"),
                        Triple(90, "90 Days", "Quarterly"),
                        Triple(365, "365 Days", "1 Year")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        milestones.forEach { (targetDays, label, title) ->
                            val isUnlocked = currentDays >= targetDays || longestStreakDays >= targetDays
                            MilestoneBadge(
                                targetDays = targetDays,
                                label = label,
                                title = title,
                                isUnlocked = isUnlocked
                            )
                        }
                    }
                }
            }
        }

        // Emergency Spiritual Warfare & Temptation Shield
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_prayers_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(1.dp, CrimsonBurgundy.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEmergencyPrayer = !expandedEmergencyPrayer },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Temptation Shield",
                                tint = CrimsonBurgundy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Emergency Temptation Shield",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ParchmentLight
                                    )
                                )
                                Text(
                                    text = "Prayers against impure thoughts & spiritual warfare",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedParchment
                                )
                            }
                        }
                        Icon(
                            imageVector = if (expandedEmergencyPrayer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = GoldBright
                        )
                    }

                    AnimatedVisibility(
                        visible = expandedEmergencyPrayer,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EmergencyPrayerItem(
                                title = "Prayer to St. Michael the Archangel",
                                prayerText = "St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen."
                            )

                            EmergencyPrayerItem(
                                title = "Anima Christi (Soul of Christ)",
                                prayerText = "Soul of Christ, sanctify me. Body of Christ, save me. Blood of Christ, inebriate me. Water from the side of Christ, wash me. Passion of Christ, strengthen me. O good Jesus, hear me. Within Thy wounds hide me. Suffer me not to be separated from Thee. From the malicious enemy defend me. In the hour of my death call me, and bid me come unto Thee, that with Thy saints I may praise Thee, forever and ever. Amen."
                            )

                            EmergencyPrayerItem(
                                title = "Jaculatory Ejaculations (Immediate Relief)",
                                prayerText = "• \"Jesus, Mary, Joseph, assist me in my last agony!\"\n• \"O Mary, conceived without sin, pray for us who have recourse to thee!\"\n• \"Precious Blood of Jesus, wash and deliver me!\"\n• \"Lord Jesus Christ, Son of God, have mercy on me, a sinner!\""
                            )
                        }
                    }
                }
            }
        }

        // Relapse & Victory History Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "History & Reflection Log",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    )
                }
                Text(
                    text = "${relapseLogs.size} recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }
        }

        if (relapseLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface.copy(alpha = 0.6f)),
                    border = BorderStroke(0.5.dp, GoldSacred.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No falls logged. Stay vigilant, faithful, and focused on Heaven!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedParchment,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(relapseLogs, key = { it.id }) { log ->
                RelapseLogItemCard(
                    log = log,
                    onDelete = { viewModel.deleteRelapseLog(log.id) }
                )
            }
        }
    } // End LazyColumn

    // Dialog 1: Log Fall / Reflection Dialog
    if (showResetDialog) {
        LogFallDialog(
            onDismiss = { showResetDialog = false },
            onProceedToConfirm = { trigger, resolution ->
                pendingTriggerNote = trigger
                pendingResolutionNote = resolution
                showResetDialog = false
                showConfirmationModal = true
            }
        )
    }

    // Dialog 2: Subtle Confirmation Modal
    if (showConfirmationModal) {
        ResetConfirmationModal(
            daysCount = currentDays,
            hoursCount = currentHours,
            onDismiss = { showConfirmationModal = false },
            onConfirmReset = {
                showConfirmationModal = false
                isTransitioningReset = true
            }
        )
    }

    // Dialog 3: Adjust / Calibrate Start Date
    if (showAdjustDateDialog) {
        AdjustStartDateDialog(
            currentTimestamp = cleanSinceTimestamp,
            onDismiss = { showAdjustDateDialog = false },
            onConfirm = { newTimestamp ->
                viewModel.updateVirtueStartTimestamp(newTimestamp)
                showAdjustDateDialog = false
            }
        )
    }

    // Satisfying Exit Transition Overlay
    AnimatedVisibility(
        visible = isTransitioningReset,
        enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300)),
        exit = fadeOut(tween(400)) + scaleOut(targetScale = 1.15f, animationSpec = tween(400)) + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = Modifier.fillMaxSize()
    ) {
        ResetExitTransitionOverlay(transitionStep = transitionStep)
    }
    } // End Box
} // End VirtueTrackerScreen Composable

@Composable
fun LiveTimerCard(
    cleanSinceTimestamp: Long,
    cleanSinceFormatted: String
) {
    var tickerTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(cleanSinceTimestamp) {
        while (true) {
            tickerTime = System.currentTimeMillis()
            delay(1000L)
        }
    }
    val elapsedMs = maxOf(0L, tickerTime - cleanSinceTimestamp)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMs)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMs) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMs) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMs) % 60

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("virtue_timer_card"),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.5.dp, GoldBright)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = CrimsonBurgundy.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, GoldBright),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$days",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright
                            )
                        )
                        Text(
                            text = if (days == 1L) "DAY" else "DAYS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ParchmentLight
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VICTORY OVER TEMPTATION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = GoldSacred
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Detailed Time Breakdown Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimerTimeUnitBox(label = "HOURS", value = String.format("%02d", hours))
                TimerTimeUnitBox(label = "MINS", value = String.format("%02d", minutes))
                TimerTimeUnitBox(label = "SECS", value = String.format("%02d", seconds))
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = GoldSacred.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = "Start date",
                    tint = TextMutedParchment,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Clean Since: $cleanSinceFormatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }
        }
    }
}

@Composable
private fun TimerTimeUnitBox(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = DarkCathedralSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.3f)),
            modifier = Modifier.width(64.dp).height(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ParchmentLight
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMutedParchment
            )
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DarkCathedralSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, GoldSacred.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GoldBright,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ParchmentLight
                    )
                )
            }
        }
    }
}

@Composable
private fun MilestoneBadge(
    targetDays: Int,
    label: String,
    title: String,
    isUnlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = if (isUnlocked) CrimsonBurgundy.copy(alpha = 0.4f) else DarkCathedralSurfaceVariant,
            border = BorderStroke(
                width = if (isUnlocked) 1.5.dp else 0.5.dp,
                color = if (isUnlocked) GoldBright else TextMutedParchment.copy(alpha = 0.3f)
            ),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "$targetDays",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMutedParchment
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = if (isUnlocked) GoldBright else TextMutedParchment
            )
        )
    }
}

@Composable
private fun EmergencyPrayerItem(
    title: String,
    prayerText: String
) {
    Surface(
        color = DarkCathedralSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, GoldSacred.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = prayerText,
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 18.sp
                ),
                color = ParchmentLight
            )
        }
    }
}

@Composable
private fun RelapseLogItemCard(
    log: RelapseLog,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(0.5.dp, CrimsonBurgundy.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Fall logged",
                        tint = CrimsonBurgundy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Achieved ${log.streakDays}d ${log.streakHours}h before fall",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ParchmentLight
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete log",
                        tint = TextMutedParchment,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedParchment
            )

            if (log.triggerNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Trigger/Occasion: ${log.triggerNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )
            }

            if (log.resolutionNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Resolution: ${log.resolutionNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldBright
                )
            }
        }
    }
}

@Composable
private fun LogFallDialog(
    onDismiss: () -> Unit,
    onProceedToConfirm: (trigger: String, resolution: String) -> Unit
) {
    var triggerNote by remember { mutableStateOf("") }
    var resolutionNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield",
                    tint = CrimsonBurgundy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Timer & Renew Resolve",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = CrimsonBurgundy.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, CrimsonBurgundy)
                ) {
                    Text(
                        text = "“Do not lose courage in considering your imperfections, but instantly set about remedying them — every day begin the task anew.” — St. Francis de Sales",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentLight,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = triggerNote,
                    onValueChange = { triggerNote = it },
                    label = { Text("What led to this temptation / fall?", color = TextMutedParchment) },
                    placeholder = { Text("e.g., Late night phone scrolling, tiredness, anger", color = TextMutedParchment) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GoldSacred,
                        focusedTextColor = ParchmentLight,
                        unfocusedTextColor = ParchmentLight
                    ),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = resolutionNote,
                    onValueChange = { resolutionNote = it },
                    label = { Text("Concrete Action / Resolution for Next Time", color = TextMutedParchment) },
                    placeholder = { Text("e.g., Put phone away at 9 PM, pray 1 Rosary decade", color = TextMutedParchment) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GoldSacred,
                        focusedTextColor = ParchmentLight,
                        unfocusedTextColor = ParchmentLight
                    ),
                    maxLines = 2
                )

                Text(
                    text = "Act of Contrition Prompt:\n\"O my God, I am heartily sorry for having offended Thee...\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedParchment
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onProceedToConfirm(triggerNote, resolutionNote) },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                modifier = Modifier.testTag("log_fall_proceed_button")
            ) {
                Text("Proceed to Confirmation", color = ParchmentLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ResetConfirmationModal(
    daysCount: Long,
    hoursCount: Long,
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit
) {
    var acknowledged by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = CrimsonBurgundy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acknowledge & Confirm Reset",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CrimsonBurgundy.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, CrimsonBurgundy.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Are you sure you want to reset your virtue counter?",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ParchmentLight
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your current streak of $daysCount days and $hoursCount hours will be ended and archived in your reflection log. Your counter will reset to Day 0.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { acknowledged = !acknowledged }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = acknowledged,
                        onCheckedChange = { acknowledged = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldBright,
                            uncheckedColor = GoldSacred
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I acknowledge this reset and resolve to begin anew in the State of Grace.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = ParchmentLight
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmReset,
                enabled = acknowledged,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonBurgundy,
                    disabledContainerColor = CrimsonBurgundy.copy(alpha = 0.3f)
                ),
                modifier = Modifier.testTag("confirm_reset_final_button")
            ) {
                Text("Yes, Reset Counter to 0", color = if (acknowledged) ParchmentLight else TextMutedParchment)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_reset_button")
            ) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("reset_confirmation_modal")
    )
}

@Composable
private fun ResetExitTransitionOverlay(
    transitionStep: Int
) {
    val scale by animateFloatAsState(
        targetValue = if (transitionStep == 1) 1.15f else 1.0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "overlayScale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkCathedralBackground.copy(alpha = 0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .testTag("reset_exit_transition_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(2.dp, if (transitionStep == 2) GoldBright else CrimsonBurgundy)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (transitionStep == 2) GoldBright.copy(alpha = 0.2f) else CrimsonBurgundy.copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, if (transitionStep == 2) GoldBright else CrimsonBurgundy),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (transitionStep == 2) Icons.Default.CheckCircle else Icons.Default.Shield,
                                contentDescription = "Reset status",
                                tint = if (transitionStep == 2) GoldBright else CrimsonBurgundy,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (transitionStep == 2) "STATE OF GRACE RESTORED" else "RESETTING TIMER...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = GoldBright
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (transitionStep == 2)
                            "Your counter is set to Day 0. 'Be faithful unto death, and I will give thee a crown of life.' — Rev 2:10"
                        else
                            "Archiving your streak and renewing your resolution...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMutedParchment,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GoldBright,
                        trackColor = DarkCathedralSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AdjustStartDateDialog(
    currentTimestamp: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var daysAgoInput by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Calibrate Start Date",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter how many days you have already been clean before today to back-date your timer accurately:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )

                OutlinedTextField(
                    value = daysAgoInput,
                    onValueChange = { daysAgoInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Days Already Clean", color = TextMutedParchment) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GoldSacred,
                        focusedTextColor = ParchmentLight,
                        unfocusedTextColor = ParchmentLight
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val now = System.currentTimeMillis()
                        onConfirm(now)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurfaceVariant),
                    border = BorderStroke(0.5.dp, GoldSacred),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Start Time to EXACTLY NOW", color = GoldBright)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val daysAgo = daysAgoInput.toLongOrNull() ?: 0L
                    val newStart = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysAgo)
                    onConfirm(newStart)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred)
            ) {
                Text("Set Timer", color = DarkCathedralBackground)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(12.dp)
    )
}
