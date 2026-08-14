package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.IlluminatedDivider
import com.example.ui.components.TrackScrollState
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.ui.theme.*

data class SocialLinkItem(
    val id: String,
    val title: String,
    val handleOrSubtitle: String,
    val url: String,
    val icon: ImageVector,
    val buttonLabel: String,
    val badge: String,
    val accentColor: Color,
    val testTag: String
)

@Composable
fun RegulaVitaeScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
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

    val socialLinks = listOf(
        SocialLinkItem(
            id = "website",
            title = "Official Website",
            handleOrSubtitle = "regulavitae.vercel.app",
            url = "https://regulavitae.vercel.app/",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            buttonLabel = "Visit Website 🌐",
            badge = "Official Portal",
            accentColor = GoldBright,
            testTag = "social_link_website"
        ),
        SocialLinkItem(
            id = "whatsapp",
            title = "WhatsApp Channel",
            handleOrSubtitle = "RegulaVitae Community",
            url = "https://whatsapp.com/channel/0029VbCj8TYKGGGDxtDcrc0h",
            icon = Icons.Default.Notifications,
            buttonLabel = "Join WhatsApp Channel 💬",
            badge = "Daily Alerts",
            accentColor = Color(0xFF25D366),
            testTag = "social_link_whatsapp"
        ),
        SocialLinkItem(
            id = "instagram",
            title = "Instagram",
            handleOrSubtitle = "@regulavitae",
            url = "https://www.instagram.com/regulavitae/#",
            icon = Icons.Default.CameraAlt,
            buttonLabel = "Follow on Instagram 📸",
            badge = "@regulavitae",
            accentColor = Color(0xFFE1306C),
            testTag = "social_link_instagram"
        ),
        SocialLinkItem(
            id = "youtube",
            title = "YouTube Channel",
            handleOrSubtitle = "@regulavitae",
            url = "https://www.youtube.com/@regulavitae",
            icon = Icons.Default.PlayArrow,
            buttonLabel = "Subscribe on YouTube ▶️",
            badge = "Video Catechesis",
            accentColor = Color(0xFFFF0000),
            testTag = "social_link_youtube"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 640.dp)
            .padding(16.dp)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isTopHeaderVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GothicHeader(
                    title = "REGULA VITAE",
                    subtitle = "My Creator Page • Community & Socials",
                    badgeText = "My Work"
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Developer & Mission Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("regulavitae_mission_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(GoldBright, GoldSacred, CrimsonBurgundy, GoldBright)
                )
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(CrimsonBurgundy, DarkCathedralSurfaceVariant)
                                ),
                                CircleShape
                            )
                            .border(1.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Church,
                            contentDescription = "RegulaVitae Cross",
                            tint = GoldBright,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REGULA VITAE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = GoldBright
                        )
                        Text(
                            text = "My Personal Work Title & Spiritual Rule",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "I created Custos Animae under my work title, RegulaVitae, to help us be accountable to the spiritual discipline that God has entrusted us to live out as faithfuls. I compiled these quotes, Saints, and their prayers to strengthen our faith so that we may be more disciplined with the help of their intercessions. This is an app that you can use in order to start being spiritually disciplined for God.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 20.sp
                    ),
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CrimsonBurgundy.copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, GoldSacred.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Follow our official channels to stay connected and grow in holiness.",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = GoldLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Social Channels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = GoldBright,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "OFFICIAL SOCIAL CHANNELS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldBright
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Social Links Cards
        socialLinks.forEach { item ->
            SocialCard(
                item = item,
                onOpenUrl = { openUrlInBrowser(context, item.url) },
                onCopyUrl = { copyToClipboard(context, item.url, item.title) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Share App Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("share_regulavitae_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share with Friends & Family",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Help spread the traditional Catholic faith",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val shareText = """Discover RegulaVitae & Custos Animae — Traditional Catholic Spiritual Rule of Life, 1954 Liturgical Calendar, Offline Prayers, and Monastic Discipline.

Website: https://regulavitae.vercel.app/
WhatsApp: https://whatsapp.com/channel/0029VbCj8TYKGGGDxtDcrc0h
Instagram: https://www.instagram.com/regulavitae/#
YouTube: https://www.youtube.com/@regulavitae"""
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share RegulaVitae")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_app_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonBurgundy,
                        contentColor = ParchmentLight
                    ),
                    border = BorderStroke(1.dp, GoldSacred),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share RegulaVitae Links",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Spiritual Patronage & Motto
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "“Ora et Labora • Vivat Christus Rex”",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldBright,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dedicated to the Sacred Heart of Jesus and the Immaculate Heart of Mary.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedParchment,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun SocialCard(
    item: SocialLinkItem,
    onOpenUrl: () -> Unit,
    onCopyUrl: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(item.testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(item.accentColor.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, item.accentColor.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                        Text(
                            text = item.handleOrSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = item.accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, item.accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = item.accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selectable / Visible Link text
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = DarkCathedralBackground,
                border = BorderStroke(0.5.dp, DarkCathedralCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyUrl() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.url,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = GoldLight,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Link",
                        tint = TextMutedParchment,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenUrl,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("${item.testTag}_open_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldSacred,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.buttonLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onCopyUrl,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${item.testTag}_copy_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldBright
                    ),
                    border = BorderStroke(1.dp, GoldDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun openUrlInBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        copyToClipboard(context, url, "Link")
        Toast.makeText(context, "Link copied to clipboard: $url", Toast.LENGTH_LONG).show()
    }
}

private fun copyToClipboard(context: Context, url: String, label: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Copied: $url", Toast.LENGTH_SHORT).show()
    }
}
