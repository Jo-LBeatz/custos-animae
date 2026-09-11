package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.components.TrackLazyGridScroll
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SpiritualDuty
import com.example.notifications.NotificationScheduler
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.LiturgicalBanner
import com.example.ui.components.LiturgicalColorAndSeasonWidget
import com.example.ui.components.RoseWindowCanvas
import com.example.ui.components.SacredCrossCanvas
import com.example.ui.components.ChiRhoSymbolCanvas
import com.example.ui.components.RosarySymbolCanvas
import com.example.ui.components.ChaliceSymbolCanvas
import com.example.ui.components.CrownSymbolCanvas
import com.example.ui.components.LiturgicalSunCanvas
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper
import java.util.Calendar

// Stained Glass Cathedral Arch Shape
val StainedGlassArchShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0f, h)
    lineTo(0f, h * 0.35f)
    cubicTo(0f, h * 0.10f, w * 0.25f, 0f, w * 0.5f, 0f)
    cubicTo(w * 0.75f, 0f, w, h * 0.10f, w, h * 0.35f)
    lineTo(w, h)
    close()
}

data class StainedGlassPaneData(
    val title: String,
    val subtitle: String,
    val liveBadge: String,
    val icon: ImageVector = Icons.Default.Star,
    val targetTab: Int,
    val primaryGlassColor: Color,
    val secondaryGlassColor: Color,
    val testTag: String
)

@Composable
fun StainedGlassDashboardScreen(
    viewModel: MainViewModel,
    duties: List<SpiritualDuty>,
    onNavigateToTab: (Int) -> Unit
) {
    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    TrackLazyGridScroll(gridState = gridState, viewModel = viewModel)

    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val todayInfo = viewModel.getTodayLiturgicalInfo()
    val todaySaint = viewModel.getTodaySaint()

    val cleanSinceTimestamp by viewModel.virtueCleanSinceTimestamp.collectAsStateWithLifecycle()
    val cleanDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(maxOf(0L, System.currentTimeMillis() - cleanSinceTimestamp))

    val completedDutiesCount = duties.count { it.isCompleted }
    val totalDutiesCount = duties.size

    val cal = Calendar.getInstance()
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val todayRosaryMystery = when (dayOfWeek) {
        Calendar.SUNDAY, Calendar.WEDNESDAY, Calendar.SATURDAY -> "Glorious Mysteries"
        Calendar.MONDAY, Calendar.THURSDAY -> "Joyful Mysteries"
        Calendar.TUESDAY, Calendar.FRIDAY -> "Sorrowful Mysteries"
        else -> "Joyful Mysteries"
    }

    val panes = remember(completedDutiesCount, totalDutiesCount, todaySaint, todayRosaryMystery, todayInfo, cleanDays) {
        listOf(
            StainedGlassPaneData(
                title = "RULE OF LIFE",
                subtitle = "Daily, Weekly, Monthly & Yearly Duties",
                liveBadge = "$completedDutiesCount/$totalDutiesCount Completed Today",
                icon = Icons.Default.Checklist,
                targetTab = 1,
                primaryGlassColor = Color(0xFF1B4332), // Emerald Green
                secondaryGlassColor = Color(0xFF2D6A4F),
                testTag = "pane_rule_of_life"
            ),
            StainedGlassPaneData(
                title = "VIRTUE & CHASTITY",
                subtitle = "Chastity Timer & Victory Log",
                liveBadge = "$cleanDays Days Clean",
                icon = Icons.Default.Shield,
                targetTab = 2,
                primaryGlassColor = Color(0xFF581C87), // Deep Imperial Purple
                secondaryGlassColor = Color(0xFF7E22CE),
                testTag = "pane_virtue"
            ),
            StainedGlassPaneData(
                title = "SAINT MEDITATION",
                subtitle = "Virtues & Daily Reflection",
                liveBadge = todaySaint.saintName,
                icon = Icons.Default.SelfImprovement,
                targetTab = 3,
                primaryGlassColor = Color(0xFF680014), // Crimson Ruby
                secondaryGlassColor = Color(0xFF991B1B),
                testTag = "pane_saint"
            ),
            StainedGlassPaneData(
                title = "PRAYER LIBRARY",
                subtitle = "Holy Rosary ($todayRosaryMystery) & Devotions",
                liveBadge = "Today: $todayRosaryMystery",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                targetTab = 4,
                primaryGlassColor = Color(0xFF0F2B5C), // Sapphire Blue
                secondaryGlassColor = Color(0xFF1D4ED8),
                testTag = "pane_prayers"
            ),
            StainedGlassPaneData(
                title = "NIGHT EXAMEN",
                subtitle = "Conscience, Virtues & Gratitude",
                liveBadge = "Daily Examen of Conscience",
                icon = Icons.Default.Church,
                targetTab = 5,
                primaryGlassColor = Color(0xFF78350F), // Topaz Amber
                secondaryGlassColor = Color(0xFFB45309),
                testTag = "pane_examen"
            ),
            StainedGlassPaneData(
                title = "1954 CALENDAR",
                subtitle = "Traditional Feasts & Fasting Rules",
                liveBadge = if (todayInfo.isFastingDay) "Traditional Fast Day" else "Feast Day",
                icon = Icons.Default.CalendarMonth,
                targetTab = 6,
                primaryGlassColor = Color(0xFF134E4A), // Deep Cobalt Teal
                secondaryGlassColor = Color(0xFF0F766E),
                testTag = "pane_calendar"
            ),
            StainedGlassPaneData(
                title = "DECEPTION OF VATICAN II",
                subtitle = "Conciliar Analysis & Critical Studies",
                liveBadge = "Traditional Theological Critique",
                icon = Icons.Default.Warning,
                targetTab = 7,
                primaryGlassColor = Color(0xFF4A0E17), // Deep Obsidian Blood
                secondaryGlassColor = Color(0xFF881337),
                testTag = "pane_deception"
            ),
            StainedGlassPaneData(
                title = "REGULAVITAE",
                subtitle = "Powered by RegulaVitae • Socials & Web",
                liveBadge = "Official Community",
                icon = Icons.Default.Info,
                targetTab = 9,
                primaryGlassColor = Color(0xFF3B1E54), // Royal Purple & Gold
                secondaryGlassColor = Color(0xFF5D2E8C),
                testTag = "pane_regulavitae"
            )
        )
    }

    val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection)
    ) {
        val columns = if (maxWidth > 600.dp) 3 else 2

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1000.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
        ) {
            // Title Header & Liturgical Banner
            item(span = { GridItemSpan(columns) }) {
                val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()

                Column {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isTopHeaderVisible,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
                    ) {
                        Column {
                            GothicHeader(
                                title = "CUSTOS ANIMAE",
                                subtitle = AppTranslationHelper.translate("POWERED BY REGULAVITAE • Sacred Rule & Stained Glass Panes", appLanguage)
                            )

                            LiturgicalBanner(info = todayInfo, appLanguage = appLanguage)

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    RuleOfLifeProgressCard(
                        viewModel = viewModel,
                        duties = duties,
                        onNavigateToTab = onNavigateToTab
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Glass Lead Tracery Notice
                    Text(
                        text = AppTranslationHelper.translate("☩ Tap any stained glass pane to enter devotion or rule ☩", appLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.1.sp
                        ),
                        color = GoldBright,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            // Stained Glass Grid Panes
            items(panes.size, key = { index -> panes[index].title }) { index ->
                val pane = panes[index]
                StainedGlassPaneContainer(
                    pane = pane,
                    appLanguage = appLanguage,
                    onClick = { onNavigateToTab(pane.targetTab) }
                )
            }

            // Daily Wisdom: Desert Fathers & Traditional Saints
            item(span = { GridItemSpan(columns) }) {
                val todaySaintQuotes by viewModel.todaySaintQuotes.collectAsStateWithLifecycle()
                SaintlyQuotesCard(
                    todaySaintQuotes = todaySaintQuotes,
                    saintName = todaySaint.saintName,
                    onNavigateToRule = { onNavigateToTab(1) }
                )
            }

            // RegulaVitae Apostolate & Social Community Banner
            item(span = { GridItemSpan(columns) }) {
                RegulaVitaeDashboardCard(
                    onNavigateToRegulaVitae = { onNavigateToTab(9) }
                )
            }
        }
    }
}

@Composable
fun StainedGlassPaneContainer(
    pane: StainedGlassPaneData,
    appLanguage: AppLanguage,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .testTag(pane.testTag)
            .shadow(8.dp, shape = StainedGlassArchShape)
            .clip(StainedGlassArchShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        pane.secondaryGlassColor.copy(alpha = 0.95f),
                        pane.primaryGlassColor,
                        DarkCathedralBackground
                    )
                )
            )
            .border(
                BorderStroke(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            GoldBright,
                            Color(0xFF2C2F38), // Metallic Lead line
                            GoldSacred,
                            Color(0xFF1E2128)
                        )
                    )
                ),
                shape = StainedGlassArchShape
            )
            .clickable { onClick() }
    ) {
        // Stained Glass Refraction Sheen & Tracery Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Angled Glass Light Glare
            drawPath(
                path = Path().apply {
                    moveTo(w * 0.2f, 0f)
                    lineTo(w * 0.5f, 0f)
                    lineTo(0f, h * 0.8f)
                    lineTo(0f, h * 0.4f)
                    close()
                },
                color = Color.White.copy(alpha = 0.12f)
            )

            // Leaded Lead Lines across the Glass Pane
            drawLine(
                color = Color(0xFF1A1D24),
                start = Offset(w * 0.5f, 0f),
                end = Offset(w * 0.5f, h),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color(0xFF1A1D24),
                start = Offset(0f, h * 0.55f),
                end = Offset(w, h * 0.55f),
                strokeWidth = 2.dp.toPx()
            )

            // Arch Tracery Inner Arc
            drawPath(
                path = Path().apply {
                    moveTo(w * 0.15f, h * 0.35f)
                    cubicTo(w * 0.15f, h * 0.15f, w * 0.35f, h * 0.08f, w * 0.5f, h * 0.08f)
                    cubicTo(w * 0.65f, h * 0.08f, w * 0.85f, h * 0.15f, w * 0.85f, h * 0.35f)
                },
                color = GoldBright.copy(alpha = 0.4f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Pane Contents
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Rose Window and Traditional Catholic Symbol
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                RoseWindowCanvas(
                    modifier = Modifier.size(36.dp),
                    accentColor = GoldBright,
                    secondaryColor = pane.secondaryGlassColor
                )
                when (pane.targetTab) {
                    1 -> ChiRhoSymbolCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    2 -> CrownSymbolCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    3 -> SacredCrossCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    4 -> RosarySymbolCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    5 -> ChaliceSymbolCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    6 -> SacredCrossCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    7 -> LiturgicalSunCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    9 -> ChiRhoSymbolCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                    else -> SacredCrossCanvas(modifier = Modifier.size(20.dp), tint = GoldBright)
                }
            }

            // Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                com.example.ui.components.ResponsiveText(
                    text = AppTranslationHelper.translate(pane.title, appLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        fontSize = 13.sp
                    ),
                    color = Color(0xFFFBF8EE),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    minFontSize = 9.sp
                )

                com.example.ui.components.ResponsiveText(
                    text = AppTranslationHelper.translate(pane.subtitle, appLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    minFontSize = 7.5.sp
                )
            }

            // Live Dynamic Status Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkCathedralBackground.copy(alpha = 0.85f),
                border = BorderStroke(0.8.dp, GoldSacred.copy(alpha = 0.7f)),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                com.example.ui.components.ResponsiveText(
                    text = AppTranslationHelper.translateDynamic(pane.liveBadge, appLanguage),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.5.sp
                    ),
                    color = GoldBright,
                    maxLines = 2,
                    minFontSize = 7.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RuleOfLifeProgressCard(
    viewModel: MainViewModel,
    duties: List<SpiritualDuty>,
    onNavigateToTab: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val totalDutiesCount = duties.size
    val completedDutiesCount = duties.count { it.isCompleted }
    val percent = if (totalDutiesCount > 0) completedDutiesCount.toFloat() / totalDutiesCount else 0f
    val maxStreak = if (duties.isNotEmpty()) duties.maxOf { it.streakCount } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_of_life_progress_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = "Rule of Life Icon",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY RULE PROGRESS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = GoldBright
                    )
                }

                if (maxStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(CrimsonDeep.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, GoldSacred.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = GoldBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$maxStreak Day Streak",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = GoldBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (totalDutiesCount == 0) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No daily spiritual duties scheduled in your Rule of Life.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMutedParchment,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onNavigateToTab(1) }, // Navigate to Rule tab
                        border = BorderStroke(1.dp, GoldSacred),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("configure_rule_btn")
                    ) {
                        Text(
                            text = "Configure Rule of Life",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // Dynamic Progress Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$completedDutiesCount of $totalDutiesCount Duties Fulfilled",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentLight
                    )
                    Text(
                        text = "${(percent * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Segmented Stained Glass Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF1A1D24), RoundedCornerShape(6.dp))
                        .background(Color(0xFF0C0F13)),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    duties.forEachIndexed { index, duty ->
                        val segmentColor = if (duty.isCompleted) {
                            // Liturgical colored glowing segment based on category
                            when (duty.category) {
                                "PRAYER" -> GoldBright
                                "FASTING" -> CrimsonLight
                                "CHARITY" -> LiturgicalGreen
                                "READING" -> LiturgicalRose
                                else -> GoldSacred
                            }
                        } else {
                            // Uncompleted dark pane
                            Color(0xFF1A222C)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(segmentColor)
                                .border(0.5.dp, Color(0xFF13171D))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Spiritual quote/encouragement depending on progress percentage
                val encouragement = when {
                    percent >= 1f -> "† Laus Deo! Your spiritual duties are completed. Stay in His presence."
                    percent >= 0.6f -> "† Well done! Maintain vigilance in your sacred disciplines."
                    percent > 0f -> "† Steps on the path of grace. Continue faithfully."
                    else -> "† \"Begin with prayer, continue with devotion, end with praise.\""
                }

                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp
                    ),
                    color = TextMutedParchment
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Check-off row header
                Text(
                    text = "QUICK COMPLETE TODAY'S DUTIES:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = GoldDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Horizontal scrollable check-off row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    duties.forEach { duty ->
                        val isDone = duty.isCompleted
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDone) CrimsonDeep.copy(alpha = 0.4f) else DarkCathedralSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isDone) GoldBright else DarkCathedralCardBorder
                            ),
                            modifier = Modifier
                                .clickable {
                                    viewModel.toggleDutyCompletion(duty)
                                    val status = if (!isDone) "fulfilled" else "reopened"
                                    Toast.makeText(context, "Duty '${duty.title}' is now $status.", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("quick_complete_${duty.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = if (isDone) "Completed" else "Pending",
                                    tint = if (isDone) GoldBright else TextMutedParchment,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = duty.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDone) GoldLight else ParchmentLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiturgicalSeasonTrackerCard(
    currentSeason: com.example.data.calendar.LiturgicalSeason
) {
    val seasonsInOrder = listOf(
        com.example.data.calendar.LiturgicalSeason.ADVENT,
        com.example.data.calendar.LiturgicalSeason.CHRISTMASTIDE,
        com.example.data.calendar.LiturgicalSeason.SEPTUAGESIMA,
        com.example.data.calendar.LiturgicalSeason.LENT,
        com.example.data.calendar.LiturgicalSeason.PASSIONTIDE,
        com.example.data.calendar.LiturgicalSeason.PASCHALTIDE,
        com.example.data.calendar.LiturgicalSeason.PENTECOST_SEASON
    )

    val seasonMetadata = mapOf(
        com.example.data.calendar.LiturgicalSeason.ADVENT to SeasonInfo(
            shortName = "Advent",
            latinName = "Adventus",
            icon = Icons.Default.Notifications,
            color = LiturgicalViolet,
            focus = "Spiritual Preparation & Expectation",
            practice = "Fasting, prayer, and quiet longing for Christ's birth and second coming."
        ),
        com.example.data.calendar.LiturgicalSeason.CHRISTMASTIDE to SeasonInfo(
            shortName = "Christmas",
            latinName = "Nativitas",
            icon = Icons.Default.Star,
            color = LiturgicalWhiteGold,
            focus = "Incarnation & Manifestation",
            practice = "Adoring the Infant King, celebrating holy joy, and contemplating God made man."
        ),
        com.example.data.calendar.LiturgicalSeason.SEPTUAGESIMA to SeasonInfo(
            shortName = "Septuagesima",
            latinName = "Pre-Lent",
            icon = Icons.Default.Lock,
            color = LiturgicalViolet,
            focus = "Pre-Lenten Preparation",
            practice = "Putting aside festive songs (the Alleluia is buried) and girding ourselves for battle."
        ),
        com.example.data.calendar.LiturgicalSeason.LENT to SeasonInfo(
            shortName = "Lent",
            latinName = "Quadragesima",
            icon = Icons.Default.History,
            color = LiturgicalViolet,
            focus = "Forty Days of Penance",
            practice = "Fasting, abstinence, daily examinations, and generous almsgiving."
        ),
        com.example.data.calendar.LiturgicalSeason.PASSIONTIDE to SeasonInfo(
            shortName = "Passiontide",
            latinName = "Passio",
            icon = Icons.Default.Favorite,
            color = LiturgicalViolet,
            focus = "The Holy Passion & Cross",
            practice = "Veiling of crucifixes and statues, deep contemplation on Christ's sacrifice."
        ),
        com.example.data.calendar.LiturgicalSeason.PASCHALTIDE to SeasonInfo(
            shortName = "Eastertide",
            latinName = "Pascha",
            icon = Icons.Default.AutoAwesome,
            color = LiturgicalWhiteGold,
            focus = "Resurrection Triumph",
            practice = "Joyful celebration of Easter, the Ascension, and the descent of the Holy Ghost."
        ),
        com.example.data.calendar.LiturgicalSeason.PENTECOST_SEASON to SeasonInfo(
            shortName = "Pentecost",
            latinName = "Pentecoste",
            icon = Icons.Default.SelfImprovement,
            color = LiturgicalGreen,
            focus = "Spiritual Growth & Virtues",
            practice = "Fostering daily virtues, studying Church teaching, and fighting spiritual combat."
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("liturgical_season_tracker_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "† 1954 LITURGICAL SEASON TRACKER †",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CrimsonBurgundy.copy(alpha = 0.3f),
                    border = BorderStroke(0.5.dp, GoldSacred)
                ) {
                    com.example.ui.components.ResponsiveText(
                        text = "1954 Traditional Liturgical Calendar Season Tracker",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sequence Row (Horizontal scroll)
            val scrollState = rememberScrollState()

            LaunchedEffect(currentSeason) {
                val activeIndex = seasonsInOrder.indexOf(currentSeason)
                if (activeIndex >= 0) {
                    try {
                        scrollState.animateScrollTo(activeIndex * 160)
                    } catch (e: Exception) {}
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                seasonsInOrder.forEachIndexed { index, season ->
                    val isCurrent = season == currentSeason
                    val info = seasonMetadata[season] ?: return@forEachIndexed

                    // Render Season Node
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        // Badge (Stained Glass Circle)
                        Box(
                            modifier = Modifier
                                .size(if (isCurrent) 42.dp else 34.dp)
                                .shadow(
                                    if (isCurrent) 6.dp else 0.dp,
                                    shape = CircleShape,
                                    ambientColor = GoldBright,
                                    spotColor = GoldBright
                                )
                                .background(
                                    if (isCurrent) info.color.copy(alpha = 0.9f) else info.color.copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) GoldBright else info.color.copy(alpha = 0.6f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = info.icon,
                                contentDescription = info.shortName,
                                tint = if (isCurrent) {
                                    if (info.color == LiturgicalWhiteGold) Color.Black else ParchmentLight
                                } else {
                                    info.color
                                },
                                modifier = Modifier.size(if (isCurrent) 20.dp else 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        com.example.ui.components.ResponsiveText(
                            text = info.shortName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                fontSize = if (isCurrent) 11.sp else 10.sp
                            ),
                            color = if (isCurrent) GoldBright else TextMutedParchment,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            minFontSize = 7.5.sp
                        )

                        com.example.ui.components.ResponsiveText(
                            text = info.latinName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = if (isCurrent) GoldLight.copy(alpha = 0.8f) else TextMutedParchment.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            minFontSize = 6.5.sp
                        )
                    }

                    // Draw connecting lead line if not the last item
                    if (index < seasonsInOrder.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            (seasonMetadata[season]?.color ?: GoldSacred).copy(alpha = 0.5f),
                                            (seasonMetadata[seasonsInOrder[index + 1]]?.color ?: GoldSacred).copy(alpha = 0.5f)
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlighted current season details
            val activeInfo = seasonMetadata[currentSeason]
            if (activeInfo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = activeInfo.color.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, activeInfo.color.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(activeInfo.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE: ${activeInfo.latinName.uppercase()} (${activeInfo.shortName})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = GoldBright
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "• Spiritual Focus: ${activeInfo.focus}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = ParchmentLight
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "• Traditional Practice: ${activeInfo.practice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }
                }
            }
        }
    }
}

private data class SeasonInfo(
    val shortName: String,
    val latinName: String,
    val icon: ImageVector,
    val color: Color,
    val focus: String,
    val practice: String
)

@Composable
fun SaintlyQuotesCard(
    todaySaintQuotes: List<com.example.data.SaintQuoteEntity>,
    saintName: String,
    onNavigateToRule: () -> Unit = {}
) {
    var dayOffset by remember { mutableStateOf(0) }
    val dailyWisdom = remember(dayOffset) {
        com.example.data.calendar.DesertFathersAndSaintsQuotes.getDailyQuoteForDate(dayOffset)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("saintly_quotes_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section Title & Navigation Arrows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CrimsonBurgundy.copy(alpha = 0.3f))
                            .border(1.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Daily Wisdom",
                            tint = GoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Monastic & Saintly Wisdom",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                        Text(
                            text = "Desert Fathers & Pre-Vatican II Traditional Saints",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                }

                // Prev/Next buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { dayOffset-- },
                        modifier = Modifier.size(32.dp).testTag("quotes_prev_day")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day Quote", tint = GoldBright)
                    }
                    if (dayOffset != 0) {
                        TextButton(
                            onClick = { dayOffset = 0 },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Today", fontSize = 11.sp, color = GoldLight)
                        }
                    }
                    IconButton(
                        onClick = { dayOffset++ },
                        modifier = Modifier.size(32.dp).testTag("quotes_next_day")
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Day Quote", tint = GoldBright)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Discipline Category Badge
            Surface(
                color = CrimsonBurgundy.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.6f)),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dailyWisdom.disciplineCategory,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = GoldLight
                    )
                }
            }

            // Main Desert Father / Traditional Saint Quote
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .border(1.dp, DarkCathedralCardBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "“${dailyWisdom.quoteText}”",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 23.sp
                    ),
                    color = ParchmentLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— ${dailyWisdom.saintOrFatherName}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright,
                    modifier = Modifier.align(Alignment.End)
                )
                Text(
                    text = "${dailyWisdom.era} • ${dailyWisdom.sourceWork}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedParchment,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Practical Rule of Life Discipline Application
            Surface(
                color = StaticGoldenBrown.copy(alpha = 0.18f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rule of Life Discipline Practice:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dailyWisdom.ruleApplication,
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Updates automatically at midnight",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedParchment,
                    fontSize = 10.sp
                )

                OutlinedButton(
                    onClick = onNavigateToRule,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldBright),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply to Rule of Life", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Additional Today's Saint Quotes from Room DB if dayOffset == 0
            if (dayOffset == 0 && todaySaintQuotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Commemorated Saint Reflections ($saintName):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldDark
                )
                todaySaintQuotes.forEach { quote ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "“${quote.quoteText}”",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = ParchmentLight
                        )
                        if (quote.sourceWork.isNotBlank()) {
                            Text(
                                text = "— ${quote.sourceWork}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedParchment,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegulaVitaeDashboardCard(
    onNavigateToRegulaVitae: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("regulavitae_dashboard_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(GoldBright, CrimsonBurgundy, GoldSacred)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CrimsonBurgundy.copy(alpha = 0.3f), CircleShape)
                            .border(1.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "RegulaVitae",
                            tint = GoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        com.example.ui.components.ResponsiveText(
                            text = "POWERED BY REGULAVITAE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            ),
                            color = GoldBright,
                            maxLines = 2,
                            minFontSize = 9.sp
                        )
                        Text(
                            text = "My Personal Work & Social Channels",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldSacred.copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    com.example.ui.components.ResponsiveText(
                        text = "Connect",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        minFontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Follow my official channels under RegulaVitae for daily traditional feast reflections, rules of life, video catechesis, and spiritual discipline updates.",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Social Links Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029Vb8WpzkFi8xWdG8IJf2F"))
                        context.startActivity(intent)
                    },
                    border = BorderStroke(1.dp, Color(0xFF25D366)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp, color = ParchmentLight)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://regulavitae.vercel.app/"))
                        context.startActivity(intent)
                    },
                    border = BorderStroke(1.dp, GoldSacred),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = GoldBright, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Website", fontSize = 11.sp, color = ParchmentLight)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/regulavitae/#"))
                        context.startActivity(intent)
                    },
                    border = BorderStroke(1.dp, Color(0xFFE1306C)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Instagram", fontSize = 11.sp, color = ParchmentLight)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@regulavitae"))
                        context.startActivity(intent)
                    },
                    border = BorderStroke(1.dp, Color(0xFFFF0000)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube", fontSize = 11.sp, color = ParchmentLight)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToRegulaVitae,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_regulavitae_page_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonBurgundy,
                    contentColor = StaticGoldBright
                ),
                border = BorderStroke(1.dp, GoldSacred),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Open RegulaVitae Community & Links Page",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

