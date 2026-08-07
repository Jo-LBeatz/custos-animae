package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.notifications.NotificationHelper
import androidx.compose.animation.togetherWith
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.screens.checkNotificationPermission
import com.example.ui.screens.checkExactAlarmPermission
import com.example.ui.theme.CrimsonBurgundy
import com.example.ui.theme.DarkCathedralBackground
import com.example.ui.theme.DarkCathedralCardBorder
import com.example.ui.theme.DarkCathedralSurface
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldSacred
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMutedParchment

data class ActiveCallState(val title: String, val message: String)

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val activeCallData = mutableStateOf<ActiveCallState?>(null)

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Permission result handled
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleCallIntent(intent)
    }

    private fun handleCallIntent(intent: Intent?) {
        if (intent != null && intent.action == com.example.notifications.CallActionReceiver.ACTION_ANSWER_CALL) {
            val title = intent.getStringExtra("EXTRA_CALL_TITLE") ?: "Rule of Life Call"
            val message = intent.getStringExtra("EXTRA_CALL_MESSAGE") ?: "Time for your scheduled spiritual duty."
            activeCallData.value = ActiveCallState(title, message)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetExpiredDuties()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming call action intent if activity started from call notification
        handleCallIntent(intent)

        // Initialize high importance notification channels with sound & vibration
        NotificationHelper.createNotificationChannels(this)

        // Request POST_NOTIFICATIONS runtime permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val appTheme by viewModel.appTheme.collectAsState()
            MyApplicationTheme(themeId = appTheme) {
                val profile by viewModel.userProfile.collectAsState()
                val duties by viewModel.allDuties.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()
                val context = LocalContext.current

                var showBackgroundPermissionDialog by remember { mutableStateOf(false) }
                var showPermissionsSetup by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val hasSeenPermissions = sharedPrefs.getBoolean("has_seen_permissions", false)
                    
                    val needsPermissions = !checkNotificationPermission(context) || 
                                           !checkExactAlarmPermission(context) || 
                                           !isBatteryOptimizationIgnored(context)
                                           
                    if (profile != null && profile!!.isConfigured && needsPermissions && !hasSeenPermissions) {
                        showPermissionsSetup = true
                    } else if (!isBatteryOptimizationIgnored(context) && !showPermissionsSetup) {
                        showBackgroundPermissionDialog = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkCathedralBackground
                ) {
                    val activeCall = activeCallData.value

                    if (activeCall != null) {
                        com.example.ui.components.ActiveCallOverlay(
                            title = activeCall.title,
                            message = activeCall.message,
                            viewModel = viewModel,
                            onEndCall = {
                                activeCallData.value = null
                            }
                        )
                    } else if (profile == null || !profile!!.isConfigured) {
                        OnboardingGenderScreen(
                            onSelectGender = { gender, morning, evening, saint, enabled ->
                                viewModel.setupGenderProfileWithNotifications(gender, morning, evening, saint, enabled)
                                val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                val hasSeenPermissions = sharedPrefs.getBoolean("has_seen_permissions", false)
                                val needsPermissions = !checkNotificationPermission(context) || 
                                                       !checkExactAlarmPermission(context) || 
                                                       !isBatteryOptimizationIgnored(context)
                                if (needsPermissions && !hasSeenPermissions) {
                                    showPermissionsSetup = true
                                }
                            }
                        )
                    } else if (showPermissionsSetup) {
                        PermissionsSetupScreen(
                            onPermissionsComplete = {
                                val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                sharedPrefs.edit().putBoolean("has_seen_permissions", true).apply()
                                showPermissionsSetup = false
                                if (!isBatteryOptimizationIgnored(context)) {
                                    showBackgroundPermissionDialog = true
                                }
                            }
                        )
                    } else {
                        MainAppContent(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.setTab(it) },
                            viewModel = viewModel,
                            duties = duties
                        )

                        if (showBackgroundPermissionDialog) {
                            BackgroundPermissionDialog(
                                onDismiss = { showBackgroundPermissionDialog = false },
                                onRequestPermission = {
                                    requestIgnoreBatteryOptimization(context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun requestIgnoreBatteryOptimization(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}

fun isBatteryOptimizationIgnored(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    } else {
        true
    }
}

@Composable
fun BackgroundPermissionDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCathedralSurface,
        titleContentColor = GoldBright,
        textContentColor = TextMutedParchment,
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Church,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Background Execution Needed",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = GoldBright
                )
            }
        },
        text = {
            Text(
                text = "Custos Animae must always run uninterrupted in the background of your device so that scheduled spiritual duty reminders, rule of life alarms, and saint reflections arrive reliably at your designated time.\n\n⚠️ Warning: If you do not give permission to run in the background, your device power management will suspend the app and you will NOT receive notifications for your spiritual discipline.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMutedParchment
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onRequestPermission()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                border = BorderStroke(1.dp, GoldSacred)
            ) {
                Text("Allow Background Execution", color = GoldBright)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("I Understand (Skip)", color = TextMutedParchment)
            }
        }
    )
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun MainAppContent(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    viewModel: MainViewModel,
    duties: List<com.example.data.SpiritualDuty>
) {
    val isBottomNavVisible by viewModel.isBottomNavVisible.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, "nav_home_tab"),
        NavItem("Rule", Icons.Default.Checklist, "nav_rule_tab"),
        NavItem("Saint", Icons.Default.AccountCircle, "nav_saint_tab"),
        NavItem("Prayers", Icons.AutoMirrored.Filled.MenuBook, "nav_prayers_tab"),
        NavItem("Examine", Icons.Default.Church, "nav_examine_tab"),
        NavItem("Calendar", Icons.Default.CalendarMonth, "nav_calendar_tab"),
        NavItem("Deception", Icons.Default.Warning, "nav_deception_tab"),
        NavItem("Catechism", Icons.AutoMirrored.Filled.MenuBook, "nav_catechism_tab"),
        NavItem("Settings", Icons.Default.Settings, "nav_settings_tab")
    )

    Scaffold(
        bottomBar = {
            androidx.compose.animation.AnimatedVisibility(
                visible = isBottomNavVisible,
                enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.expandVertically(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.shrinkVertically(animationSpec = androidx.compose.animation.core.tween(300))
            ) {
                Surface(
                    color = DarkCathedralSurface.copy(alpha = 0.95f),
                    tonalElevation = 12.dp,
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .border(
                            BorderStroke(
                                1.dp,
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(
                                        GoldBright.copy(alpha = 0.8f),
                                        GoldSacred.copy(alpha = 0.2f),
                                        GoldBright.copy(alpha = 0.8f)
                                    )
                                )
                            )
                        )
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = currentTab,
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = GoldBright,
                        edgePadding = 8.dp,
                        indicator = { tabPositions ->
                            if (currentTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                                    color = CrimsonBurgundy
                                )
                            }
                        },
                        divider = {}
                    ) {
                        navItems.forEachIndexed { index, item ->
                            val isSelected = currentTab == index
                            Tab(
                                selected = isSelected,
                                onClick = { onTabSelected(index) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) GoldBright else TextMutedParchment,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                text = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                        ),
                                        color = if (isSelected) GoldBright else TextMutedParchment,
                                        maxLines = 1
                                    )
                                },
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkCathedralBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (readingProgress > 0f) {
                    LinearProgressIndicator(
                        progress = { readingProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .testTag("reading_progress_bar"),
                        color = GoldBright,
                        trackColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    androidx.compose.animation.AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                        },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            0 -> StainedGlassDashboardScreen(
                                viewModel = viewModel,
                                duties = duties,
                                onNavigateToTab = onTabSelected
                            )
                            1 -> RuleOfLifeScreen(viewModel = viewModel, duties = duties)
                            2 -> SaintMeditationScreen(viewModel = viewModel)
                            3 -> PrayerLibraryScreen(viewModel = viewModel)
                            4 -> ExaminationScreen(viewModel = viewModel)
                            5 -> CalendarScreen(viewModel = viewModel)
                            6 -> DeceptionScreen(viewModel = viewModel)
                            7 -> CatechismScreen(viewModel = viewModel)
                            8 -> SettingsScreen(viewModel = viewModel)
                            else -> StainedGlassDashboardScreen(
                                viewModel = viewModel,
                                duties = duties,
                                onNavigateToTab = onTabSelected
                            )
                        }
                    }
                }
            }
        }
    }
}
