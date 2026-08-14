package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.FormatQuote
import android.content.Intent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaintReflection
import com.example.data.calendar.SaintData
import com.example.data.calendar.SaintProfile
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.IlluminatedDivider
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper

import com.example.ui.components.TrackScrollState
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun SaintMeditationScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val todaySaint = viewModel.getTodaySaint()
    var selectedSaint by remember { mutableStateOf(todaySaint) }

    val searchQuery by viewModel.saintSearchQuery.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val searchResults = remember(searchQuery) { SaintData.searchSaints(searchQuery) }

    val existingReflectionFlow = remember(selectedSaint.saintId) {
        viewModel.getSaintReflection(selectedSaint.saintId)
    }
    val existingReflection by existingReflectionFlow.collectAsStateWithLifecycle(initialValue = null)

    var userReflectionText by remember(selectedSaint.saintId) { mutableStateOf("") }
    var personalResolutionText by remember(selectedSaint.saintId) { mutableStateOf(selectedSaint.resolution) }
    var isBookmarked by remember(selectedSaint.saintId) { mutableStateOf(false) }
    var initializedReflectionId by remember { mutableStateOf("") }

    LaunchedEffect(existingReflection, selectedSaint.saintId) {
        existingReflection?.let { ref ->
            if (initializedReflectionId != selectedSaint.saintId) {
                userReflectionText = ref.userReflection
                if (ref.personalResolution.isNotBlank()) {
                    personalResolutionText = ref.personalResolution
                }
                isBookmarked = ref.isBookmarked
                initializedReflectionId = selectedSaint.saintId
            }
        }
    }

    val scrollState = rememberScrollState()
    TrackScrollState(scrollState = scrollState, viewModel = viewModel)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()
    val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 600.dp)
            .padding(horizontal = 16.dp)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection)
            .verticalScroll(scrollState)
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isTopHeaderVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Column {
                GothicHeader(
                    title = AppTranslationHelper.translate("SAINT MEDITATION", appLanguage),
                    subtitle = AppTranslationHelper.translate("1954 Roman Calendar • Virtues, Quotes & Intercession", appLanguage)
                )

                // Search Bar for Saints
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSaintSearchQuery(it) },
                    label = { Text(AppTranslationHelper.translate("Search Saints across 1954 Calendar...", appLanguage)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GoldBright) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_saints_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    ),
                    singleLine = true
                )

                if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(AppTranslationHelper.translate("Search Results:", appLanguage), style = MaterialTheme.typography.labelSmall, color = TextMutedParchment)
                            searchResults.take(4).forEach { saint ->
                                TextButton(
                                    onClick = {
                                        selectedSaint = saint
                                        viewModel.setSaintSearchQuery("")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val transName = AppTranslationHelper.translate(saint.saintName, appLanguage)
                                    val transFeast = AppTranslationHelper.translate(saint.feastTitle, appLanguage)
                                    Text(
                                        text = "☩ $transName ($transFeast)",
                                        color = GoldBright,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Saint Feast Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldSacred)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppTranslationHelper.translate(selectedSaint.feastTitle, appLanguage).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = GoldBright
                        )
                        Text(
                            text = AppTranslationHelper.translate(selectedSaint.feastRank, appLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }

                    IconButton(
                        onClick = {
                            isBookmarked = !isBookmarked
                            viewModel.saveSaintReflection(
                                selectedSaint.saintId,
                                userReflectionText,
                                personalResolutionText,
                                isBookmarked
                            )
                        },
                        modifier = Modifier.testTag("bookmark_saint_btn")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Saint",
                            tint = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = AppTranslationHelper.translate(selectedSaint.saintName, appLanguage),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Virtues Exemplified
                Text(
                    text = "Virtues Exemplified:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedParchment
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    selectedSaint.virtues.forEach { virtue ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CrimsonBurgundy.copy(alpha = 0.3f),
                            border = BorderStroke(0.5.dp, GoldSacred)
                        ) {
                            com.example.ui.components.ResponsiveText(
                                text = virtue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Traditional Quote Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant),
                    border = BorderStroke(1.dp, GoldDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.FormatQuote,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Traditional Quote",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldBright
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${selectedSaint.quote}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp
                            ),
                            color = TextGoldHighlight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Intercessory Prayer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonBurgundy.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, CrimsonBurgundy)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Traditional Intercessory Prayer",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = selectedSaint.intercessoryPrayer,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Reflection & Notes Recorder
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, DarkCathedralCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Record Personal Meditation Notes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userReflectionText,
                    onValueChange = { userReflectionText = it },
                    label = { Text("What did God speak to your soul during this meditation?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("reflection_notes_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.saveSaintReflection(
                            selectedSaint.saintId,
                            userReflectionText,
                            personalResolutionText,
                            isBookmarked
                        )
                        
                        val reflectionContent = mapOf(
                            "Saint" to selectedSaint.saintName,
                            "Personal Reflection" to userReflectionText,
                            "Personal Resolution" to personalResolutionText
                        )
                        com.example.util.PdfGenerator.generatePdf(
                            context,
                            "Personal Reflection",
                            "Reflection_${selectedSaint.saintName.replace(" ", "_")}",
                            reflectionContent
                        )
                        
                        userReflectionText = ""
                        Toast.makeText(context, "Personal meditation notes saved successfully.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_reflection_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black)
                ) {
                    Text("Save Personal Reflection", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Share & Archive Text Snippet",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export your saved reflection and the traditional saint details as a formatted text snippet to other apps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val formattedTextHelper = {
                        buildString {
                            appendLine("† CUSTOS ANIMAE - SAINT MEDITATION REFLECTION †")
                            appendLine("--------------------------------------------------")
                            appendLine("Feast: ${selectedSaint.feastTitle}")
                            appendLine("Saint: ${selectedSaint.saintName}")
                            appendLine("Rank: ${selectedSaint.feastRank}")
                            appendLine()
                            appendLine("• TRADITIONAL QUOTE:")
                            appendLine("\"${selectedSaint.quote}\"")
                            appendLine("— ${selectedSaint.saintName}")
                            appendLine()
                            appendLine("• PRACTICAL RESOLUTION:")
                            appendLine("• ${selectedSaint.resolution}")
                            appendLine()
                            appendLine("==================================================")
                            appendLine("• MY PERSONAL REFLECTION NOTES:")
                            if (userReflectionText.isNotBlank()) {
                                appendLine(userReflectionText)
                            } else {
                                appendLine("(No custom notes recorded yet)")
                            }
                            appendLine()
                            appendLine("• MY RESOLUTION TO IMITATE:")
                            if (personalResolutionText.isNotBlank()) {
                                appendLine(personalResolutionText)
                            } else {
                                appendLine("• ${selectedSaint.resolution}")
                            }
                            appendLine("--------------------------------------------------")
                            appendLine("— Shared from Custos Animae Spiritual Life Companion —")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val textToCopy = formattedTextHelper()
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            Toast.makeText(context, "Reflection text copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_reflection_snippet_btn"),
                        border = BorderStroke(1.dp, GoldDark),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy to clipboard",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Snippet",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val textToShare = formattedTextHelper()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, textToShare)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Export Reflection")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_reflection_snippet_btn"),
                        border = BorderStroke(1.dp, CrimsonBurgundy),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ParchmentLight),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share snippet",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Snippet",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Spiritual Director Export 📜",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compile and format up to a month's worth of your saved saint reflections into an organized, elegant digest to share with your confessor, spiritual director, or priest.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Spacer(modifier = Modifier.height(12.dp))

                val allReflections by viewModel.allSaintReflections.collectAsStateWithLifecycle()
                val calendarEntries by viewModel.allCalendarEntries.collectAsStateWithLifecycle()

                Button(
                    onClick = {
                        if (allReflections.isEmpty()) {
                            Toast.makeText(context, "No saved reflections found to export.", Toast.LENGTH_LONG).show()
                        } else {
                            val digest = buildString {
                                appendLine("==================================================")
                                appendLine("† CUSTOS ANIMAE - SPIRITUAL DIRECTION DIGEST †")
                                appendLine("==================================================")
                                appendLine("Generated: ${viewModel.getTodayLiturgicalInfo().dateString}")
                                appendLine("This digest aggregates your recorded spiritual reflections and resolutions for spiritual review.")
                                appendLine()

                                val sortedReflections = allReflections.sortedBy { it.date }
                                sortedReflections.forEachIndexed { index, reflection ->
                                    val matchedCalendar = calendarEntries.find { it.id == reflection.saintId }
                                    val saintName = matchedCalendar?.saintName ?: "Unknown Saint"
                                    val feastTitle = matchedCalendar?.feastTitle ?: "Feast"
                                    
                                    appendLine("${index + 1}. [${reflection.date}] — † ${saintName.uppercase()} †")
                                    appendLine("   Feast: $feastTitle")
                                    if (matchedCalendar != null) {
                                        appendLine("   Virtues: ${matchedCalendar.virtues}")
                                        appendLine("   Resolution to Imitate: ${reflection.personalResolution.ifBlank { matchedCalendar.resolution }}")
                                    }
                                    appendLine("   My Spiritual Notes:")
                                    appendLine("   \"${reflection.userReflection.ifBlank { "(No notes recorded)" }}\"")
                                    appendLine("--------------------------------------------------")
                                }
                                appendLine()
                                appendLine("— Shared from Custos Animae Spiritual Life Companion —")
                            }

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, digest)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Export Spiritual Director Digest")
                            context.startActivity(shareIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_director_digest_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = ParchmentLight),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldBright)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export digest icon",
                        tint = GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Spiritual Director Digest",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}
