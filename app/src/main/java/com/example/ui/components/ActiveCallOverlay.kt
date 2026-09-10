package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.NotificationScheduler
import com.example.notifications.RescheduleManager
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun formatSpokenTextWithBreaks(title: String, message: String): String {
    val cleanTitle = title.trim().trimEnd('.', ':', ';', '!', '?', ',')
    val cleanMessage = message.trim()
    return if (cleanMessage.isNotBlank()) {
        "$cleanTitle. ... $cleanMessage"
    } else {
        cleanTitle
    }
}

@Composable
fun ActiveCallOverlay(
    title: String,
    message: String,
    notificationId: Int = 1001,
    viewModel: MainViewModel,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var showPostponeDialog by remember { mutableStateOf(false) }
    val isLatinMode by viewModel.isLatinMode.collectAsStateWithLifecycle()
    val playbackState by viewModel.latinAudioState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activePrayerMode = com.example.util.PrayerMode.fromCode(userProfile?.prayerMode)
    val formattedMessage = if (!isLatinMode) com.example.util.PrayerPerspectiveFormatter.format(message, activePrayerMode) else message

    fun previewTime(minutesToAdd: Int): String {
        val cal = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.MINUTE, minutesToAdd)
        }
        return java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(cal.time)
    }

    fun formatted24h(minutesToAdd: Int): String {
        val cal = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.MINUTE, minutesToAdd)
        }
        return String.format(java.util.Locale.US, "%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    }

    fun applyReschedule(selectedTime: String) {
        viewModel.stopLatinAudio()
        showPostponeDialog = false
        val dutyId = if (notificationId >= 1000) notificationId - 1000 else -1
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val db = com.example.data.AppDatabase.getDatabase(context)
                val duty = if (dutyId > 0) db.appDao().getDutyById(dutyId) else null
                if (duty != null) {
                    val today = RescheduleManager.getTodayDateString()
                    RescheduleManager.setReschedule(context, dutyId, today, selectedTime)
                    val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as? android.app.AlarmManager
                    if (alarmManager != null) {
                        NotificationScheduler.scheduleDutyNotification(context, alarmManager, duty)
                    }
                } else {
                    NotificationScheduler.scheduleOneOffReminder(
                        context,
                        title,
                        message,
                        selectedTime,
                        notificationId
                    )
                }

                com.example.data.CallLogRepository.getInstance(context)
                    .logCall(title, "Rescheduled to $selectedTime today: $message", "RESCHEDULED")

                launch(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "Rescheduled '$title' to $selectedTime for today.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    onEndCall()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { onEndCall() }
            }
        }
    }

    fun applyCompleteDuty() {
        viewModel.stopLatinAudio()
        val dutyId = if (notificationId >= 1000) notificationId - 1000 else -1
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val db = com.example.data.AppDatabase.getDatabase(context)
                val duty = if (dutyId > 0) db.appDao().getDutyById(dutyId) else null
                if (duty != null) {
                    val repo = com.example.data.SpiritualRepository(db.appDao())
                    if (!duty.isCompleted) {
                        repo.toggleDutyCompletion(duty)
                    }
                    NotificationScheduler.cancelDutyNotification(context, dutyId)
                }

                com.example.data.CallLogRepository.getInstance(context)
                    .logCall(title, "Completed during call: $message", "ANSWERED")

                launch(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "Marked '$title' completed! Deo Gratias.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    onEndCall()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { onEndCall() }
            }
        }
    }

    // Call duration timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    // Auto-play speech audio for prayer/duty message when call connects
    LaunchedEffect(title, formattedMessage, isLatinMode) {
        if (!isMuted) {
            val spokenText = formatSpokenTextWithBreaks(title, formattedMessage)
            viewModel.playLatinAudio(
                id = "call_${title.hashCode()}",
                text = spokenText,
                isLatin = isLatinMode
            )
        }
    }

    // Pulse animation for active call ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val formattedDuration = String.format("%02d:%02d", callSeconds / 60, callSeconds % 60)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_call_overlay_surface"),
        color = DarkCathedralBackground.copy(alpha = 0.98f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Adaptive Content Area (Title + Description + Pulsing Avatar)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = "CUSTOS ANIMAE • SPIRITUAL CALL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Call Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .scale(if (playbackState.isPlaying) pulseScale else 1.0f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(CrimsonBurgundy, DarkCathedralSurface)
                            )
                        )
                        .border(2.dp, GoldBright, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Church,
                        contentDescription = "Monastic Call",
                        tint = GoldBright,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title adaptively cut down in UI with maxLines and Ellipsis
                ResponsiveText(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    ),
                    color = GoldBright,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (playbackState.isPlaying) "Connected • Reading Prayer ($formattedDuration)" else "Connected • Active Call ($formattedDuration)",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Prayer Message Box (Parchment Card) adaptively cut down & scrollable in UI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, DarkCathedralCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = GoldSacred,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formattedMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 22.sp
                            ),
                            color = ParchmentLight,
                            textAlign = TextAlign.Center,
                            softWrap = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fixed Bottom Control Section - GUARANTEED ALWAYS VISIBLE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Control Buttons Row (Language Toggle & Mute)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language Toggle (Latin / English)
                    OutlinedIconButton(
                        onClick = {
                            viewModel.toggleLatinMode()
                        },
                        modifier = Modifier.size(52.dp),
                        border = BorderStroke(1.dp, GoldBright),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = if (isLatinMode) CrimsonBurgundy.copy(alpha = 0.5f) else DarkCathedralSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = GoldBright
                        )
                    }

                    // Mute / Unmute TTS
                    OutlinedIconButton(
                        onClick = {
                            isMuted = !isMuted
                            if (isMuted) {
                                viewModel.stopLatinAudio()
                            } else {
                                val spokenText = formatSpokenTextWithBreaks(title, message)
                                viewModel.playLatinAudio(
                                    id = "call_${title.hashCode()}",
                                    text = spokenText,
                                    isLatin = isLatinMode
                                )
                            }
                        },
                        modifier = Modifier.size(52.dp),
                        border = BorderStroke(1.dp, GoldBright),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = if (isMuted) CrimsonBurgundy.copy(alpha = 0.5f) else DarkCathedralSurface
                        )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = GoldBright
                        )
                    }

                    // Postpone / Snooze Button ("I'm Busy")
                    OutlinedIconButton(
                        onClick = {
                            showPostponeDialog = true
                        },
                        modifier = Modifier.size(52.dp),
                        border = BorderStroke(1.dp, GoldBright),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = DarkCathedralSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Postpone",
                            tint = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons: Mark Complete vs End Call
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { applyCompleteDuty() },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(54.dp)
                            .testTag("complete_duty_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete Duty",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Mark Done ✓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.stopLatinAudio()
                            onEndCall()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("end_call_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "End Call",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Postpone / Busy Dialog
        if (showPostponeDialog) {
            AlertDialog(
                onDismissRequest = { showPostponeDialog = false },
                containerColor = DarkCathedralSurface,
                title = {
                    Text(
                        text = "Postpone for Today?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Choose a new time for '$title' today. Your regular schedule will resume normally tomorrow.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ParchmentLight
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { applyReschedule(formatted24h(5)) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("+5m", color = ParchmentLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(previewTime(5), color = GoldSacred, fontSize = 10.sp)
                                }
                            }
                            OutlinedButton(
                                onClick = { applyReschedule(formatted24h(15)) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("+15m", color = ParchmentLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(previewTime(15), color = GoldSacred, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { applyReschedule(formatted24h(30)) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("+30m", color = ParchmentLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(previewTime(30), color = GoldSacred, fontSize = 10.sp)
                                }
                            }
                            OutlinedButton(
                                onClick = { applyReschedule(formatted24h(60)) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("+1h", color = ParchmentLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(previewTime(60), color = GoldSacred, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom time picker
                        Button(
                            onClick = {
                                val cal = java.util.Calendar.getInstance()
                                val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                                val minute = cal.get(java.util.Calendar.MINUTE)
                                val picker = android.app.TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        val formatted = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                                        applyReschedule(formatted)
                                    },
                                    hour,
                                    minute,
                                    false
                                )
                                picker.show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = StaticGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Custom Time...", color = StaticGoldBright, fontSize = 13.sp)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPostponeDialog = false }) {
                        Text("Cancel", color = ParchmentLight.copy(alpha = 0.7f))
                    }
                }
            )
        }
    }
}
