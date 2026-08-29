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
    const val CHANNEL_NOVENAS = "custos_novenas_channel_v1"

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

            val novenasChannel = NotificationChannel(
                CHANNEL_NOVENAS,
                "📿 Feast Novena Start Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special banner notifications on the starting day of traditional Catholic feast novenas."
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                // Unique 9-beat rhythmic chime pattern representing 9 days of novena prayer
                this.vibrationPattern = longArrayOf(0, 150, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150, 100, 400)
                enableLights(true)
                lightColor = 0xFF4A148C.toInt()
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(dutiesChannel)
            manager.createNotificationChannel(saintsChannel)
            manager.createNotificationChannel(callsChannel)
            manager.createNotificationChannel(novenasChannel)
        }
    }

    fun showIncomingCallNotification(context: Context, title: String, message: String, notificationId: Int = 1001) {
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
            try {
                context.startService(serviceIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                showNotification(context, "Incoming Prayer Call: $title", message, notificationId)
            }
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

    fun showNovenaStartBannerNotification(
        context: Context,
        novena: com.example.data.novena.NovenaDefinition,
        notificationId: Int = 8001
    ) {
        createNotificationChannels(context)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = "ACTION_OPEN_NOVENA"
            putExtra("EXTRA_NOVENA_ID", novena.id)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.church_bell)

        val titleText = "📿 NOVENA STARTING TODAY | ${novena.novenaTitle}"
        val messageText = "Today is Day 1 of the ${novena.novenaTitle} (Feast: ${novena.feastTitle}). Begin your 9 days of holy prayer today!"

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle(titleText)
            .setSummaryText("📿 Holy Novena Start Banner")
            .bigText(
                "Feast Day: ${novena.feastTitle}\n\n" +
                "Intention: ${novena.intention}\n\n" +
                "\"${novena.summary}\"\n\n" +
                "Tap below to open and pray Day 1 of this 9-day devotion."
            )

        val builder = NotificationCompat.Builder(context, CHANNEL_NOVENAS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleText)
            .setContentText(messageText)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setSound(soundUri)
            .setColor(0xFFD4AF37.toInt())
            .setSubText("📿 Novena Start Reminder")
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "📿 OPEN NOVENA PRAYER",
                contentPendingIntent
            )
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }
}
