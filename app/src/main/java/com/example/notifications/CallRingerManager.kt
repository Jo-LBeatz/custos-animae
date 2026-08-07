package com.example.notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.R

object CallRingerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var ringtoneManager: RingtoneManager? = null
    private var isRinging = false

    @Synchronized
    fun startRinging(context: Context) {
        if (isRinging) {
            Log.d("CallRingerManager", "Already ringing.")
            return
        }
        isRinging = true
        Log.d("CallRingerManager", "startRinging() called with custom RingtoneManager and DND bypass.")

        // Audio attributes configured for ALARM usage to ensure sound plays even in Silent & Do Not Disturb modes
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        // Initialize custom RingtoneManager instance
        val customRingtoneManager = RingtoneManager(context.applicationContext).apply {
            setType(RingtoneManager.TYPE_RINGTONE)
        }
        ringtoneManager = customRingtoneManager

        // 1. Play Ringtone with USAGE_ALARM stream to bypass Silent mode / Do Not Disturb
        try {
            val sharedPrefs = context.applicationContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val customRingtoneUriString = sharedPrefs.getString("custom_ringtone_uri", null)

            var player: MediaPlayer? = null

            if (!customRingtoneUriString.isNullOrEmpty()) {
                try {
                    val customUri = Uri.parse(customRingtoneUriString)
                    player = MediaPlayer().apply {
                        setAudioAttributes(audioAttributes)
                        setDataSource(context.applicationContext, customUri)
                        prepare()
                    }
                    Log.d("CallRingerManager", "Successfully prepared custom ringtone from: $customRingtoneUriString")
                } catch (e: Exception) {
                    Log.e("CallRingerManager", "Failed to load custom ringtone $customRingtoneUriString, falling back to default", e)
                    player = null
                }
            }

            if (player == null) {
                player = try {
                    MediaPlayer.create(context.applicationContext, R.raw.church_bell, audioAttributes, 0)
                } catch (e: Exception) {
                    null
                }
            }

            if (player == null) {
                try {
                    val ringtoneUri: Uri? = RingtoneManager.getActualDefaultRingtoneUri(context.applicationContext, RingtoneManager.TYPE_RINGTONE)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

                    if (ringtoneUri != null) {
                        player = MediaPlayer().apply {
                            setAudioAttributes(audioAttributes)
                            setDataSource(context.applicationContext, ringtoneUri)
                            prepare()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CallRingerManager", "Failed to load fallback ringtone via RingtoneManager", e)
                }
            }

            player?.apply {
                isLooping = true
                setOnErrorListener { _, what, extra ->
                    Log.w("CallRingerManager", "MediaPlayer error suppressed: what=$what, extra=$extra")
                    true
                }
                start()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e("CallRingerManager", "Error starting media player", e)
        }

        // 2. Trigger Haptic Feedback (Vibration) synchronously with incoming audio
        try {
            val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            vibrator = v
            val pattern = longArrayOf(0, 1000, 500, 1000, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(pattern, 0)
                v?.vibrate(vibrationEffect, audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                v?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e("CallRingerManager", "Error starting vibrator", e)
        }
    }

    @Synchronized
    fun stopRinging() {
        if (!isRinging) return
        isRinging = false
        Log.d("CallRingerManager", "stopRinging() called.")

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("CallRingerManager", "Error stopping media player", e)
        }

        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            Log.e("CallRingerManager", "Error stopping vibrator", e)
        }

        ringtoneManager = null
    }

    fun isRinging(): Boolean = isRinging
}
