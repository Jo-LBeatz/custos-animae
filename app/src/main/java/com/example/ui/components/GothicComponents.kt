package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.data.SpiritualDuty
import com.example.data.calendar.LiturgicalDayInfo
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper

/**
 * Traditional Catholic Canvas Geometry: Futuristic Gothic Rose Window
 */
@Composable
fun RoseWindowCanvas(
    modifier: Modifier = Modifier,
    accentColor: Color = GoldBright,
    secondaryColor: Color = CrimsonBurgundy
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) / 2f * 0.9f

        // Outer Glowing Ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                center = center,
                radius = radius * 1.2f
            ),
            center = center,
            radius = radius * 1.1f
        )

        // Outer Geometry Circle
        drawCircle(
            color = accentColor.copy(alpha = 0.8f),
            center = center,
            radius = radius,
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner Circle
        drawCircle(
            color = accentColor.copy(alpha = 0.5f),
            center = center,
            radius = radius * 0.65f,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Center Halo Circle
        drawCircle(
            color = secondaryColor.copy(alpha = 0.8f),
            center = center,
            radius = radius * 0.25f
        )
        drawCircle(
            color = accentColor,
            center = center,
            radius = radius * 0.25f,
            style = Stroke(width = 2.dp.toPx())
        )

        // 8 Radiant Rose Petals
        val petals = 8
        val angleStep = 360f / petals
        for (i in 0 until petals) {
            rotate(degrees = i * angleStep, pivot = center) {
                val petalRadius = radius * 0.35f
                val petalCenter = Offset(center.x, center.y - radius * 0.45f)
                drawCircle(
                    color = accentColor.copy(alpha = 0.4f),
                    center = petalCenter,
                    radius = petalRadius,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawLine(
                    color = accentColor.copy(alpha = 0.6f),
                    start = center,
                    end = Offset(center.x, center.y - radius),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}

/**
 * Traditional Catholic Canvas Geometry: Chi-Rho (☧) Symbol
 */
@Composable
fun ChiRhoSymbolCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val h = size.height * 0.8f
        val w = size.width * 0.8f
        val strokePx = 2.5.dp.toPx()

        val pTop = center.y - h / 2f
        val pBottom = center.y + h / 2f
        drawLine(
            color = tint,
            start = Offset(center.x, pTop),
            end = Offset(center.x, pBottom),
            strokeWidth = strokePx
        )

        val loopPath = Path().apply {
            moveTo(center.x, pTop)
            cubicTo(
                center.x + w * 0.45f, pTop,
                center.x + w * 0.45f, pTop + h * 0.35f,
                center.x, pTop + h * 0.35f
            )
        }
        drawPath(path = loopPath, color = tint, style = Stroke(width = strokePx))

        val chiWidth = w * 0.45f
        val chiHeight = h * 0.45f
        drawLine(
            color = tint,
            start = Offset(center.x - chiWidth, center.y - chiHeight * 0.5f),
            end = Offset(center.x + chiWidth, center.y + chiHeight * 0.5f),
            strokeWidth = strokePx
        )
        drawLine(
            color = tint,
            start = Offset(center.x - chiWidth, center.y + chiHeight * 0.5f),
            end = Offset(center.x + chiWidth, center.y - chiHeight * 0.5f),
            strokeWidth = strokePx
        )
    }
}

/**
 * Traditional Catholic Canvas Geometry: Sacred Cross (☩) with Halo
 */
@Composable
fun SacredCrossCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright,
    haloColor: Color = CrimsonBurgundy
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val armLength = minOf(size.width, size.height) * 0.4f
        val strokeWidthPx = 3.dp.toPx()

        drawCircle(
            color = haloColor.copy(alpha = 0.4f),
            center = center,
            radius = armLength * 0.7f
        )
        drawCircle(
            color = tint.copy(alpha = 0.7f),
            center = center,
            radius = armLength * 0.7f,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawLine(
            color = tint,
            start = Offset(center.x, center.y - armLength),
            end = Offset(center.x, center.y + armLength),
            strokeWidth = strokeWidthPx
        )

        drawLine(
            color = tint,
            start = Offset(center.x - armLength * 0.75f, center.y - armLength * 0.2f),
            end = Offset(center.x + armLength * 0.75f, center.y - armLength * 0.2f),
            strokeWidth = strokeWidthPx
        )
    }
}


@Composable
fun GothicHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = GoldSacred,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(CrimsonBurgundy, CircleShape)
                    .border(1.dp, StaticGoldBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☩",
                    color = StaticGoldBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldBright
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedParchment
                    )
                }
            }
            
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }

        if (badgeText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, badgeColor)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = badgeColor,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        IlluminatedDivider()
    }
}

@Composable
fun IlluminatedDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, GoldSacred)
                    )
                )
        )
        Text(
            text = " ❖ ",
            color = GoldBright,
            fontSize = 12.sp
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GoldSacred, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun LiturgicalBanner(info: LiturgicalDayInfo, appLanguage: AppLanguage = AppLanguage.ENGLISH) {
    val seasonColor = try {
        Color(android.graphics.Color.parseColor(info.season.colorHex))
    } catch (e: Exception) {
        GoldSacred
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("liturgical_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCathedralSurface
        ),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(seasonColor, CircleShape)
                            .border(1.dp, GoldBright, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val vestmentLabel = if (info.vestmentColor.isNotEmpty()) "${info.vestmentColor.uppercase()} VESTMENTS" else "TODAY'S FEAST"
                    Text(
                        text = AppTranslationHelper.translate(vestmentLabel, appLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }

                Text(
                    text = AppTranslationHelper.translate(info.rank, appLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppTranslationHelper.translate(info.feastName, appLanguage),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = ParchmentLight
            )

            if (info.isEmberDay || info.isFastingDay || info.isAbstinenceDay) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if (info.isEmberDay) {
                        AssistChip(
                            onClick = {},
                            label = { com.example.ui.components.ResponsiveText(AppTranslationHelper.translate("Quattuor Tempora (Ember Day)", appLanguage), fontSize = 11.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = LiturgicalViolet.copy(alpha = 0.3f),
                                labelColor = GoldBright
                            ),
                            border = BorderStroke(0.5.dp, LiturgicalViolet)
                        )
                    }
                    if (info.isFastingDay) {
                        AssistChip(
                            onClick = {},
                            label = { com.example.ui.components.ResponsiveText(AppTranslationHelper.translate("Traditional Fast", appLanguage), fontSize = 11.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = CrimsonBurgundy.copy(alpha = 0.35f),
                                labelColor = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) StaticGoldenBrown else StaticGoldBright
                            ),
                            border = BorderStroke(0.5.dp, if (MaterialTheme.colorScheme.background.luminance() > 0.5f) StaticWarmBronze else CrimsonBurgundy)
                        )
                    }
                    if (info.isAbstinenceDay) {
                        AssistChip(
                            onClick = {},
                            label = { com.example.ui.components.ResponsiveText(AppTranslationHelper.translate("Abstinence", appLanguage), fontSize = 11.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DarkCathedralSurfaceVariant,
                                labelColor = TextGoldHighlight
                            ),
                            border = BorderStroke(0.5.dp, GoldDark)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpiritualDutyCard(
    duty: SpiritualDuty,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isSacred = duty.priority == "SACRED"

    // Animate container background and border colors using spring
    val containerBg by animateColorAsState(
        targetValue = if (duty.isCompleted) DarkCathedralSurface.copy(alpha = 0.6f) else DarkCathedralSurface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "container_bg_anim"
    )

    val borderColor by animateColorAsState(
        targetValue = if (duty.isCompleted) GoldBright.copy(alpha = 0.5f) else if (isSacred) GoldSacred else DarkCathedralCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "border_color_anim"
    )

    val textColor by animateColorAsState(
        targetValue = if (duty.isCompleted) TextMutedParchment else ParchmentLight,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "text_color_anim"
    )

    // Spring animation for icon scale and rotation
    val iconScale by animateFloatAsState(
        targetValue = if (duty.isCompleted) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale_anim"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (duty.isCompleted) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "icon_rotation_anim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("duty_card_${duty.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(if (isSacred) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
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
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleComplete()
                        },
                        modifier = Modifier
                            .testTag("toggle_complete_${duty.id}")
                            .scale(iconScale)
                            .graphicsLayer(rotationZ = iconRotation)
                    ) {
                        Icon(
                            imageVector = if (duty.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = "Toggle Duty Completion",
                            tint = if (duty.isCompleted) GoldBright else TextMutedParchment,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = duty.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (duty.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = textColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = GoldSacred,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val scheduleText = formatDutySchedule(duty)
                            Text(
                                text = "$scheduleText • ${duty.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (duty.streakCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CrimsonBurgundy.copy(alpha = 0.35f),
                            border = BorderStroke(0.5.dp, if (MaterialTheme.colorScheme.background.luminance() > 0.5f) StaticWarmBronze else GoldSacred)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔥 ${duty.streakCount}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) StaticGoldenBrown else StaticGoldBright
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Duty",
                            tint = TextMutedParchment,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Duty",
                            tint = CrimsonLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (duty.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = duty.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentLight.copy(alpha = 0.85f),
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
        }
    }
}

@Composable
fun RosarySymbolCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) * 0.38f
        val strokeWidth = 1.5.dp.toPx()

        // Draw Rosary Loop (Circle of beads)
        drawCircle(
            color = tint.copy(alpha = 0.8f),
            center = center,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Draw small bead dots along the circle
        val beadCount = 8
        for (i in 0 until beadCount) {
            val angle = (i * 360f / beadCount) * (Math.PI / 180f)
            val bx = center.x + radius * kotlin.math.cos(angle).toFloat()
            val by = center.y + radius * kotlin.math.sin(angle).toFloat()
            drawCircle(color = tint, center = Offset(bx, by), radius = 2.dp.toPx())
        }

        // Crucifix hanging at bottom
        val crucTop = center.y + radius
        val crucBottom = crucTop + radius * 0.7f
        drawLine(color = tint, start = Offset(center.x, crucTop), end = Offset(center.x, crucBottom), strokeWidth = strokeWidth * 1.2f)
        drawLine(color = tint, start = Offset(center.x - radius * 0.2f, crucTop + radius * 0.2f), end = Offset(center.x + radius * 0.2f, crucTop + radius * 0.2f), strokeWidth = strokeWidth)
    }
}

@Composable
fun ChaliceSymbolCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val w = size.width * 0.7f
        val h = size.height * 0.8f
        val stroke = 2.dp.toPx()

        // Sacred Host above chalice
        val hostRadius = w * 0.25f
        val hostCenter = Offset(center.x, center.y - h * 0.3f)
        drawCircle(color = tint, center = hostCenter, radius = hostRadius, style = Stroke(width = stroke))
        // Cross inside host
        drawLine(color = tint, start = Offset(hostCenter.x, hostCenter.y - hostRadius * 0.6f), end = Offset(hostCenter.x, hostCenter.y + hostRadius * 0.6f), strokeWidth = stroke * 0.8f)
        drawLine(color = tint, start = Offset(hostCenter.x - hostRadius * 0.6f, hostCenter.y), end = Offset(hostCenter.x + hostRadius * 0.6f, hostCenter.y), strokeWidth = stroke * 0.8f)

        // Chalice Bowl
        val bowlTop = center.y + h * 0.05f
        val bowlBottom = center.y + h * 0.4f
        drawLine(color = tint, start = Offset(center.x - w * 0.35f, bowlTop), end = Offset(center.x + w * 0.35f, bowlTop), strokeWidth = stroke)
        val cupPath = Path().apply {
            moveTo(center.x - w * 0.35f, bowlTop)
            quadraticTo(center.x - w * 0.4f, bowlBottom, center.x, bowlBottom + h * 0.1f)
            quadraticTo(center.x + w * 0.4f, bowlBottom, center.x + w * 0.35f, bowlTop)
            close()
        }
        drawPath(path = cupPath, color = tint, style = Stroke(width = stroke))

        // Stem & Base
        drawLine(color = tint, start = Offset(center.x, bowlBottom + h * 0.1f), end = Offset(center.x, center.y + h * 0.42f), strokeWidth = stroke * 1.2f)
        drawLine(color = tint, start = Offset(center.x - w * 0.3f, center.y + h * 0.42f), end = Offset(center.x + w * 0.3f, center.y + h * 0.42f), strokeWidth = stroke * 1.5f)
    }
}

@Composable
fun CrownSymbolCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val w = size.width * 0.7f
        val h = size.height * 0.6f
        val stroke = 2.dp.toPx()

        val crownPath = Path().apply {
            moveTo(center.x - w * 0.4f, center.y + h * 0.3f)
            lineTo(center.x - w * 0.45f, center.y - h * 0.2f)
            lineTo(center.x - w * 0.2f, center.y + h * 0.05f)
            lineTo(center.x, center.y - h * 0.4f)
            lineTo(center.x + w * 0.2f, center.y + h * 0.05f)
            lineTo(center.x + w * 0.45f, center.y - h * 0.2f)
            lineTo(center.x + w * 0.4f, center.y + h * 0.3f)
            close()
        }
        drawPath(path = crownPath, color = tint, style = Stroke(width = stroke))
        // Base line
        drawLine(color = tint, start = Offset(center.x - w * 0.4f, center.y + h * 0.3f), end = Offset(center.x + w * 0.4f, center.y + h * 0.3f), strokeWidth = stroke * 1.5f)
        // Cross on top center peak
        drawLine(color = tint, start = Offset(center.x, center.y - h * 0.4f - 6.dp.toPx()), end = Offset(center.x, center.y - h * 0.4f), strokeWidth = stroke)
        drawLine(color = tint, start = Offset(center.x - 3.dp.toPx(), center.y - h * 0.4f - 3.dp.toPx()), end = Offset(center.x + 3.dp.toPx(), center.y - h * 0.4f - 3.dp.toPx()), strokeWidth = stroke)
    }
}

@Composable
fun LiturgicalSunCanvas(
    modifier: Modifier = Modifier,
    tint: Color = GoldBright
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) * 0.25f
        val stroke = 2.dp.toPx()

        drawCircle(color = tint, center = center, radius = radius)

        // 8 Rays
        val rays = 8
        for (i in 0 until rays) {
            val angle = (i * 360f / rays) * (Math.PI / 180f)
            val innerR = radius * 1.3f
            val outerR = radius * 1.9f
            val sx = center.x + innerR * kotlin.math.cos(angle).toFloat()
            val sy = center.y + innerR * kotlin.math.sin(angle).toFloat()
            val ex = center.x + outerR * kotlin.math.cos(angle).toFloat()
            val ey = center.y + outerR * kotlin.math.sin(angle).toFloat()
            drawLine(color = tint, start = Offset(sx, sy), end = Offset(ex, ey), strokeWidth = stroke)
        }
    }
}

@Composable
fun ReflectionGuardDialog(
    duty: SpiritualDuty,
    onConfirmKeep: () -> Unit,
    onRevise: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = GoldBright
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Spiritual Reflection Guard",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "You are adding/editing: \"${duty.title}\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ParchmentLight
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "As a assistant to your soul, Custos Animae gently invites you to reflect:\n\n" +
                            "• Does this task contribute to Christian virtue?\n" +
                            "• Does it fulfill a legitimate duty of your state of life?\n" +
                            "• Does it help draw you or others closer to God?\n\n" +
                            "If this is a necessary daily responsibility, you may keep it under your spiritual rule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmKeep,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldSacred,
                    contentColor = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Color.White else Color.Black
                )
            ) {
                Text("It Fulfills My Duty (Keep)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onRevise,
                border = BorderStroke(1.dp, CrimsonLight),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonLight)
            ) {
                Text("Revise / Discard")
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

fun formatDutySchedule(duty: SpiritualDuty): String {
    val freq = duty.frequency.uppercase()
    val time = duty.reminderTime
    val details = when (freq) {
        "DAILY" -> "Daily"
        "WEEKLY" -> {
            if (duty.scheduledDaysOfWeek.isNotBlank()) {
                val days = duty.scheduledDaysOfWeek.split(",").joinToString(", ") { it.take(3) }
                "Weekly ($days)"
            } else "Weekly"
        }
        "MONTHLY" -> {
            if (duty.scheduledDaysOfMonth.isNotBlank()) {
                val parts = duty.scheduledDaysOfMonth.split(",")
                val formattedParts = parts.map { part ->
                    val trimmed = part.trim()
                    if (trimmed.contains("_")) {
                        val sub = trimmed.split("_")
                        val ordinal = when (sub.getOrNull(0)) {
                            "1ST" -> "1st"
                            "2ND" -> "2nd"
                            "3RD" -> "3rd"
                            "4TH" -> "4th"
                            "LST" -> "Last"
                            else -> "1st"
                        }
                        val day = sub.getOrNull(1) ?: "Day"
                        "$ordinal $day"
                    } else {
                        val num = trimmed.toIntOrNull()
                        if (num != null) {
                            val suffix = when {
                                num % 10 == 1 && num % 100 != 11 -> "st"
                                num % 10 == 2 && num % 100 != 12 -> "nd"
                                num % 10 == 3 && num % 100 != 13 -> "rd"
                                else -> "th"
                            }
                            "$num$suffix"
                        } else trimmed
                    }
                }
                "Monthly on ${formattedParts.joinToString(", ")}"
            } else "Monthly"
        }
        "YEARLY" -> {
            if (duty.scheduledDaysOfYear.isNotBlank()) {
                val parts = duty.scheduledDaysOfYear.split(",")
                val formattedParts = parts.map { part ->
                    val trimmed = part.trim()
                    if (trimmed.contains("_")) {
                        val sub = trimmed.split("_")
                        val ordinal = when (sub.getOrNull(0)) {
                            "1ST" -> "1st"
                            "2ND" -> "2nd"
                            "3RD" -> "3rd"
                            "4TH" -> "4th"
                            "LST" -> "Last"
                            else -> "1st"
                        }
                        val day = sub.getOrNull(1) ?: "Day"
                        val monthNum = sub.getOrNull(2)?.toIntOrNull() ?: 1
                        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                        val mName = monthNames.getOrNull(monthNum - 1) ?: monthNum.toString()
                        "$ordinal $day of $mName"
                    } else {
                        val sub = trimmed.split("-")
                        if (sub.size == 2) {
                            val monthNum = sub[0].toIntOrNull() ?: 1
                            val day = sub[1]
                            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            val mName = monthNames.getOrNull(monthNum - 1) ?: sub[0]
                            "$mName $day"
                        } else trimmed
                    }
                }
                "Yearly on ${formattedParts.joinToString(", ")}"
            } else "Yearly"
        }
        else -> duty.frequency.lowercase(java.util.Locale.ROOT).replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }
    }
    return "$time • $details"
}

@Composable
fun LiturgicalColorAndSeasonWidget(
    info: LiturgicalDayInfo,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Map vestmentColor string to actual Color and descriptive info
    val (vestmentColor, vestmentColorName, vestmentMeaning) = remember(info.vestmentColor) {
        when (info.vestmentColor.lowercase(java.util.Locale.ROOT)) {
            "violet", "purple" -> Triple(
                LiturgicalViolet,
                "VIOLET / PURPLE",
                "Penance, sacrifice, and spiritual preparation. Violet calls us to turn our hearts toward prayer, fasting, and repentance in anticipation of Christ's coming or Passion."
            )
            "white", "gold" -> Triple(
                LiturgicalWhiteGold,
                "WHITE / GOLD",
                "Purity, joy, glory, and the resurrection. Used to celebrate the great feasts of Our Lord, the Blessed Virgin, and the saints who were not martyred, signifying divine triumph."
            )
            "green" -> Triple(
                LiturgicalGreen,
                "GREEN",
                "Spiritual growth, hope, and eternal life. Green represents the growing life of the Church and our soul's journey in faith during the season after Pentecost."
            )
            "red" -> Triple(
                LiturgicalRed,
                "RED",
                "The fire of the Holy Ghost and the blood of martyrs. Red represents divine charity and the ultimate witness of those who shed their blood for Christ and His Church."
            )
            "rose" -> Triple(
                LiturgicalRose,
                "ROSE",
                "Joy and rejoicing in the midst of a penitential season. Used only on Gaudete Sunday (Advent) and Laetare Sunday (Lent) to signify a brief respite and anticipation of joy."
            )
            "black" -> Triple(
                LiturgicalBlack,
                "BLACK",
                "Mourning, sorrow, and repose. Black is used on Good Friday and during Requiem Masses to remind us to pray for the repose of the souls of the faithful departed."
            )
            else -> Triple(
                GoldSacred,
                "SACRED METALLIC GOLD",
                "Universal festive splendor. Gold is a traditional alternative for white or green on high solemnities, symbolizing the glory of Christ the King and the heavenly court."
            )
        }
    }

    // Map season to detailed traditional 1954 context
    val (seasonName, seasonLatin, seasonFocus, seasonDuty) = remember(info.season) {
        when (info.season) {
            com.example.data.calendar.LiturgicalSeason.ADVENT -> Quadruple(
                "Season of Advent",
                "Adventus Domini",
                "Preparation for Christ's Birth & Second Coming",
                "Quiet anticipation, prayerful longing, and minor fasting/abstinence."
            )
            com.example.data.calendar.LiturgicalSeason.CHRISTMASTIDE -> Quadruple(
                "Christmastide & Epiphanytide",
                "Nativitas et Epiphania Domini",
                "Adoration of the Incarnate Word & Manifestation",
                "Holy joy, adoration at the Crèche, and family celebrations."
            )
            com.example.data.calendar.LiturgicalSeason.SEPTUAGESIMA -> Quadruple(
                "Septuagesima (Pre-Lent)",
                "Septuagesima",
                "Preparation for the Lenten Combat",
                "Ceasing of festive chants (Alleluia is buried), preparing the mind for self-denial."
            )
            com.example.data.calendar.LiturgicalSeason.LENT -> Quadruple(
                "Season of Lent",
                "Quadragesima",
                "Forty Days of Penance & Mortification",
                "Daily fasting, abstinence from meat, daily examinations of conscience, almsgiving."
            )
            com.example.data.calendar.LiturgicalSeason.PASSIONTIDE -> Quadruple(
                "Passiontide",
                "Tempus Passionis",
                "The Holy Passion & Sacrifice of the Cross",
                "Veiling of statues and crucifixes, deep contemplation on Christ's wounds and sorrows."
            )
            com.example.data.calendar.LiturgicalSeason.PASCHALTIDE -> Quadruple(
                "Paschaltide (Eastertide)",
                "Tempus Paschale",
                "The Triumph of the Resurrection & Holy Ghost",
                "Joyful singing of Alleluia, celebrating the Ascension, and Pentecost Novena."
            )
            com.example.data.calendar.LiturgicalSeason.PENTECOST_SEASON -> Quadruple(
                "Time after Pentecost",
                "Tempus per Annum (Pentecoste)",
                "Spiritual Growth, Apostolic Zeal & Virtue",
                "Fostering daily virtues, holy charity, study of theology, spiritual combat."
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("liturgical_color_and_season_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.2.dp, GoldSacred.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Widget Main Header Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CrimsonBurgundy.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "☩",
                            color = GoldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1954 LITURGICAL YEAR STATUS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = GoldBright
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CrimsonBurgundy,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "TRADITIONAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ParchmentLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Central Banner Content: Left Stained-Glass Shield, Right Season & Color Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Visual Element: A glowing Gothic Arch depicting the Liturgical Color & Season
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 115.dp)
                        .clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    vestmentColor.copy(alpha = 0.95f),
                                    vestmentColor.copy(alpha = 0.4f),
                                    Color(0xFF10141D)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.5.dp,
                                Brush.linearGradient(
                                    colors = listOf(GoldBright, GoldSacred.copy(alpha = 0.4f), GoldBright)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Stained Glass tracery lines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        // Lead line arches
                        drawPath(
                            path = Path().apply {
                                moveTo(0f, h * 0.4f)
                                cubicTo(w * 0.25f, h * 0.1f, w * 0.75f, h * 0.1f, w, h * 0.4f)
                            },
                            color = Color(0xFF12141A).copy(alpha = 0.6f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        // Diagonal lead lines
                        drawLine(
                            color = Color(0xFF12141A).copy(alpha = 0.6f),
                            start = Offset(0f, h),
                            end = Offset(w, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF12141A).copy(alpha = 0.6f),
                            start = Offset(w, h),
                            end = Offset(0f, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // A golden lit cross centered in the stained glass
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "☩",
                            fontSize = 32.sp,
                            color = if (vestmentColor == LiturgicalWhiteGold) Color(0xFF6B4C00) else GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = info.vestmentColor.uppercase(),
                            fontSize = 9.sp,
                            color = if (vestmentColor == LiturgicalWhiteGold) Color.Black else ParchmentLight,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Info Section
                Column(modifier = Modifier.weight(1f)) {
                    // Season Header
                    Text(
                        text = seasonName.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = GoldBright
                    )

                    Text(
                        text = "Latin: $seasonLatin",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextMutedParchment
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Color indicator row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(vestmentColor, CircleShape)
                                .border(0.5.dp, GoldBright, CircleShape)
                        )
                        Text(
                            text = "COLOR: $vestmentColorName",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.sp
                            ),
                            color = TextGoldHighlight
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Brief Focus text
                    Text(
                        text = "Focus: $seasonFocus",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentLight,
                        lineHeight = 15.sp
                    )
                }
            }

            // Expandable Traditional Color Guide
            HorizontalDivider(color = GoldSacred.copy(alpha = 0.2f), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🎨", fontSize = 14.sp)
                    Text(
                        text = "Traditional Liturgical Color Meanings & Spiritual Guide",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextMutedParchment
                    )
                }
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = GoldBright,
                    fontSize = 11.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF12161E))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Current Season detailed rule
                    Column {
                        Text(
                            text = "CURRENT SEASON DETAIL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldBright,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "For $seasonName ($seasonLatin), traditional Catholic practice calls for: $seasonDuty",
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentLight,
                            lineHeight = 15.sp
                        )
                    }

                    HorizontalDivider(color = GoldSacred.copy(alpha = 0.15f), thickness = 0.5.dp)

                    // Current Color detailed rule
                    Column {
                        Text(
                            text = "LITURGICAL COLOR SYMBOLISM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldBright,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = vestmentMeaning,
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentLight,
                            lineHeight = 15.sp
                        )
                    }

                    HorizontalDivider(color = GoldSacred.copy(alpha = 0.15f), thickness = 0.5.dp)

                    // 1954 Rubrics note
                    Text(
                        text = "☩ Calculated according to the 1954 Rubrics under the reforms of Pope Pius XII, representing the timeless rhythm of the traditional Roman Rite.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = TextMutedParchment.copy(alpha = 0.7f),
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)


