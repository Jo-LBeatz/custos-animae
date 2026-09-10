package com.example.notifications

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Church
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.R
import com.example.ui.theme.*

open class CallOverlayActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var ringingTimeoutJob: kotlinx.coroutines.Job? = null

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN ||
            keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == android.view.KeyEvent.KEYCODE_VOLUME_MUTE) {
            stopRinging()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Turn screen on and show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        val title = intent.getStringExtra("EXTRA_CALL_TITLE") ?: intent.getStringExtra("EXTRA_TITLE") ?: "Rule of Life Reminder"
        val message = intent.getStringExtra("EXTRA_CALL_MESSAGE") ?: intent.getStringExtra("EXTRA_MESSAGE") ?: "Time for your scheduled spiritual duty."
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 1001)
        val showRescheduleOnly = intent.getBooleanExtra("SHOW_RESCHEDULE_ONLY", false)

        if (!showRescheduleOnly) {
            startRinging()
            // 45-second auto-timeout: if unanswered, stop ringing and record as missed
            ringingTimeoutJob = lifecycleScope.launch {
                kotlinx.coroutines.delay(45000L)
                if (!isFinishing && !isDestroyed) {
                    stopRinging()
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    manager?.cancel(notificationId)
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            com.example.data.CallLogRepository.getInstance(this@CallOverlayActivity)
                                .logCall(title, "Missed prayer call: $message", "MISSED")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    finish()
                }
            }
        }

        val sharedPrefs = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val savedTheme = sharedPrefs.getString("app_theme", "CATHEDRAL_DARK") ?: "CATHEDRAL_DARK"
        val savedDarkModePref = sharedPrefs.getString(
            "dark_mode_preference",
            if (savedTheme == "CATHEDRAL_LIGHT") "LIGHT" else "DARK"
        ) ?: "DARK"

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            val isDark = when (savedDarkModePref) {
                "DARK" -> true
                "LIGHT" -> false
                "SYSTEM" -> systemIsDark
                else -> when (savedTheme) {
                    "CATHEDRAL_LIGHT" -> false
                    else -> true
                }
            }

            MyApplicationTheme(themeId = savedTheme, darkTheme = isDark) {
                var showReschedulePanel by remember { mutableStateOf(showRescheduleOnly) }

                if (!showReschedulePanel) {
                    CallOverlayScreen(
                        title = title,
                        message = message,
                        onAccept = {
                            ringingTimeoutJob?.cancel()
                            stopRinging()
                            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                            manager?.cancel(notificationId)

                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    com.example.data.CallLogRepository.getInstance(this@CallOverlayActivity)
                                        .logCall(title, message, "ANSWERED")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            val mainIntent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                action = CallActionReceiver.ACTION_ANSWER_CALL
                                putExtra("EXTRA_CALL_TITLE", title)
                                putExtra("EXTRA_CALL_MESSAGE", message)
                                putExtra("EXTRA_NOTIFICATION_ID", notificationId)
                                putExtra("OPEN_PRAYER_DISPLAY", true)
                            }

                            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                            if (keyguardManager != null && keyguardManager.isKeyguardLocked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    keyguardManager.requestDismissKeyguard(this@CallOverlayActivity, object : KeyguardManager.KeyguardDismissCallback() {
                                        override fun onDismissSucceeded() {
                                            startActivity(mainIntent)
                                            finish()
                                        }

                                        override fun onDismissError() {
                                            startActivity(mainIntent)
                                            finish()
                                        }

                                        override fun onDismissCancelled() {
                                            startActivity(mainIntent)
                                            finish()
                                        }
                                    })
                                } else {
                                    startActivity(mainIntent)
                                    finish()
                                }
                            } else {
                                startActivity(mainIntent)
                                finish()
                            }
                        },
                        onBusy = {
                            ringingTimeoutJob?.cancel()
                            stopRinging()
                            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                            manager?.cancel(notificationId)
                            showReschedulePanel = true
                        },
                        onDismiss = {
                            ringingTimeoutJob?.cancel()
                            stopRinging()
                            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                            manager?.cancel(notificationId)
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    com.example.data.CallLogRepository.getInstance(this@CallOverlayActivity)
                                        .logCall(title, message, "DECLINED")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            finish()
                        }
                    )
                } else {
                    val dutyId = if (notificationId >= 1000) notificationId - 1000 else -1
                    RescheduleOverlayPanel(
                        title = title,
                        dutyId = dutyId,
                        onBack = if (!showRescheduleOnly) { { showReschedulePanel = false } } else null,
                        onDeclineConfirmed = {
                            ringingTimeoutJob?.cancel()
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    com.example.data.CallLogRepository.getInstance(this@CallOverlayActivity)
                                        .logCall(title, message, "DECLINED")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            finish()
                        },
                        onRescheduled = { selectedTime ->
                            ringingTimeoutJob?.cancel()
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    val db = com.example.data.AppDatabase.getDatabase(this@CallOverlayActivity)
                                    val duty = if (dutyId > 0) db.appDao().getDutyById(dutyId) else null
                                    if (duty != null) {
                                        val today = RescheduleManager.getTodayDateString()
                                        RescheduleManager.setReschedule(this@CallOverlayActivity, dutyId, today, selectedTime)

                                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager
                                        if (alarmManager != null) {
                                            NotificationScheduler.scheduleDutyNotification(this@CallOverlayActivity, alarmManager, duty)
                                        }
                                    } else {
                                        NotificationScheduler.scheduleOneOffReminder(
                                            this@CallOverlayActivity,
                                            title,
                                            message,
                                            selectedTime,
                                            notificationId
                                        )
                                    }

                                    com.example.data.CallLogRepository.getInstance(this@CallOverlayActivity)
                                        .logCall(title, "Rescheduled to $selectedTime today: $message", "RESCHEDULED")

                                    launch(Dispatchers.Main) {
                                        android.widget.Toast.makeText(
                                            this@CallOverlayActivity,
                                            "Rescheduled '$title' to $selectedTime for today.",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    launch(Dispatchers.Main) { finish() }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    open fun startRinging() {
        CallRingerManager.startRinging(this)
    }

    open fun stopRinging() {
        CallRingerManager.stopRinging()
        val notificationId = intent?.getIntExtra("EXTRA_NOTIFICATION_ID", 1001) ?: 1001
        try {
            RuleOfLifeCallService.stopCall(this, notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRinging()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            stopRinging()
        }
    }
}

@Composable
fun CallOverlayScreen(
    title: String,
    message: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    onBusy: () -> Unit
) {
    var animateEntrance by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateEntrance = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Pulsing outer glow scale and button scale for the Accept button
    val acceptButtonGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "acceptButtonGlowScale"
    )
    val acceptButtonScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "acceptButtonScale"
    )

    // Entrance slide down for the header block (using standard dp state)
    val headerOffset by animateDpAsState(
        targetValue = if (animateEntrance) 0.dp else (-40).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "headerEntrance"
    )
    val headerAlpha by animateFloatAsState(
        targetValue = if (animateEntrance) 1f else 0f,
        animationSpec = tween(700),
        label = "headerAlpha"
    )

    // Entrance slide up for the message card block
    val cardOffset by animateDpAsState(
        targetValue = if (animateEntrance) 0.dp else 40.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardEntrance"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (animateEntrance) 1f else 0f,
        animationSpec = tween(800, delayMillis = 150),
        label = "cardAlpha"
    )

    // Entrance slide up for buttons row
    val buttonsOffset by animateDpAsState(
        targetValue = if (animateEntrance) 0.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonsEntrance"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (animateEntrance) 1f else 0f,
        animationSpec = tween(800, delayMillis = 250),
        label = "buttonsAlpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("call_overlay_full_screen"),
        color = DarkCathedralBackground
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
            // Adaptive Top/Middle Content Area (Title + Description + Contact Avatar)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = "INCOMING PRAYER CALL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Circular Contact Image Placeholder with Metallic Gold Border and Pulsing Aura
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(108.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(CrimsonBurgundy, DarkCathedralSurface)
                            )
                        )
                        .border(2.5.dp, GoldBright, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Church,
                        contentDescription = "Spiritual Caller Contact Icon",
                        tint = GoldBright,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title adaptively cut down in UI with maxLines and Ellipsis
                com.example.ui.components.ResponsiveText(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    ),
                    color = GoldBright,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = headerOffset)
                        .graphicsLayer(alpha = headerAlpha)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rule of Life • Canonical Hours Prayer Call",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentLight.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Message Preview Card adaptively cut down & scrollable in UI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                        .offset(y = cardOffset)
                        .graphicsLayer(alpha = cardAlpha),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, DarkCathedralCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 20.sp
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

            // Action Buttons Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = buttonsOffset)
                    .graphicsLayer(alpha = buttonsAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dedicated "I'm Busy / Postpone" option for immediate addition of time
                OutlinedButton(
                    onClick = onBusy,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                        .testTag("busy_postpone_button"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, GoldBright.copy(alpha = 0.8f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkCathedralSurface.copy(alpha = 0.85f),
                        contentColor = GoldBright
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Postpone",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I'm Busy • Postpone / Add Time",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High Visibility Accept & Dismiss Buttons Row - GUARANTEED ALWAYS VISIBLE AT BOTTOM
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline / Dismiss Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(72.dp)
                                .testTag("dismiss_call_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Decline Call",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Decline", style = MaterialTheme.typography.labelMedium, color = ParchmentLight)
                    }

                    // Pick Up / Accept Button with Outer Glow and Scale Pulse
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(92.dp)
                        ) {
                            // Soft glowing outer pulse ring
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .scale(acceptButtonGlowScale)
                                    .background(
                                        color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
                            )

                            Button(
                                onClick = onAccept,
                                modifier = Modifier
                                    .size(72.dp)
                                    .scale(acceptButtonScale)
                                    .testTag("accept_call_button"),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Pick Up Call",
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Pick Up", style = MaterialTheme.typography.labelMedium, color = GoldBright, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RescheduleOverlayPanel(
    title: String,
    dutyId: Int,
    onBack: (() -> Unit)? = null,
    onDeclineConfirmed: () -> Unit,
    onRescheduled: (timeStr: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedCustomTime by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reschedule_overlay_panel"),
        color = DarkCathedralBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Back button if navigated from active call
            if (onBack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = onBack) {
                        Text("← Back to Call", color = GoldBright, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Elegant Monastic Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
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
                    contentDescription = "Monastic Bell",
                    tint = GoldBright,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Prompt
            Text(
                text = "Are you busy?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldBright,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Would you like to reschedule '${title}' to another time today?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Serif,
                    lineHeight = 24.sp
                ),
                color = ParchmentLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your regular rule of life schedule will resume normally tomorrow.",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentLight.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            fun previewTime(minutesToAdd: Int): String {
                val cal = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.MINUTE, minutesToAdd)
                }
                return java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(cal.time)
            }

            fun formatted24h(minutesToAdd: Int): String {
                val cal = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.MINUTE, minutesToAdd)
                }
                return String.format(java.util.Locale.US, "%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
            }

            // Quick Postpone Choices Grid
            Text(
                text = "CHOOSE SNOOZE TIME FOR TODAY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldBright.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: +5m and +15m
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onRescheduled(formatted24h(5)) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+5 min", color = ParchmentLight, fontWeight = FontWeight.Bold)
                        Text(previewTime(5), style = MaterialTheme.typography.labelSmall, color = GoldSacred)
                    }
                }

                Button(
                    onClick = { onRescheduled(formatted24h(15)) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+15 min", color = ParchmentLight, fontWeight = FontWeight.Bold)
                        Text(previewTime(15), style = MaterialTheme.typography.labelSmall, color = GoldSacred)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: +30m and +1 hr
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onRescheduled(formatted24h(30)) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+30 min", color = ParchmentLight, fontWeight = FontWeight.Bold)
                        Text(previewTime(30), style = MaterialTheme.typography.labelSmall, color = GoldSacred)
                    }
                }

                Button(
                    onClick = { onRescheduled(formatted24h(60)) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+1 hour", color = ParchmentLight, fontWeight = FontWeight.Bold)
                        Text(previewTime(60), style = MaterialTheme.typography.labelSmall, color = GoldSacred)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: +2 hr and Custom Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onRescheduled(formatted24h(120)) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldBright.copy(alpha = 0.5f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+2 hours", color = ParchmentLight, fontWeight = FontWeight.Bold)
                        Text(previewTime(120), style = MaterialTheme.typography.labelSmall, color = GoldSacred)
                    }
                }

                Button(
                    onClick = {
                        val cal = java.util.Calendar.getInstance()
                        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                        val minute = cal.get(java.util.Calendar.MINUTE)
                        val picker = android.app.TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val formatted = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                                onRescheduled(formatted)
                            },
                            hour,
                            minute,
                            false
                        )
                        picker.show()
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = StaticGoldBright),
                    border = BorderStroke(1.dp, StaticGoldBright)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Custom Time", color = StaticGoldBright, fontWeight = FontWeight.Bold)
                        Text("Pick clock", style = MaterialTheme.typography.labelSmall, color = ParchmentLight.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Decline Completely button
            TextButton(
                onClick = onDeclineConfirmed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No, Just Decline For Today",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ParchmentLight.copy(alpha = 0.6f)
                )
            }
        }
    }
}
