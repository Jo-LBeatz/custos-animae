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
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Church
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

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
    viewModel: MainViewModel,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    val isLatinMode by viewModel.isLatinMode.collectAsStateWithLifecycle()
    val playbackState by viewModel.latinAudioState.collectAsStateWithLifecycle()

    // Call duration timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    // Auto-play speech audio for prayer/duty message when call connects
    LaunchedEffect(title, message, isLatinMode) {
        if (!isMuted) {
            val spokenText = formatSpokenTextWithBreaks(title, message)
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
                            text = message,
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Red End Call Button
                Button(
                    onClick = {
                        viewModel.stopLatinAudio()
                        onEndCall()
                    },
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("end_call_button"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}
