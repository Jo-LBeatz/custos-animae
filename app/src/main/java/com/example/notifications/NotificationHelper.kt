package com.example.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_DUTIES = "custos_duties_banner_v12"
    const val CHANNEL_SAINTS = "custos_saints_banner_v12"
    const val CHANNEL_CALLS = "custos_calls_channel_v12"

    private val legacyChannelIds = listOf(
        "custos_duties_channel_v7", "custos_saints_channel_v7", "custos_calls_channel_v1",
        "custos_duties_channel_v6", "custos_duties_channel_v5", "custos_duties_channel",
        "custos_duties_banner_channel_v8", "custos_saints_channel_v8", "custos_calls_channel_v8"
    )

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Clean up any legacy channels with outdated/low importance
                for (legacyId in legacyChannelIds) {
                    try {
                        manager.deleteNotificationChannel(legacyId)
                    } catch (_: Exception) {}
                }

                val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val callAudioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                val vibrationPattern = longArrayOf(0, 350, 200, 350)

                val dutiesChannel = NotificationChannel(
                    CHANNEL_DUTIES,
                    "Spiritual Duties & Prayers (Banner)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Heads-up banner alerts with monastic church bell for Catholic rule of life, prayers, and duties."
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                    this.vibrationPattern = vibrationPattern
                    enableLights(true)
                    lightColor = 0xFFD4AF37.toInt()
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setShowBadge(true)
                }

                val saintsChannel = NotificationChannel(
                    CHANNEL_SAINTS,
                    "Daily Saint Meditation (Banner)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Daily invitation to meditate on the Saint of the Day with monastic church bell."
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                    this.vibrationPattern = vibrationPattern
                    enableLights(true)
                    lightColor = 0xFFD4AF37.toInt()
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setShowBadge(true)
                }

                val callsChannel = NotificationChannel(
                    CHANNEL_CALLS,
                    "Rule of Life Incoming Calls",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Incoming call style notifications for Rule of Life prayer schedules."
                    setSound(soundUri, callAudioAttributes)
                    enableVibration(true)
                    this.vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 1000)
                    enableLights(true)
                    lightColor = 0xFFD4AF37.toInt()
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setShowBadge(true)
                    try {
                        setBypassDnd(true)
                    } catch (_: Exception) {}
                }

                manager.createNotificationChannel(dutiesChannel)
                manager.createNotificationChannel(saintsChannel)
                manager.createNotificationChannel(callsChannel)
                Log.d("NotificationHelper", "Notification channels created with IMPORTANCE_HIGH")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showIncomingCallNotification(context: Context, title: String, message: String, notificationId: Int = 1001) {
        createNotificationChannels(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val answerIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_ANSWER_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val answerPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val declineIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_DECLINE_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_SNOOZE_15
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 3,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)

        // Post high-priority incoming call notification with heads-up display and direct actions
        val callNotification = NotificationCompat.Builder(context, CHANNEL_CALLS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Rule of Life: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 1000))
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(android.R.drawable.ic_menu_call, "Pick Up", answerPendingIntent)
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Busy (+15m)", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declinePendingIntent)
            .build()

        try {
            manager.notify(notificationId, callNotification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Start ringing audio & vibration
        try {
            CallRingerManager.startRinging(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Launch full screen call activity safely (system setFullScreenIntent handles locked device display)
        try {
            context.startActivity(fullScreenIntent)
        } catch (e: Exception) {
            Log.d("NotificationHelper", "Direct startActivity deferred to setFullScreenIntent: ${e.message}")
        }

        // Maintain background call service queue safely
        val serviceIntent = Intent(context, RuleOfLifeCallService::class.java).apply {
            action = RuleOfLifeCallService.ACTION_START_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showNotification(context: Context, title: String, message: String, notificationId: Int = 1001) {
        createNotificationChannels(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Stop any call ringer if ringing
        try {
            CallRingerManager.stopRinging()
        } catch (_: Exception) {}

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)

        val markDoneIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_MARK_COMPLETED
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 5,
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_SNOOZE_15
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 4,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Select channel: Saints vs Duties
        val channelId = if (notificationId == 9999) CHANNEL_SAINTS else CHANNEL_DUTIES

        // Build heads-up system banner notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Rule of Life: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 350, 200, 350))
            .setLights(0xFFD4AF37.toInt(), 500, 1000)
            .setColor(0xFFD4AF37.toInt())
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .addAction(android.R.drawable.checkbox_on_background, "Mark Done ✓", markDonePendingIntent)
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Busy (+15m)", snoozePendingIntent)

        try {
            manager.notify(notificationId, builder.build())
            Log.d("NotificationHelper", "Banner notification posted successfully: id=$notificationId title=$title")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to post banner notification", e)
        }
    }
}

