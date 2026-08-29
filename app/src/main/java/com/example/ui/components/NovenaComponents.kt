package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.novena.ActiveNovenaInfo
import com.example.data.novena.NovenaData
import com.example.data.novena.NovenaDefinition
import java.util.Calendar

@Composable
fun NovenaBannerCard(
    onOpenNovena: (NovenaDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { Calendar.getInstance() }
    val activeNovenas = remember(today) { NovenaData.getActiveNovenasForDate(today) }

    if (activeNovenas.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        activeNovenas.forEach { active ->
            val isStart = active.isStartDay
            val borderBrush = if (isStart) {
                Brush.horizontalGradient(listOf(Color(0xFFD4AF37), Color(0xFFF59E0B), Color(0xFFD4AF37)))
            } else {
                Brush.horizontalGradient(listOf(Color(0xFF6B21A8), Color(0xFF9333EA), Color(0xFF6B21A8)))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.5.dp, borderBrush, RoundedCornerShape(12.dp))
                    .clickable { onOpenNovena(active.definition) }
                    .testTag("novena_banner_card_${active.definition.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isStart) Color(0xFF1E1B10) else Color(0xFF1A1024)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = if (isStart) Color(0xFF3B2D0A) else Color(0xFF32144A),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (isStart) Color(0xFFD4AF37) else Color(0xFFA855F7),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📿",
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (isStart) Color(0xFFD4AF37) else Color(0xFF9333EA),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isStart) "📿 NOVENA STARTING TODAY" else "📿 DAY ${active.currentDay} OF 9",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Feast: ${active.feastDateString}",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = active.definition.novenaTitle,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ResponsiveText(
                            text = active.definition.summary,
                            color = Color(0xFFD1D5DB),
                            fontSize = 12.sp,
                            maxLines = 3,
                            minFontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onOpenNovena(active.definition) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStart) Color(0xFFD4AF37) else Color(0xFF7E22CE),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isStart) "Begin" else "Pray",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NovenaPrayerDialog(
    novena: NovenaDefinition,
    onDismiss: () -> Unit,
    initialCompletedDays: Set<Int> = emptySet(),
    onMarkDayPrayed: (novenaId: String, dayNumber: Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var selectedDay by remember { mutableStateOf(1) }
    var showLatin by remember { mutableStateOf(false) }
    var prayedDays by remember(initialCompletedDays) { mutableStateOf(initialCompletedDays) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .border(2.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp)),
            color = Color(0xFF121212),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = Color(0xFF3B2D0A),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37))
                        ) {
                            Text(
                                text = "📿 TRADITIONAL NOVENA",
                                color = Color(0xFFD4AF37),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = novena.novenaTitle,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Feast: ${novena.feastTitle}",
                            color = Color(0xFF9CA3AF),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar & Day X of 9 Tracker Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1917)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF44403C))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (prayedDays.size >= 9) "🎉 NOVENA COMPLETED (9 OF 9 DAYS)" else "PROGRESS: DAY ${if (prayedDays.isEmpty()) 1 else (prayedDays.maxOrNull() ?: 1)} OF 9 (${prayedDays.size}/9 Prayed)",
                                color = if (prayedDays.size >= 9) Color(0xFF22C55E) else Color(0xFFD4AF37),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${((prayedDays.size / 9f) * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { prayedDays.size / 9f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (prayedDays.size >= 9) Color(0xFF22C55E) else Color(0xFFD4AF37),
                            trackColor = Color(0xFF292524)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFF333333)
                )

                // Day Selection Tabs (Day 1..9)
                Text(
                    text = "SELECT NOVENA DAY:",
                    color = Color(0xFFD4AF37),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..9).forEach { dayNum ->
                        val isSelected = (selectedDay == dayNum)
                        val isDone = prayedDays.contains(dayNum)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 1.dp)
                                .heightIn(min = 42.dp)
                                .background(
                                    color = when {
                                        isSelected -> Color(0xFFD4AF37)
                                        isDone -> Color(0xFF14532D)
                                        else -> Color(0xFF1E1E1E)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    when {
                                        isSelected -> Color.White
                                        isDone -> Color(0xFF22C55E)
                                        else -> Color(0xFF333333)
                                    },
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedDay = dayNum },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isDone) "D$dayNum ✓" else "D$dayNum",
                                color = when {
                                    isSelected -> Color.Black
                                    isDone -> Color(0xFF86EFAC)
                                    else -> Color.White
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSelected || isDone) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    // Intention Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E2E))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NOVENA INTENTION:", color = Color(0xFFD4AF37), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(novena.intention, color = Color(0xFFE5E7EB), fontSize = 13.sp)
                        }
                    }

                    // Language Toggle if Latin exists
                    if (novena.latinPrayer.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Latin Text", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = showLatin,
                                onCheckedChange = { showLatin = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFD4AF37),
                                    checkedTrackColor = Color(0xFF3B2D0A)
                                )
                            )
                        }
                    }

                    // Day X Prayer
                    Text(
                        text = "DAY $selectedDay PRAYER:",
                        color = Color(0xFFD4AF37),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    val textToDisplay = if (showLatin && novena.latinPrayer.isNotBlank()) {
                        novena.latinPrayer
                    } else {
                        novena.englishPrayer
                    }

                    Text(
                        text = textToDisplay,
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Concluding 3 Our Fathers, 3 Hail Marys, 1 Glory Be
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18150D)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF523F10))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📿 CONCLUDING DEVOTION (Daily):",
                                color = Color(0xFFD4AF37),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Recite 1 Our Father (Pater Noster), 1 Hail Mary (Ave Maria), and 1 Glory Be (Gloria Patri) for the Holy Father's intentions and the triumph of the Church.",
                                color = Color(0xFFD1D5DB),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFF333333)
                )

                // Bottom Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text("Close", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            onMarkDayPrayed(novena.id, selectedDay)
                            prayedDays = prayedDays + selectedDay
                            android.widget.Toast.makeText(
                                context,
                                "Day $selectedDay of ${novena.novenaTitle} saved in local Room database!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prayedDays.contains(selectedDay)) Color(0xFF16A34A) else Color(0xFFD4AF37),
                            contentColor = if (prayedDays.contains(selectedDay)) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            if (prayedDays.contains(selectedDay)) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (prayedDays.contains(selectedDay)) "Day $selectedDay Recorded ✔" else "Mark Day $selectedDay Prayed",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
