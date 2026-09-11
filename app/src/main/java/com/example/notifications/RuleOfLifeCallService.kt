package com.example.notifications

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PendingCall(
    val notificationId: Int,
    val title: String,
    val message: String
)

class RuleOfLifeCallService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val title = intent?.getStringExtra("EXTRA_CALL_TITLE") ?: "Rule of Life Call"
        val message = intent?.getStringExtra("EXTRA_CALL_MESSAGE") ?: "Time for your scheduled spiritual duty."
        val notificationId = intent?.getIntExtra("EXTRA_NOTIFICATION_ID", 1001) ?: 1001

        Log.d("RuleOfLifeCallService", "onStartCommand action=$action notificationId=$notificationId title=$title")

        if (action == ACTION_STOP_CALL) {
            serviceScope.launch {
                queueMutex.withLock {
                    handleStopCallLocked(notificationId)
                }
            }
            return START_NOT_STICKY
        }

        // Post foreground notification immediately on startCommand ONLY when starting a call
        try {
            val initialNotification = buildForegroundNotification(title, message, notificationId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startForeground(notificationId, initialNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL)
                } catch (se: SecurityException) {
                    startForeground(notificationId, initialNotification)
                }
            } else {
                startForeground(notificationId, initialNotification)
            }
        } catch (e: Exception) {
            Log.e("RuleOfLifeCallService", "Failed to startForeground synchronously in onStartCommand", e)
        }

        if (action == ACTION_START_CALL || action == null) {
            val pendingCall = PendingCall(notificationId, title, message)
            serviceScope.launch {
                queueMutex.withLock {
                    handleStartCallLocked(pendingCall)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun handleStartCallLocked(call: PendingCall) {
        val exists = (currentCall?.notificationId == call.notificationId) ||
                callQueue.any { it.notificationId == call.notificationId }
        if (!exists) {
            callQueue.addLast(call)
            Log.d("RuleOfLifeCallService", "Enqueued call notificationId=${call.notificationId}. Queue size=${callQueue.size}")
        }

        if (currentCall == null) {
            processNextCallLocked()
        } else {
            Log.d("RuleOfLifeCallService", "A call (id=${currentCall?.notificationId}) is currently active. Call ${call.notificationId} queued to ring next.")
        }
    }

    private fun handleStopCallLocked(stoppedNotificationId: Int) {
        Log.d("RuleOfLifeCallService", "handleStopCall for id=$stoppedNotificationId. Active id=${currentCall?.notificationId}")
        
        if (stoppedNotificationId != -1) {
            callQueue.removeAll { it.notificationId == stoppedNotificationId }
            if (currentCall?.notificationId == stoppedNotificationId) {
                currentCall = null
            }
        } else {
            if (currentCall != null) {
                callQueue.removeAll { it.notificationId == currentCall?.notificationId }
                currentCall = null
            } else if (callQueue.isNotEmpty()) {
                callQueue.removeFirst()
            }
        }

        CallRingerManager.stopRinging()

        if (callQueue.isNotEmpty()) {
            Log.d("RuleOfLifeCallService", "${callQueue.size} call(s) remaining in queue. Ringing next call...")
            processNextCallLocked()
        } else {
            Log.d("RuleOfLifeCallService", "No more calls in queue. Stopping Call Service.")
            stopCallServiceInternalLocked()
        }
    }

    private fun processNextCallLocked() {
        val activeCall = callQueue.firstOrNull()
        if (activeCall == null) {
            stopCallServiceInternalLocked()
            return
        }
        currentCall = activeCall

        Log.d("RuleOfLifeCallService", "Processing active call with Mutex lock: id=${activeCall.notificationId} title=${activeCall.title}")

        // Acquire WakeLock via WakeLockManager to keep CPU active
        WakeLockManager.acquireWakeLock(this)

        // Start playing ringtone and vibration
        CallRingerManager.startRinging(this)

        // Build Foreground Notification
        val notification = buildForegroundNotification(activeCall.title, activeCall.message, activeCall.notificationId)

        // Start Foreground
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startForeground(activeCall.notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL)
                } catch (se: SecurityException) {
                    startForeground(activeCall.notificationId, notification)
                }
            } else {
                startForeground(activeCall.notificationId, notification)
            }
        } catch (e: Exception) {
            Log.e("RuleOfLifeCallService", "Failed to startForeground in processNextCallLocked", e)
        }

        // Launch full screen IncomingCallActivity
        launchCallActivity(activeCall)
    }

    private fun launchCallActivity(call: PendingCall) {
        val directCallIntent = Intent(this, IncomingCallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("EXTRA_NOTIFICATION_ID", call.notificationId)
            putExtra("EXTRA_CALL_TITLE", call.title)
            putExtra("EXTRA_CALL_MESSAGE", call.message)
        }
        try {
            startActivity(directCallIntent)
        } catch (e: Exception) {
            Log.d("RuleOfLifeCallService", "Direct startActivity deferred to setFullScreenIntent: ${e.message}")
        }
    }

    private fun buildForegroundNotification(title: String, message: String, notificationId: Int): Notification {
        NotificationHelper.createNotificationChannels(this)

        val soundUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.church_bell)

        // PendingIntent for Answer Call Action Button
        val answerIntent = Intent(this, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_ANSWER_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val answerPendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId * 10 + 1,
            answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent for Decline Call Action Button
        val declineIntent = Intent(this, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_DECLINE_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId * 10 + 2,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent for Busy / Snooze Action Button (+15m)
        val snoozeIntent = Intent(this, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_SNOOZE_15
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId * 10 + 4,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Full-Screen Intent launching IncomingCallActivity with Pick Up / Decline buttons
        val fullScreenIntent = Intent(this, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            notificationId * 10 + 3,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_CALLS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incoming Prayer Call: $title")
            .setContentText("$message (Tap to open call screen)")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\n📞 Tap to open call screen with Answer & Decline controls."))
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(soundUri, android.media.AudioManager.STREAM_ALARM)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 1000))
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_call, "Answer Call", answerPendingIntent)
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Busy (+15m)", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declinePendingIntent)
            .build()
    }

    private fun stopCallServiceInternalLocked() {
        Log.d("RuleOfLifeCallService", "Stopping Foreground Service")
        CallRingerManager.stopRinging()
        WakeLockManager.releaseWakeLock()
        callQueue.clear()
        currentCall = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        CallRingerManager.stopRinging()
        WakeLockManager.releaseWakeLock()
        callQueue.clear()
        currentCall = null
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_CALL = "com.example.notifications.ACTION_START_CALL"
        const val ACTION_STOP_CALL = "com.example.notifications.ACTION_STOP_CALL"

        private val queueMutex = Mutex()
        private val callQueue = ArrayDeque<PendingCall>()
        @Volatile private var currentCall: PendingCall? = null

        fun stopCall(context: Context, notificationId: Int = -1) {
            try {
                if (currentCall == null && callQueue.isEmpty()) {
                    CallRingerManager.stopRinging()
                    WakeLockManager.releaseWakeLock()
                    return
                }
                val intent = Intent(context, RuleOfLifeCallService::class.java).apply {
                    action = ACTION_STOP_CALL
                    putExtra("EXTRA_NOTIFICATION_ID", notificationId)
                }
                context.startService(intent)
            } catch (e: Exception) {
                Log.w("RuleOfLifeCallService", "Could not send stopCall intent", e)
            }
        }
    }
}

