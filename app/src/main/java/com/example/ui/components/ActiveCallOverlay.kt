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
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ActiveCallOverlay(
    title: String,
    message: String,
    viewModel: MainViewModel,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    val isLatinMode by viewModel.isLatinMode.collectAsState()
    val playbackState by viewModel.latinAudioState.collectAsState()

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
            viewModel.playLatinAudio(
                id = "call_${title.hashCode()}",
                text = "$title. $message",
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header - Monastic Identity
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CUSTOS ANIMAE • SPIRITUAL CALL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsing Call Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
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
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (playbackState.isPlaying) "Connected • Reading Prayer ($formattedDuration)" else "Connected • Active Call ($formattedDuration)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentLight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Prayer Message Box (Parchment Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(1.dp, DarkCathedralCardBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = GoldSacred,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            lineHeight = 26.sp
                        ),
                        color = ParchmentLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Control Buttons Row
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
                    modifier = Modifier.size(56.dp),
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
                            viewModel.playLatinAudio(
                                id = "call_${title.hashCode()}",
                                text = "$title. $message",
                                isLatin = isLatinMode
                            )
                        }
                    },
                    modifier = Modifier.size(56.dp),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Big Red End Call Button
            Button(
                onClick = {
                    viewModel.stopLatinAudio()
                    onEndCall()
                },
                modifier = Modifier
                    .size(72.dp)
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
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
