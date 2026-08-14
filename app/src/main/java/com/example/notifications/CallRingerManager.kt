package com.example.notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
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
    private var activeRingtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var isRinging = false

    @Synchronized
    fun startRinging(context: Context) {
        if (isRinging) {
            Log.d("CallRingerManager", "Already ringing.")
            return
        }
        stopRinging()
        isRinging = true
        Log.d("CallRingerManager", "startRinging() initiated.")

        val appContext = context.applicationContext

        // Audio attributes configured for ALARM / RINGTONE usage to ensure sound plays even in Silent & DND modes
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        var soundStarted = false

        // 1. Try Custom Ringtone if set by user
        try {
            val sharedPrefs = appContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val customRingtoneUriString = sharedPrefs.getString("custom_ringtone_uri", null)

            if (!customRingtoneUriString.isNullOrEmpty()) {
                try {
                    val customUri = Uri.parse(customRingtoneUriString)
                    val ringtone = RingtoneManager.getRingtone(appContext, customUri)
                    if (ringtone != null) {
                        ringtone.audioAttributes = audioAttributes
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ringtone.isLooping = true
                        }
                        ringtone.play()
                        activeRingtone = ringtone
                        soundStarted = true
                        Log.d("CallRingerManager", "Playing custom ringtone via RingtoneManager: $customRingtoneUriString")
                    }
                } catch (e: Exception) {
                    Log.w("CallRingerManager", "Could not play custom ringtone URI: $customRingtoneUriString", e)
                }
            }
        } catch (e: Exception) {
            Log.w("CallRingerManager", "Error checking custom ringtone preference", e)
        }

        // 2. Try Church Bell WAV file safely using openRawResourceFd
        if (!soundStarted) {
            try {
                val afd = appContext.resources.openRawResourceFd(R.raw.church_bell)
                if (afd != null) {
                    val player = MediaPlayer()
                    player.setOnErrorListener { _, what, extra ->
                        Log.w("CallRingerManager", "Church bell MediaPlayer handled error: what=$what, extra=$extra")
                        true
                    }
                    player.setAudioAttributes(audioAttributes)
                    player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    player.isLooping = true
                    player.prepare()
                    player.start()
                    mediaPlayer = player
                    soundStarted = true
                    Log.d("CallRingerManager", "Playing church bell sound successfully.")
                }
            } catch (e: Exception) {
                Log.w("CallRingerManager", "Could not play church_bell raw resource, falling back to system ringtone", e)
            }
        }

        // 3. Fallback to default System Ringtone or Alarm sound using RingtoneManager
        if (!soundStarted) {
            try {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                if (defaultUri != null) {
                    val ringtone = RingtoneManager.getRingtone(appContext, defaultUri)
                    if (ringtone != null) {
                        ringtone.audioAttributes = audioAttributes
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ringtone.isLooping = true
                        }
                        ringtone.play()
                        activeRingtone = ringtone
                        soundStarted = true
                        Log.d("CallRingerManager", "Playing system fallback ringtone.")
                    }
                }
            } catch (e: Exception) {
                Log.e("CallRingerManager", "Fallback ringtone failed", e)
            }
        }

        // 4. Trigger Haptic Feedback (Vibration)
        try {
            val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
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
            Log.w("CallRingerManager", "Error starting vibrator", e)
        }
    }

    @Synchronized
    fun stopRinging() {
        isRinging = false
        Log.d("CallRingerManager", "stopRinging() called.")

        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            }
        } catch (e: Exception) {
            Log.w("CallRingerManager", "Error stopping/releasing media player", e)
        }
        mediaPlayer = null

        try {
            activeRingtone?.let { ringtone ->
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }
        } catch (e: Exception) {
            Log.w("CallRingerManager", "Error stopping ringtone", e)
        }
        activeRingtone = null

        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.w("CallRingerManager", "Error cancelling vibrator", e)
        }
        vibrator = null
    }

    fun isRinging(): Boolean = isRinging
}
