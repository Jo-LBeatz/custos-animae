package com.example.ui.screens

import androidx.compose.material.icons.filled.ArrowBack
import android.os.Build
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.PageViewMode
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper
import com.example.util.PrayerMode
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import com.example.ui.components.ScrollableClockPicker
import com.example.ui.components.ScrollableClockField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import kotlinx.coroutines.launch
import com.example.ui.MainViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GothicHeader
import com.example.ui.theme.*
import com.example.util.CalendarSyncHelper
import com.example.util.PhoneCalendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

import com.example.ui.components.TrackScrollState
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val duties by viewModel.allDuties.collectAsStateWithLifecycle()
    val customRingtoneUri by viewModel.customRingtoneUri.collectAsStateWithLifecycle()

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.setCustomRingtoneUri(uri.toString())
            Toast.makeText(context, "Dedicated call ringtone updated!", Toast.LENGTH_SHORT).show()
        }
    }

    var showSyncCalendarDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var showBackupPasswordDialog by remember { mutableStateOf(false) }
    var showRestorePasswordDialog by remember { mutableStateOf(false) }
    var backupPasswordInput by remember { mutableStateOf("") }
    var restoreFileUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isBackupOperationRunning by remember { mutableStateOf(false) }

    val exportDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && backupPasswordInput.isNotBlank()) {
            isBackupOperationRunning = true
            coroutineScope.launch {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val result = viewModel.exportEncryptedBackup(backupPasswordInput, outputStream)
                        when (result) {
                            is com.example.util.BackupResult.Success -> {
                                Toast.makeText(context, "Backup Created Successfully!\n${result.summary}", Toast.LENGTH_LONG).show()
                            }
                            is com.example.util.BackupResult.Error -> {
                                Toast.makeText(context, "Backup Error: ${result.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Could not open storage location.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Export Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally {
                    isBackupOperationRunning = false
                    backupPasswordInput = ""
                }
            }
        }
    }

    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            restoreFileUri = uri
            showRestorePasswordDialog = true
        }
    }

    var showExportExamPdfDialog by remember { mutableStateOf(false) }
    var pdfExportPasswordInput by remember { mutableStateOf<String?>(null) }

    val exportExamPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            isBackupOperationRunning = true
            coroutineScope.launch {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val resultMsg = viewModel.exportExaminationsToPdf(pdfExportPasswordInput, outputStream)
                        Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Could not open storage location.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "PDF Export Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally {
                    isBackupOperationRunning = false
                    pdfExportPasswordInput = null
                }
            }
        }
    }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[android.Manifest.permission.READ_CALENDAR] == true
        val writeGranted = permissions[android.Manifest.permission.WRITE_CALENDAR] == true
        if (readGranted && writeGranted) {
            showSyncCalendarDialog = true
        } else {
            Toast.makeText(context, "Calendar permissions are required for sync.", Toast.LENGTH_SHORT).show()
        }
    }

    var morningTimeInput by remember(profile?.morningPrayerTime) {
        mutableStateOf(profile?.morningPrayerTime ?: "07:00")
    }
    var eveningTimeInput by remember(profile?.eveningPrayerTime) {
        mutableStateOf(profile?.eveningPrayerTime ?: "21:00")
    }
    var saintTimeInput by remember(profile?.saintNotificationTime) {
        mutableStateOf(profile?.saintNotificationTime ?: "07:30")
    }
    var angelusMorningInput by remember(profile?.angelusMorningTime) {
        mutableStateOf(profile?.angelusMorningTime ?: "06:00")
    }
    var angelusNoonInput by remember(profile?.angelusNoonTime) {
        mutableStateOf(profile?.angelusNoonTime ?: "12:00")
    }
    var angelusEveningInput by remember(profile?.angelusEveningTime) {
        mutableStateOf(profile?.angelusEveningTime ?: "18:00")
    }
    var angelusEnabled by remember(profile?.angelusEnabled) {
        mutableStateOf(profile?.angelusEnabled ?: true)
    }
    var notificationsEnabled by remember(profile?.notificationEnabled) {
        mutableStateOf(profile?.notificationEnabled ?: true)
    }
    var sacredHeartTimeInput by remember(profile?.sacredHeartTime) {
        mutableStateOf(profile?.sacredHeartTime ?: "08:00")
    }
    var sacredHeartEnabled by remember(profile?.sacredHeartEnabled) {
        mutableStateOf(profile?.sacredHeartEnabled ?: true)
    }
    var immaculateHeartTimeInput by remember(profile?.immaculateHeartTime) {
        mutableStateOf(profile?.immaculateHeartTime ?: "08:00")
    }
    var immaculateHeartEnabled by remember(profile?.immaculateHeartEnabled) {
        mutableStateOf(profile?.immaculateHeartEnabled ?: true)
    }

    var liturgicalSeasonEnabled by remember(profile?.liturgicalSeasonNotificationsEnabled) {
        mutableStateOf(profile?.liturgicalSeasonNotificationsEnabled ?: true)
    }

    var isOptimizedIgnored by remember {
        mutableStateOf(isBatteryOptimizationIgnored(context))
    }

    var isExactAlarmGranted by remember {
        mutableStateOf(checkExactAlarmPermission(context))
    }

    var isOverlayGranted by remember {
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.provider.Settings.canDrawOverlays(context)
        } else {
            true
        })
    }

    LaunchedEffect(Unit) {
        while (true) {
            isOptimizedIgnored = isBatteryOptimizationIgnored(context)
            isExactAlarmGranted = checkExactAlarmPermission(context)
            isOverlayGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.provider.Settings.canDrawOverlays(context)
            } else {
                true
            }
            kotlinx.coroutines.delay(2000)
        }
    }

    var showTimeEditDialog by remember { mutableStateOf<String?>(null) }
    var tempTimeValue by remember { mutableStateOf("") }

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
                    title = "MONASTERY SETTINGS",
                    subtitle = "Notification Queries & Spiritual Discipline Configuration",
                    badgeText = "Settings"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Spiritual State Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Spiritual Rule Identity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (profile?.gender == "MALE") "Rule of St. Benedict (Regula Benedicti)" else "Rule of Donatus (Regula Donati)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liturgical Calendar Offline Database Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calendar_database_card"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = Color(0xFF4CAF50),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Liturgical Calendar Database Status",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E2D1E),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Room Storage Active • 1954 Calendar Synced",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFA5D6A7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The local Room database is active and populated with the complete 1954 General Roman Calendar rubrics and fasts, stored entirely offline on your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )
            }
        }



        // Daily Canonical Prayer & Angelus Notification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_prayer_notifications_card"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = GoldBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Prayer & Angelus Alarms",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                    }

                    // Master Toggle
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            notificationsEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                enabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                angelusEnabled,
                                sacredHeartTimeInput,
                                sacredHeartEnabled,
                                immaculateHeartTimeInput,
                                immaculateHeartEnabled, liturgicalSeasonEnabled)
                            Toast.makeText(context, "System prayer alarms ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy),
                        modifier = Modifier.testTag("master_notifications_switch")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Configure exact system notification times for daily prayers, Angelus, and saint commemorations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Angelus Section Header & Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "The Angelus / Regina Caeli Alarms",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
                            color = GoldBright
                        )
                        Text(
                            text = "Traditional 6:00 AM, 12:00 PM (Noon), & 6:00 PM calls to prayer",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                    Switch(
                        checked = angelusEnabled,
                        onCheckedChange = { enabled ->
                            angelusEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                notificationsEnabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                enabled,
                                sacredHeartTimeInput,
                                sacredHeartEnabled,
                                immaculateHeartTimeInput,
                                immaculateHeartEnabled, liturgicalSeasonEnabled)
                            Toast.makeText(context, "Angelus alarms ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy),
                        modifier = Modifier.testTag("angelus_toggle")
                    )
                }

                if (angelusEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    NotificationTimeRow(
                        title = "Angelus - Morning",
                        subtitle = "Morning call to prayer",
                        time = angelusMorningInput,
                        onClick = {
                            tempTimeValue = angelusMorningInput
                            showTimeEditDialog = "ANGELUS_MORNING"
                        },
                        onTestClick = {
                            viewModel.testNotificationNow(
                                "The Angelus (Morning)",
                                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost."
                            )
                            Toast.makeText(context, "Fired test Angelus notification!", Toast.LENGTH_SHORT).show()
                        },
                        testTagPrefix = "angelus_morning"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = DarkCathedralCardBorder)

                    NotificationTimeRow(
                        title = "Angelus - Noon (12:00 PM)",
                        subtitle = "Midday call to prayer",
                        time = angelusNoonInput,
                        onClick = {
                            tempTimeValue = angelusNoonInput
                            showTimeEditDialog = "ANGELUS_NOON"
                        },
                        onTestClick = {
                            viewModel.testNotificationNow(
                                "The Angelus (Noon)",
                                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost."
                            )
                            Toast.makeText(context, "Fired test Angelus notification!", Toast.LENGTH_SHORT).show()
                        },
                        testTagPrefix = "angelus_noon"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = DarkCathedralCardBorder)

                    NotificationTimeRow(
                        title = "Angelus - Evening (6:00 PM)",
                        subtitle = "Evening call to prayer",
                        time = angelusEveningInput,
                        onClick = {
                            tempTimeValue = angelusEveningInput
                            showTimeEditDialog = "ANGELUS_EVENING"
                        },
                        onTestClick = {
                            viewModel.testNotificationNow(
                                "The Angelus (Evening)",
                                "The Angel of the Lord declared unto Mary. R. And she conceived of the Holy Ghost."
                            )
                            Toast.makeText(context, "Fired test Angelus notification!", Toast.LENGTH_SHORT).show()
                        },
                        testTagPrefix = "angelus_evening"
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarkCathedralCardBorder)

                // Morning Prayer
                NotificationTimeRow(
                    title = "Morning Prayer",
                    subtitle = "Morning offering and daily prayers",
                    time = morningTimeInput,
                    onClick = {
                        tempTimeValue = morningTimeInput
                        showTimeEditDialog = "MORNING"
                    },
                    onTestClick = {
                        viewModel.testNotificationNow(
                            "Morning Prayer Reminder",
                            "Time for your morning prayer and daily offering to Almighty God."
                        )
                        Toast.makeText(context, "Fired test Morning Prayer notification!", Toast.LENGTH_SHORT).show()
                    },
                    testTagPrefix = "morning_prayer"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = DarkCathedralCardBorder)

                // Saint of the Day
                NotificationTimeRow(
                    title = "Saint of the Day Commemoration",
                    subtitle = "Daily saint bio, quote, and feast alert",
                    time = saintTimeInput,
                    onClick = {
                        tempTimeValue = saintTimeInput
                        showTimeEditDialog = "SAINT"
                    },
                    onTestClick = {
                        com.example.notifications.NotificationScheduler.triggerSaintNotificationNow(context)
                        Toast.makeText(context, "Fired test Saint of the Day notification!", Toast.LENGTH_SHORT).show()
                    },
                    testTagPrefix = "saint_day"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = DarkCathedralCardBorder)

                // Evening Prayer
                NotificationTimeRow(
                    title = "Evening Prayer / Night Examen",
                    subtitle = "Evening Rosary & Examination of Conscience",
                    time = eveningTimeInput,
                    onClick = {
                        tempTimeValue = eveningTimeInput
                        showTimeEditDialog = "EVENING"
                    },
                    onTestClick = {
                        viewModel.testNotificationNow(
                            "Evening Prayer / Night Examen",
                            "Time for your evening Rosary and Examen of Conscience before sleep."
                        )
                        Toast.makeText(context, "Fired test Evening Prayer notification!", Toast.LENGTH_SHORT).show()
                    },
                    testTagPrefix = "evening_prayer"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rule of Life Reminder Style Card
        val ruleStyle = profile?.ruleNotificationStyle ?: "CALL_OVERLAY"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rule_notification_style_settings_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (ruleStyle == "CALL_OVERLAY") Icons.Default.Call else Icons.Default.Notifications,
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rule of Life Reminder Style",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select how scheduled Rule of Life duties alert you. Call overlay and banner notifications are strictly mutually exclusive.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Call Overlay Option
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateRuleNotificationStyle("CALL_OVERLAY") }
                            .testTag("style_call_overlay_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (ruleStyle == "CALL_OVERLAY") CrimsonBurgundy.copy(alpha = 0.45f) else DarkCathedralSurface
                        ),
                        border = BorderStroke(
                            if (ruleStyle == "CALL_OVERLAY") 1.5.dp else 1.dp,
                            if (ruleStyle == "CALL_OVERLAY") GoldBright else TextMutedParchment.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = if (ruleStyle == "CALL_OVERLAY") GoldBright else TextMutedParchment,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Call Overlay 📞",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (ruleStyle == "CALL_OVERLAY") GoldBright else ParchmentLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Full-screen ringtone",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Banner Option
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateRuleNotificationStyle("BANNER") }
                            .testTag("style_banner_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (ruleStyle == "BANNER") CrimsonBurgundy.copy(alpha = 0.45f) else DarkCathedralSurface
                        ),
                        border = BorderStroke(
                            if (ruleStyle == "BANNER") 1.5.dp else 1.dp,
                            if (ruleStyle == "BANNER") GoldBright else TextMutedParchment.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (ruleStyle == "BANNER") GoldBright else TextMutedParchment,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "System Banner 🔔",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (ruleStyle == "BANNER") GoldBright else ParchmentLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Heads-up banner",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test Triggers Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            com.example.notifications.NotificationScheduler.triggerDutyNotificationNow(
                                context,
                                title = "Test Rule: Angelus",
                                message = "The Angel of the Lord declared unto Mary. And she conceived of the Holy Spirit.",
                                style = "CALL_OVERLAY"
                            )
                            android.widget.Toast.makeText(context, "Triggering Call Overlay alert...", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_call_overlay_button"),
                        border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            "Test Call 📞",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            com.example.notifications.NotificationScheduler.triggerDutyNotificationNow(
                                context,
                                title = "Test Rule: Rosary",
                                message = "Time for your daily Holy Rosary devotion.",
                                style = "BANNER"
                            )
                            android.widget.Toast.makeText(context, "Triggering System Banner notification...", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_banner_button"),
                        border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            "Test Banner 🔔",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sacred Heart & Immaculate Heart Devotion Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("heart_devotions_notifications_card"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = GoldBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sacred & Immaculate Heart Devotions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Weekly and First Friday/Saturday system alarms to help you fulfill traditional devotions dedicated to the Sacred Heart of Jesus and Immaculate Heart of Mary.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Sacred Heart Devotion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sacred Heart of Jesus Alarms",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
                            color = GoldBright
                        )
                        Text(
                            text = "Scheduled on every Friday and First Fridays",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                    Switch(
                        checked = sacredHeartEnabled,
                        onCheckedChange = { enabled ->
                            sacredHeartEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                notificationsEnabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                angelusEnabled,
                                sacredHeartTimeInput,
                                enabled,
                                immaculateHeartTimeInput,
                                immaculateHeartEnabled, liturgicalSeasonEnabled)
                            Toast.makeText(context, "Sacred Heart alarms ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy),
                        modifier = Modifier.testTag("sacred_heart_toggle")
                    )
                }

                if (sacredHeartEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    NotificationTimeRow(
                        title = "Sacred Heart - Fridays",
                        subtitle = "Friday reminder at preferred hour",
                        time = sacredHeartTimeInput,
                        onClick = {
                            tempTimeValue = sacredHeartTimeInput
                            showTimeEditDialog = "SACRED_HEART"
                        },
                        onTestClick = {
                            val cal = Calendar.getInstance()
                            val isFirstFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
                            val title = if (isFirstFriday) "First Friday Devotion: Sacred Heart of Jesus" else "Friday Devotion: Sacred Heart of Jesus"
                            val message = if (isFirstFriday) {
                                "Offer your Holy Communion, prayers, and acts of reparation to the Sacred Heart of Jesus today."
                            } else {
                                "In honor of Christ's Passion, dedicate today's prayers and mortifications to His Most Sacred Heart."
                            }
                            viewModel.testNotificationNow(title, message)
                            Toast.makeText(context, "Fired test Sacred Heart notification!", Toast.LENGTH_SHORT).show()
                        },
                        testTagPrefix = "sacred_heart"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Immaculate Heart Devotion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Immaculate Heart of Mary Alarms",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
                            color = GoldBright
                        )
                        Text(
                            text = "Scheduled on every Saturday and First Saturdays",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                    }
                    Switch(
                        checked = immaculateHeartEnabled,
                        onCheckedChange = { enabled ->
                            immaculateHeartEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                notificationsEnabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                angelusEnabled,
                                sacredHeartTimeInput,
                                sacredHeartEnabled,
                                immaculateHeartTimeInput,
                                enabled
                            )
                            Toast.makeText(context, "Immaculate Heart alarms ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy),
                        modifier = Modifier.testTag("immaculate_heart_toggle")
                    )
                }

                if (immaculateHeartEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    NotificationTimeRow(
                        title = "Immaculate Heart - Saturdays",
                        subtitle = "Saturday reminder at preferred hour",
                        time = immaculateHeartTimeInput,
                        onClick = {
                            tempTimeValue = immaculateHeartTimeInput
                            showTimeEditDialog = "IMMACULATE_HEART"
                        },
                        onTestClick = {
                            val cal = Calendar.getInstance()
                            val isFirstSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.DAY_OF_MONTH) <= 7
                            val title = if (isFirstSaturday) "First Saturday Devotion: Immaculate Heart of Mary" else "Saturday Devotion: Immaculate Heart of Mary"
                            val message = if (isFirstSaturday) {
                                "Make reparation to the Immaculate Heart of Mary with the Rosary, meditation, and confession."
                            } else {
                                "Dedicate today's prayers, Rosary, and offerings in loving reparation to the Immaculate Heart of Mary."
                            }
                            viewModel.testNotificationNow(title, message)
                            Toast.makeText(context, "Fired test Immaculate Heart notification!", Toast.LENGTH_SHORT).show()
                        },
                        testTagPrefix = "immaculate_heart"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liturgical Season Notifications Card
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Liturgical Seasons",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                    }
                    Switch(
                        checked = liturgicalSeasonEnabled,
                        onCheckedChange = { enabled ->
                            liturgicalSeasonEnabled = enabled
                            viewModel.updateNotificationPreferences(
                                morningTimeInput,
                                eveningTimeInput,
                                saintTimeInput,
                                notificationsEnabled,
                                angelusMorningInput,
                                angelusNoonInput,
                                angelusEveningInput,
                                angelusEnabled,
                                sacredHeartTimeInput,
                                sacredHeartEnabled,
                                immaculateHeartTimeInput,
                                immaculateHeartEnabled,
                                enabled
                            )
                            Toast.makeText(context, "Liturgical season notifications ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = CrimsonBurgundy)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Automated announcements at 8:00 AM on the first day of major traditional liturgical seasons (e.g., Advent, Lent, Paschaltide).",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        // Background Execution Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("background_execution_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = GoldBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Background Execution & Exact Alarms",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ensure timely rule alarms when your device is locked or in Doze mode by granting unrestricted battery and exact alarm permissions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Battery Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Battery Optimization",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Text(
                            text = if (isOptimizedIgnored) "Unrestricted (Reliable background delivery)" else "Optimized (OEM may suppress exact triggers)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOptimizedIgnored) GoldBright else TextMutedParchment
                        )
                    }
                    if (!isOptimizedIgnored) {
                        TextButton(
                            onClick = {
                                requestIgnoreBatteryOptimization(context)
                                isOptimizedIgnored = isBatteryOptimizationIgnored(context)
                            }
                        ) {
                            Text("Fix", color = GoldBright, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Exact Alarm Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Exact Alarm Permission",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Text(
                            text = if (isExactAlarmGranted) "Granted (Alarms dispatch to the exact second)" else "Restricted (Alarms may be batched by OS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isExactAlarmGranted) GoldBright else TextMutedParchment
                        )
                    }
                    if (!isExactAlarmGranted) {
                        TextButton(
                            onClick = {
                                requestExactAlarmPermission(context)
                                isExactAlarmGranted = checkExactAlarmPermission(context)
                            }
                        ) {
                            Text("Grant", color = GoldBright, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (!isOptimizedIgnored || !isExactAlarmGranted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            if (!isOptimizedIgnored) requestIgnoreBatteryOptimization(context)
                            if (!isExactAlarmGranted) requestExactAlarmPermission(context)
                        },
                        border = BorderStroke(1.dp, GoldSacred),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant All Background Permissions", color = GoldBright, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prayer Perspective Mode Selector Card
        val activePrayerMode = PrayerMode.fromCode(profile?.prayerMode)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("prayer_mode_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (activePrayerMode) {
                            PrayerMode.INDIVIDUAL -> Icons.Default.Person
                            PrayerMode.GROUP -> Icons.Default.Groups
                            PrayerMode.TRADITIONAL -> Icons.AutoMirrored.Filled.MenuBook
                        },
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prayer Perspective Mode",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select whether you are praying individually or in a group. Individual mode formats Saint intercessions and devotions into first-person language (I / me / my). Group mode preserves traditional plural language (We / us / our):",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Spacer(modifier = Modifier.height(12.dp))

                PrayerMode.entries.forEach { mode ->
                    val isSelected = activePrayerMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updatePrayerMode(mode) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.updatePrayerMode(mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldBright)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${mode.label}  (${mode.badgeText})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) GoldBright else ParchmentLight,
                                softWrap = true
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment,
                                softWrap = true
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Visibility Preference Card
        val pageViewMode by viewModel.pageViewMode.collectAsStateWithLifecycle()
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = GoldBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Page Viewing Preference",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure whether top headers and bottom navigation elements are displayed or hidden for pure content visibility across ALL PAGES:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
                Spacer(modifier = Modifier.height(12.dp))

                val pageViewOptions = listOf(
                    Triple(PageViewMode.PURE_CONTENT, "Pure Visibility (Without Top & Bottom Elements)", "Hides top header and bottom navigation bar completely for 100% pure, uninterrupted content space on all pages."),
                    Triple(PageViewMode.STANDARD_WITH_BARS, "Standard View (With Top & Bottom Elements)", "Keeps top header title and bottom navigation bar visible at all times on all pages."),
                    Triple(PageViewMode.DYNAMIC_AUTO_HIDE, "Dynamic Auto-Hide", "Fades out top & bottom bars on scroll down, restores them on scroll up or tap.")
                )

                pageViewOptions.forEach { (mode, title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setPageViewMode(mode) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (pageViewMode == mode),
                            onClick = { viewModel.setPageViewMode(mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldBright)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldBright, softWrap = true)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextMutedParchment, softWrap = true)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.openPageVisibilityPrompt() },
                    border = BorderStroke(1.dp, GoldSacred),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Page Viewing Preference Prompt", color = GoldBright)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Over Other Apps (Overlay) Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = GoldBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Display Over Other Apps",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isOverlayGranted) "Status: Allowed (Incoming call screens can launch in full-screen immediately)" else "Status: Not Allowed (System will use heads-up banners instead, but will still ring and vibrate)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverlayGranted) GoldBright else TextMutedParchment
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                val intent = Intent(
                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    android.net.Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                context.startActivity(intent)
                            }
                        }
                    },
                    border = BorderStroke(1.dp, GoldSacred),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Full-Screen Call Permission", color = GoldBright, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dedicated Call Ringtone Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = GoldBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rule of Life Dedicated Ringtone",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val activeRingtoneUri = customRingtoneUri
                val ringtoneDisplayName = remember(activeRingtoneUri) {
                    if (activeRingtoneUri.isNullOrEmpty()) {
                        "Cathedral Church Bell (Default)"
                    } else {
                        try {
                            val uri = Uri.parse(activeRingtoneUri)
                            val lastSegment = uri.lastPathSegment ?: activeRingtoneUri
                            "Custom Audio: $lastSegment"
                        } catch (e: Exception) {
                            "Custom Audio Selected"
                        }
                    }
                }
                Text(
                    text = "Selected Ringtone: $ringtoneDisplayName",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (activeRingtoneUri.isNullOrEmpty()) TextMutedParchment else GoldBright
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            ringtonePickerLauncher.launch(arrayOf("audio/*"))
                        },
                        border = BorderStroke(1.dp, GoldSacred),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select Custom Audio", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    if (!activeRingtoneUri.isNullOrEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.setCustomRingtoneUri(null)
                                Toast.makeText(context, "Reset to default church bell ringtone", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, LiturgicalRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset Default", color = LiturgicalRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Calendar Integration Sync Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Device Calendar Sync",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright
                    )
                    Text(
                        text = "Clone all existing tasks/devotions to your native phone calendar with reminders.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedParchment
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val hasRead = androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_CALENDAR
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        val hasWrite = androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.WRITE_CALENDAR
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        
                        if (hasRead && hasWrite) {
                            showSyncCalendarDialog = true
                        } else {
                            calendarPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.READ_CALENDAR,
                                    android.Manifest.permission.WRITE_CALENDAR
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = StaticGoldBright),
                    border = BorderStroke(1.dp, GoldSacred),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("sync_calendar_btn")
                ) {
                    Text("Sync Now", fontSize = 11.sp, color = StaticGoldBright, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        if (showSyncCalendarDialog) {
            val phoneCalendars = remember { CalendarSyncHelper.getPhoneCalendars(context) }
            PhoneCalendarSyncDialog(
                calendars = phoneCalendars,
                onSelectAndSync = { calId ->
                    showSyncCalendarDialog = false
                    // We need access to duties here. I'll need to fetch them from viewModel.
                    // Let's assume viewModel has them.
                    // Wait, viewModel needs duties.
                    // Let's check viewModel in SettingsScreen.
                    // I will need to pass duties to SettingsScreen or add a method to MainViewModel to sync.
                    // For now, I'll assume I can just use a method in viewModel.
                    // Or I can add a method to viewModel to trigger the sync.
                    viewModel.syncDutiesToCalendar(context, calId)
                },
                onDismiss = { showSyncCalendarDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reading Preferences Card
        val readingTextSize by viewModel.readingTextSize.collectAsStateWithLifecycle()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reading_preferences_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reading Text Size Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                             fontFamily = FontFamily.Serif,
                             fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Adjust the typography size for reading prayers and spiritual texts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Text Size Scale",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldBright
                    )
                    Text(
                        text = "${readingTextSize.toInt()} sp",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = readingTextSize,
                    onValueChange = { size ->
                        viewModel.updateReadingTextSize(size)
                    },
                    valueRange = 12f..24f,
                    steps = 5, // Allows exact steps: 12, 14, 16, 18, 20, 22, 24
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = CrimsonBurgundy,
                        inactiveTrackColor = DarkCathedralCardBorder
                    ),
                    modifier = Modifier.testTag("reading_text_size_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "LITURGICAL PREVIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Preview Container showing dual-language or single text styled with selected size
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCathedralBackground.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(1.dp, DarkCathedralCardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Dominus vobiscum. Et cum spiritu tuo.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = readingTextSize.sp,
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = ParchmentLight.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The Lord be with you. And with thy spirit.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = readingTextSize.sp
                            ),
                            color = ParchmentLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // App Theme & Appearance Mode Card
        val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
        val darkModePreference by viewModel.darkModePreference.collectAsStateWithLifecycle()
        val isSystemDark = isSystemInDarkTheme()
        val isEffectiveDark = when (darkModePreference) {
            "DARK" -> true
            "LIGHT" -> false
            "SYSTEM" -> isSystemDark
            else -> when (appTheme) {
                "CATHEDRAL_LIGHT" -> false
                else -> true
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_theme_selector_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Liturgical Theme & Dark Mode",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Personalize your appearance. Each liturgical theme features custom-tailored dark and light mode color harmonies.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Appearance Mode Toggle (Dark / Light / System)
                Text(
                    text = "APPEARANCE MODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldSacred
                )
                Spacer(modifier = Modifier.height(8.dp))

                val modeOptions = listOf(
                    Triple("DARK", "Dark Mode", Icons.Default.DarkMode),
                    Triple("LIGHT", "Light Mode", Icons.Default.LightMode),
                    Triple("SYSTEM", "System Auto", Icons.Default.BrightnessAuto)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modeOptions.forEach { (modeId, label, icon) ->
                        val isModeSelected = darkModePreference == modeId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isModeSelected) CrimsonBurgundy.copy(alpha = 0.35f) else DarkCathedralSurfaceVariant,
                            border = BorderStroke(
                                width = if (isModeSelected) 2.dp else 1.dp,
                                color = if (isModeSelected) GoldBright else DarkCathedralCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateDarkModePreference(modeId) }
                                .testTag("dark_mode_option_$modeId")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isModeSelected) GoldBright else ParchmentLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isModeSelected) GoldBright else ParchmentLight,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Theme Selection Header
                Text(
                    text = "LITURGICAL COLOR PALETTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldSacred
                )
                Spacer(modifier = Modifier.height(8.dp))

                val themeOptions = if (isEffectiveDark) {
                    listOf(
                        Triple("CATHEDRAL_DARK", "Cathedral Gothic (Sacred Gold & Burgundy)", listOf(Color(0xFF0E1116), Color(0xFFD4AF37), Color(0xFF800020))),
                        Triple("ROSA_MYSTICA", "Rosa Mystica (Rose Velvet & Glowing Mint)", listOf(Color(0xFF140B10), Color(0xFFF09AB1), Color(0xFFA5DCBE))),
                        Triple("PENITENTIAL_VIOLET", "Penitential Violet (Imperial Amethyst)", listOf(Color(0xFF0F0B18), Color(0xFFCE93D8), Color(0xFF4A148C))),
                        Triple("MARIAN_BLUE", "Marian Blue (Celestial Starry Ultramarine)", listOf(Color(0xFF070D18), Color(0xFF64B5F6), Color(0xFF0D2D5E))),
                        Triple("MARTYR_CRIMSON", "Martyrs Crimson (Obsidian Velvet Ruby)", listOf(Color(0xFF13080A), Color(0xFFEF5350), Color(0xFF5A0818))),
                        Triple("ORDINARY_GREEN", "Ordinary Green (Abyssal Forest Emerald)", listOf(Color(0xFF071109), Color(0xFF66BB6A), Color(0xFF144D1E)))
                    )
                } else {
                    listOf(
                        Triple("CATHEDRAL_DARK", "Cathedral Gothic (Warm Ivory & Bronze)", listOf(Color(0xFFFAF7EE), Color(0xFF800020), Color(0xFF8A5A04))),
                        Triple("ROSA_MYSTICA", "Rosa Mystica (Blush Cream & Mint Frost)", listOf(StaticBlushCream, StaticPastelRoseAccent, StaticMintFrost)),
                        Triple("PENITENTIAL_VIOLET", "Penitential Violet (Lilac & Royal Violet)", listOf(Color(0xFFFAF6FC), Color(0xFF6A1B9A), Color(0xFF8A5A04))),
                        Triple("MARIAN_BLUE", "Marian Blue (Azure Dawn & Royal Ultramarine)", listOf(Color(0xFFF4F8FD), Color(0xFF0D47A1), Color(0xFF8A5A04))),
                        Triple("MARTYR_CRIMSON", "Martyrs Crimson (Alabaster & Cardinal Red)", listOf(Color(0xFFFDF6F6), Color(0xFF900C1E), Color(0xFF8A5A04))),
                        Triple("ORDINARY_GREEN", "Ordinary Green (Meadow Ivory & Liturgical Green)", listOf(Color(0xFFF4F8F4), Color(0xFF1B5E20), Color(0xFF8A5A04)))
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    themeOptions.forEach { (id, name, colors) ->
                        val isSelected = appTheme == id || (id == "CATHEDRAL_DARK" && appTheme == "CATHEDRAL_LIGHT")
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CrimsonBurgundy.copy(alpha = 0.25f) else DarkCathedralSurfaceVariant,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GoldBright else DarkCathedralCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateAppTheme(id) }
                                .testTag("theme_option_$id")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Visual color preview spheres
                                    Box(modifier = Modifier.size(40.dp)) {
                                        // Background sphere
                                        Surface(
                                            modifier = Modifier.size(24.dp).align(Alignment.BottomStart),
                                            shape = CircleShape,
                                            color = colors[0],
                                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                                        ) {}
                                        // Primary sphere
                                        Surface(
                                            modifier = Modifier.size(24.dp).align(Alignment.Center),
                                            shape = CircleShape,
                                            color = colors[1],
                                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                                        ) {}
                                        // Secondary sphere
                                        Surface(
                                            modifier = Modifier.size(24.dp).align(Alignment.TopEnd),
                                            shape = CircleShape,
                                            color = colors[2],
                                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                                        ) {}
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif
                                        ),
                                        color = if (isSelected) GoldBright else ParchmentLight
                                    )
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateAppTheme(id) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = GoldBright,
                                        unselectedColor = DarkCathedralCardBorder
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Local Encrypted Backup & Restore Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("backup_restore_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Local Backup & Restore",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Export or restore your examination of conscience entries, personal meditation reflections, spiritual duties, and confession ledger key into an encrypted JSON file on your device storage (100% offline & local).",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isBackupOperationRunning) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GoldBright, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Encrypting / Decrypting local data...", color = GoldBright, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBackupPasswordDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_backup_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                            border = BorderStroke(1.dp, GoldSacred),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Backup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                importDocumentLauncher.launch(arrayOf("*/*", "application/json", "application/octet-stream"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_backup_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = StaticGoldBright),
                            border = BorderStroke(1.dp, GoldSacred),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = StaticGoldBright, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore Backup", fontSize = 12.sp, color = StaticGoldBright, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Export Examination Record to PDF Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("export_exam_pdf_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export Examination to Encrypted PDF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Generate a formatted PDF report of your Examination of Conscience entries for sacramental preparation or personal record-keeping, protected with 128-bit PDF encryption for complete data privacy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { showExportExamPdfDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_exam_pdf_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                    border = BorderStroke(1.dp, GoldSacred),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Password-Protected PDF 📄🔒", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // RegulaVitae Developer & Social Links Card in Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("regulavitae_settings_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(
                1.5.dp,
                androidx.compose.ui.graphics.Brush.linearGradient(
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CrimsonBurgundy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Powered & Developed by RegulaVitae",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GoldBright
                            )
                            Text(
                                text = "My Personal Creator Work & Socials",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Follow and support my work across my official social channels for daily traditional Catholic content, feast reminders, and spiritual discipline updates:",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Social buttons grid/column
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // WhatsApp
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029Vb8WpzkFi8xWdG8IJf2F"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("settings_social_whatsapp"),
                        border = BorderStroke(1.dp, Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Join WhatsApp Channel", color = ParchmentLight, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }

                    // Website
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://regulavitae.vercel.app/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("settings_social_website"),
                        border = BorderStroke(1.dp, GoldSacred),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = GoldBright, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Official Website (regulavitae.vercel.app)", color = ParchmentLight, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }

                    // Instagram
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/regulavitae/#"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("settings_social_instagram"),
                        border = BorderStroke(1.dp, Color(0xFFE1306C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Follow on Instagram (@regulavitae)", color = ParchmentLight, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }

                    // YouTube
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@regulavitae"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("settings_social_youtube"),
                        border = BorderStroke(1.dp, Color(0xFFFF0000)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subscribe on YouTube (@regulavitae)", color = ParchmentLight, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.setTab(9) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_view_regulavitae_page_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonBurgundy,
                        contentColor = StaticGoldBright
                    ),
                    border = BorderStroke(1.dp, GoldSacred),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Open Dedicated RegulaVitae Page ☩", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }

    if (showExportExamPdfDialog) {
        ExportExaminationPdfDialog(
            onDismiss = { showExportExamPdfDialog = false },
            onConfirmExport = { pwd ->
                pdfExportPasswordInput = pwd
                showExportExamPdfDialog = false
                val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                exportExamPdfLauncher.launch("custos_examination_$dateStr.pdf")
            }
        )
    }

    // Edit Time Dialog
    if (showTimeEditDialog != null) {
        val titleText = when (showTimeEditDialog) {
            "SAINT" -> "Saint of the Day Notification Time"
            "MORNING" -> "Morning Prayer Time"
            "EVENING" -> "Evening Prayer / Examen Time"
            "ANGELUS_MORNING" -> "Angelus - Morning Alarm Time"
            "ANGELUS_NOON" -> "Angelus - Midday Alarm Time (12:00 PM)"
            "ANGELUS_EVENING" -> "Angelus - Evening Alarm Time (6:00 PM)"
            "SACRED_HEART" -> "Sacred Heart Devotion Notification Time"
            "IMMACULATE_HEART" -> "Immaculate Heart Devotion Notification Time"
            else -> "Notification Time"
        }

        AlertDialog(
            onDismissRequest = {
                showTimeEditDialog = null
            },
            title = {
                Text(
                    titleText,
                    color = GoldBright,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Scroll the clock face or hours/minutes wheels to set schedule time:",
                        color = ParchmentLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollableClockPicker(
                        initialTime = tempTimeValue.ifBlank { "08:00" },
                        onTimeChanged = { newTime ->
                            tempTimeValue = newTime
                        },
                        modifier = Modifier.testTag("dialog_time_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanTime = tempTimeValue.trim()
                        if (cleanTime.isBlank() || !cleanTime.contains(":")) {
                            Toast.makeText(context, "Please enter a valid time in HH:mm format (e.g. 12:00)", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        when (showTimeEditDialog) {
                            "SAINT" -> saintTimeInput = cleanTime
                            "MORNING" -> morningTimeInput = cleanTime
                            "EVENING" -> eveningTimeInput = cleanTime
                            "ANGELUS_MORNING" -> angelusMorningInput = cleanTime
                            "ANGELUS_NOON" -> angelusNoonInput = cleanTime
                            "ANGELUS_EVENING" -> angelusEveningInput = cleanTime
                            "SACRED_HEART" -> sacredHeartTimeInput = cleanTime
                            "IMMACULATE_HEART" -> immaculateHeartTimeInput = cleanTime
                        }
                        viewModel.updateNotificationPreferences(
                            morningTimeInput,
                            eveningTimeInput,
                            saintTimeInput,
                            notificationsEnabled,
                            angelusMorningInput,
                            angelusNoonInput,
                            angelusEveningInput,
                            angelusEnabled,
                            sacredHeartTimeInput,
                            sacredHeartEnabled,
                            immaculateHeartTimeInput,
                            immaculateHeartEnabled,
                            liturgicalSeasonEnabled
                        )
                        Toast.makeText(context, "System prayer notification time updated.", Toast.LENGTH_SHORT).show()
                        showTimeEditDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    modifier = Modifier.testTag("save_time_btn")
                ) {
                    Text("Save Time", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimeEditDialog = null
                }) {
                    Text("Cancel", color = TextMutedParchment)
                }
            },
            containerColor = DarkCathedralSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Export Password Dialog
    if (showBackupPasswordDialog) {
        var pwdText by remember { mutableStateOf("") }
        var confirmPwdText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showBackupPasswordDialog = false },
            title = {
                Text(
                    "Create Encrypted Backup",
                    color = GoldBright,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Set a password to protect your encrypted JSON backup file (AES-256). You will need this password to restore your data later.",
                        color = ParchmentLight,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = pwdText,
                        onValueChange = { pwdText = it },
                        label = { Text("Backup Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBright,
                            unfocusedBorderColor = DarkCathedralCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("export_pwd_input")
                    )
                    OutlinedTextField(
                        value = confirmPwdText,
                        onValueChange = { confirmPwdText = it },
                        label = { Text("Confirm Backup Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBright,
                            unfocusedBorderColor = DarkCathedralCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("export_confirm_pwd_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pwdText.length < 4) {
                            Toast.makeText(context, "Password must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (pwdText != confirmPwdText) {
                            Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        backupPasswordInput = pwdText
                        showBackupPasswordDialog = false
                        val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                        exportDocumentLauncher.launch("custos_backup_$dateStr.json")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    modifier = Modifier.testTag("confirm_export_btn")
                ) {
                    Text("Save Encrypted File", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupPasswordDialog = false }) {
                    Text("Cancel", color = TextMutedParchment)
                }
            },
            containerColor = DarkCathedralSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Restore Password Dialog
    if (showRestorePasswordDialog && restoreFileUri != null) {
        var restorePwdText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showRestorePasswordDialog = false
                restoreFileUri = null
            },
            title = {
                Text(
                    "Decrypt & Restore Backup",
                    color = GoldBright,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Enter the password used when creating this backup file to decrypt and restore your entries:",
                        color = ParchmentLight,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = restorePwdText,
                        onValueChange = { restorePwdText = it },
                        label = { Text("Decryption Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBright,
                            unfocusedBorderColor = DarkCathedralCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("restore_pwd_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uri = restoreFileUri
                        if (uri == null || restorePwdText.isBlank()) {
                            Toast.makeText(context, "Password is required.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        showRestorePasswordDialog = false
                        isBackupOperationRunning = true
                        coroutineScope.launch {
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                if (inputStream != null) {
                                    val result = viewModel.restoreEncryptedBackup(restorePwdText, inputStream)
                                    when (result) {
                                        is com.example.util.RestoreResult.Success -> {
                                            Toast.makeText(context, "Restore Completed!\n${result.restoredCountSummary}", Toast.LENGTH_LONG).show()
                                        }
                                        is com.example.util.RestoreResult.Error -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Could not open selected backup file.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Restore Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            } finally {
                                isBackupOperationRunning = false
                                restoreFileUri = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    modifier = Modifier.testTag("confirm_restore_btn")
                ) {
                    Text("Decrypt & Restore Data", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestorePasswordDialog = false
                    restoreFileUri = null
                }) {
                    Text("Cancel", color = TextMutedParchment)
                }
            },
            containerColor = DarkCathedralSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
    }

@Composable
fun NotificationTimeRow(
    title: String,
    subtitle: String? = null,
    time: String,
    onClick: () -> Unit,
    onTestClick: (() -> Unit)? = null,
    testTagPrefix: String = "notif_row"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = ParchmentLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, color = TextMutedParchment, fontSize = 11.sp)
            }
            Text("⏰ Scheduled time: $time", color = GoldBright, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            onTestClick?.let { onTest ->
                OutlinedButton(
                    onClick = onTest,
                    border = BorderStroke(1.dp, CrimsonBurgundy),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("${testTagPrefix}_test_btn")
                ) {
                    Text("Test", fontSize = 10.sp, color = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) StaticCrimsonBurgundy else StaticGoldBright, fontWeight = FontWeight.Bold)
                }
            }
            OutlinedButton(
                onClick = onClick,
                border = BorderStroke(1.dp, GoldSacred),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.testTag("${testTagPrefix}_change_btn")
            ) {
                Text("Change", fontSize = 10.sp, color = GoldBright, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PhoneCalendarSyncDialog(
    calendars: List<PhoneCalendar>,
    onSelectAndSync: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Phone Calendar",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldBright
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Select which calendar account on your phone to clone all spiritual duties to. This creates recurring alerts matching your rule of life.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )

                if (calendars.isEmpty()) {
                    Text(
                        text = "No active calendars found on your device. Please ensure you have configured a calendar account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        calendars.forEach { cal ->
                            val isSelected = selectedCalendarId == cal.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) DarkCathedralSurfaceVariant else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) GoldBright else DarkCathedralCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedCalendarId = cal.id }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedCalendarId = cal.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldBright)
                                )
                                Column {
                                    Text(
                                        text = cal.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ParchmentLight
                                    )
                                    Text(
                                        text = cal.accountName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedParchment
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCalendarId?.let { onSelectAndSync(it) }
                },
                enabled = selectedCalendarId != null,
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black)
            ) {
                Text("Clone Tasks", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(16.dp)
    )
}


