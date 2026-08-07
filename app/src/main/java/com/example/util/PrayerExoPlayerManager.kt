package com.example.util

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Media3 ExoPlayer Manager for multi-language prayer audio playback (English & Latin).
 * Controls ExoPlayer initialization, audio track selection, speed adjustment, and media state.
 */
class PrayerExoPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    private val _isPlayerReady = MutableStateFlow(false)
    val isPlayerReady: StateFlow<Boolean> = _isPlayerReady.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentLanguage = MutableStateFlow("la") // "la" for Latin, "en" for English
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _currentMediaId = MutableStateFlow<String?>(null)
    val currentMediaId: StateFlow<String?> = _currentMediaId.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> _isPlayerReady.value = true
                Player.STATE_ENDED -> {
                    _isPlaying.value = false
                    _currentMediaId.value = null
                }
                Player.STATE_IDLE, Player.STATE_BUFFERING -> {}
            }
        }
    }

    init {
        initializePlayer()
    }

    /**
     * Initializes the Media3 ExoPlayer instance.
     */
    fun initializePlayer() {
        if (exoPlayer == null) {
            try {
                exoPlayer = ExoPlayer.Builder(context.applicationContext)
                    .build()
                    .apply {
                        addListener(playerListener)
                        // Configure initial track selection for preferred audio language
                        updateTrackLanguage(_currentLanguage.value)
                    }
                Log.d("PrayerExoPlayerManager", "Media3 ExoPlayer successfully initialized.")
            } catch (e: Exception) {
                Log.e("PrayerExoPlayerManager", "Failed to initialize Media3 ExoPlayer", e)
            }
        }
    }

    /**
     * Updates track selection parameters based on active user language settings.
     * @param languageCode "la" for Latin, "en" for English
     */
    fun updateTrackLanguage(languageCode: String) {
        _currentLanguage.value = languageCode
        exoPlayer?.let { player ->
            val parameters: TrackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setPreferredAudioLanguage(languageCode)
                .build()
            player.trackSelectionParameters = parameters
            Log.d("PrayerExoPlayerManager", "Switched ExoPlayer preferred audio track language to: $languageCode")
        }
    }

    /**
     * Toggles language track selection based on active user setting boolean.
     * @param isLatin true for Latin ("la"), false for English ("en")
     */
    fun toggleLanguageTrack(isLatin: Boolean) {
        val langCode = if (isLatin) "la" else "en"
        updateTrackLanguage(langCode)
    }

    /**
     * Plays a prayer audio track given a URI or URL with metadata.
     */
    fun playPrayerAudio(
        id: String,
        mediaUri: String,
        title: String,
        isLatin: Boolean = true,
        speed: Float = 1.0f
    ) {
        initializePlayer()
        toggleLanguageTrack(isLatin)

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setSubtitle(if (isLatin) "Latin Prayer" else "English Prayer")
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(mediaUri)
            .setMediaMetadata(mediaMetadata)
            .build()

        exoPlayer?.let { player ->
            player.setMediaItem(mediaItem)
            player.playbackParameters = PlaybackParameters(speed)
            player.prepare()
            player.play()
            _currentMediaId.value = id
        }
    }

    /**
     * Sets playback speed on ExoPlayer.
     */
    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    /**
     * Pauses playback.
     */
    fun pause() {
        exoPlayer?.pause()
    }

    /**
     * Stops playback and resets media ID.
     */
    fun stop() {
        exoPlayer?.stop()
        _currentMediaId.value = null
        _isPlaying.value = false
    }

    /**
     * Releases ExoPlayer resources.
     */
    fun release() {
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        _isPlayerReady.value = false
        _isPlaying.value = false
        _currentMediaId.value = null
        Log.d("PrayerExoPlayerManager", "Media3 ExoPlayer instance released.")
    }

    fun getExoPlayer(): ExoPlayer? = exoPlayer
}
