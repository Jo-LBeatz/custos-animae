package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_DUTIES = "custos_duties_channel_v7"
    const val CHANNEL_SAINTS = "custos_saints_channel_v7"
    const val CHANNEL_CALLS = "custos_calls_channel_v1"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val vibrationPattern = longArrayOf(0, 300, 200, 300, 200, 500)

            val dutiesChannel = NotificationChannel(
                CHANNEL_DUTIES,
                "Spiritual Duties & Prayers",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely respectful reminders with monastic church bell for Catholic rule of life, prayers, and duties."
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                this.vibrationPattern = vibrationPattern
                enableLights(true)
                lightColor = 0xFFD4AF37.toInt()
            }

            val saintsChannel = NotificationChannel(
                CHANNEL_SAINTS,
                "Daily Saint Meditation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily invitation to meditate on the Saint of the Day with monastic church bell."
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                this.vibrationPattern = vibrationPattern
                enableLights(true)
                lightColor = 0xFFD4AF37.toInt()
            }

            val callsChannel = NotificationChannel(
                CHANNEL_CALLS,
                "Rule of Life Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming call style notifications with banner for Rule of Life prayer schedules."
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                this.vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 1000)
                enableLights(true)
                lightColor = 0xFFD4AF37.toInt()
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(dutiesChannel)
            manager.createNotificationChannel(saintsChannel)
            manager.createNotificationChannel(callsChannel)
        }
    }

    fun showIncomingCallNotification(context: Context, title: String, message: String, notificationId: Int = 1001) {
        val serviceIntent = Intent(context, RuleOfLifeCallService::class.java).apply {
            action = RuleOfLifeCallService.ACTION_START_CALL
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
            putExtra("EXTRA_CALL_TITLE", title)
            putExtra("EXTRA_CALL_MESSAGE", message)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun showNotification(context: Context, title: String, message: String, notificationId: Int = 1001) {
        createNotificationChannels(context)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)

        val builder = NotificationCompat.Builder(context, CHANNEL_DUTIES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }
}
