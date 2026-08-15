package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun LatinAudioPlayerController(
    viewModel: MainViewModel,
    prayerId: String,
    prayerText: String,
    isLatin: Boolean? = null,
    modifier: Modifier = Modifier
) {
    val isLatinModeState by viewModel.isLatinMode.collectAsStateWithLifecycle()
    val isLatin = isLatin ?: isLatinModeState
    val playbackState by viewModel.latinAudioState.collectAsStateWithLifecycle()
    val isPlayingThis = playbackState.isPlaying && playbackState.currentId == prayerId

    var showControls by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("latin_audio_controller_$prayerId"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = {
                            if (isPlayingThis) {
                                viewModel.stopLatinAudio()
                            } else {
                                viewModel.playLatinAudio(
                                    id = prayerId,
                                    text = prayerText,
                                    speed = playbackState.speed,
                                    useEcclesiastical = playbackState.useEcclesiastical,
                                    isLatin = isLatin
                                )
                            }
                        },
                        modifier = Modifier
                            .background(if (isPlayingThis) CrimsonBurgundy else GoldSacred, CircleShape)
                            .size(36.dp)
                            .testTag("audio_play_toggle_$prayerId")
                    ) {
                        Icon(
                            imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingThis) "Pause Audio" else if (isLatin) "Listen to Latin Pronunciation" else "Listen to English Prayer",
                            tint = if (isPlayingThis) StaticGoldBright else (if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Color.White else Color.Black),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isPlayingThis) {
                                if (isLatin) "Listening to Latin Pronunciation" else "Listening to English Prayer"
                            } else {
                                if (isLatin) "Latin Audio Pronunciation Guide" else "English Audio Prayer Guide"
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = GoldBright,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isPlayingThis) {
                                if (isLatin) "Pronouncing Latin phonetics offline..." else "Reading English prayer in British accent..."
                            } else {
                                if (isLatin) "Assist learning traditional Roman pronunciation" else "Listen to English prayer with refined English accent"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment,
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(
                    onClick = { showControls = !showControls },
                    modifier = Modifier.size(32.dp).testTag("audio_controls_toggle_$prayerId")
                ) {
                    Icon(
                        imageVector = if (showControls) Icons.Default.SettingsBackupRestore else Icons.Default.Tune,
                        contentDescription = "Audio Settings",
                        tint = GoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (showControls || isPlayingThis) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DarkCathedralCardBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Control panel for Speed, Accent/Voice, and Looping
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLatin) {
                            // 1. Ecclesiastical vs Classical Toggle for Latin
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val targetState = !playbackState.useEcclesiastical
                                    viewModel.toggleLatinAudioEcclesiastical(targetState)
                                    if (isPlayingThis) {
                                        viewModel.playLatinAudio(
                                            id = prayerId,
                                            text = prayerText,
                                            speed = playbackState.speed,
                                            useEcclesiastical = targetState,
                                            isLatin = true
                                        )
                                    }
                                }
                            ) {
                                Checkbox(
                                    checked = playbackState.useEcclesiastical,
                                    onCheckedChange = { useEcc ->
                                        viewModel.toggleLatinAudioEcclesiastical(useEcc)
                                        if (isPlayingThis) {
                                            viewModel.playLatinAudio(
                                                id = prayerId,
                                                text = prayerText,
                                                speed = playbackState.speed,
                                                useEcclesiastical = useEcc,
                                                isLatin = true
                                            )
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = GoldSacred,
                                        checkmarkColor = Color.Black,
                                        uncheckedColor = TextMutedParchment
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ecclesiastical Roman",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ParchmentLight,
                                    fontSize = 10.sp
                                )
                            }
                        } else {
                            // English Accent Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "English Voice Accent",
                                    tint = GoldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "English Accent (UK)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldBright,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // 2. Playback Speed Controller
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Speed:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedParchment,
                                fontSize = 10.sp
                            )

                            val speeds = listOf(0.6f, 0.8f, 1.0f)
                            speeds.forEach { speedVal ->
                                val isSelected = playbackState.speed == speedVal
                                Box(
                                    modifier = Modifier
                                        .border(
                                            1.dp,
                                            if (isSelected) GoldSacred else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .background(
                                            if (isSelected) GoldSacred.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            viewModel.setLatinAudioSpeed(speedVal)
                                            if (isPlayingThis) {
                                                viewModel.playLatinAudio(
                                                    id = prayerId,
                                                    text = prayerText,
                                                    speed = speedVal,
                                                    useEcclesiastical = playbackState.useEcclesiastical,
                                                    isLatin = isLatin
                                                )
                                            }
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${speedVal}x",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GoldBright else TextMutedParchment
                                    )
                                }
                            }
                        }
                    }

                    // 3. Memorization Loop Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Looping option",
                                tint = if (playbackState.isLooping) GoldBright else TextMutedParchment,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Continuous Memorization Loop",
                                style = MaterialTheme.typography.labelSmall,
                                color = ParchmentLight,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = playbackState.isLooping,
                            onCheckedChange = { _ ->
                                viewModel.toggleLatinAudioLooping()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldSacred,
                                uncheckedThumbColor = TextMutedParchment,
                                uncheckedTrackColor = DarkCathedralSurfaceVariant
                            ),
                            modifier = Modifier
                                .height(24.dp)
                                .testTag("audio_loop_switch_$prayerId")
                        )
                    }
                }
            }
        }
    }
}
