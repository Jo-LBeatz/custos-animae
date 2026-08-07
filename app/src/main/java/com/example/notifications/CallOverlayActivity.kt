package com.example.notifications

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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        startRinging()

        val sharedPrefs = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val savedTheme = sharedPrefs.getString("app_theme", "CATHEDRAL_DARK") ?: "CATHEDRAL_DARK"

        setContent {
            MyApplicationTheme(themeId = savedTheme) {
                CallOverlayScreen(
                    title = title,
                    message = message,
                    onAccept = {
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
                            putExtra("OPEN_PRAYER_DISPLAY", true)
                        }
                        startActivity(mainIntent)
                        finish()
                    },
                    onDismiss = {
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
            }
        }
    }

    open fun startRinging() {
        CallRingerManager.startRinging(this)
    }

    open fun stopRinging() {
        val notificationId = intent?.getIntExtra("EXTRA_NOTIFICATION_ID", 1001) ?: 1001
        val serviceIntent = Intent(this, RuleOfLifeCallService::class.java).apply {
            action = RuleOfLifeCallService.ACTION_STOP_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        CallRingerManager.stopRinging()
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
    onDismiss: () -> Unit
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Caller Details & Circular Contact Placeholder (Slide down + Fade in)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = headerOffset)
                    .graphicsLayer(alpha = headerAlpha)
            ) {
                Text(
                    text = "INCOMING PRAYER CALL",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 2.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Circular Contact Image Placeholder with Metallic Gold Border and Pulsing Aura
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(136.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(CrimsonBurgundy, DarkCathedralSurface)
                            )
                        )
                        .border(3.dp, GoldBright, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Church,
                        contentDescription = "Spiritual Caller Contact Icon",
                        tint = GoldBright,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rule of Life • Canonical Hours Prayer Call",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentLight
                )
            }

            // Message Preview Card (Slide up + Fade in)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = cardOffset)
                    .graphicsLayer(alpha = cardAlpha),
                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                border = BorderStroke(1.dp, DarkCathedralCardBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp
                    ),
                    color = ParchmentLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }

            // High Visibility Accept & Dismiss Buttons Row (Slide up + Fade in)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
                    .offset(y = buttonsOffset)
                    .graphicsLayer(alpha = buttonsAlpha),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dismiss Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(76.dp)
                            .testTag("dismiss_call_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Dismiss Call",
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dismiss", style = MaterialTheme.typography.labelMedium, color = ParchmentLight)
                }

                // Accept Button with Outer Glow and Scale Pulse
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(96.dp)
                    ) {
                        // Soft glowing outer pulse ring
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .scale(acceptButtonGlowScale)
                                .background(
                                    color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                    shape = CircleShape
                                )
                        )

                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(76.dp)
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
                                contentDescription = "Accept Call",
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Accept", style = MaterialTheme.typography.labelMedium, color = GoldBright, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
