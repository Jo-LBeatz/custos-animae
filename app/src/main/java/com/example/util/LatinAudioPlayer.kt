package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale

class LatinAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var activeLanguageLocale: Locale = Locale.ENGLISH

    fun setLanguageLocale(locale: Locale) {
        activeLanguageLocale = locale
        setupLanguage()
    }

    // Media3 ExoPlayer instance manager for audio streams / multi-language audio tracks
    val exoPlayerManager: PrayerExoPlayerManager by lazy { PrayerExoPlayerManager(context) }

    private val _onAudioCompleted = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val onAudioCompleted: SharedFlow<String> = _onAudioCompleted.asSharedFlow()

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    data class AudioPlaybackState(
        val isPlaying: Boolean = false,
        val currentId: String? = null,
        val currentText: String? = null,
        val speed: Float = 0.80f,
        val pitch: Float = 1.0f,
        val useEcclesiastical: Boolean = true,
        val isLatin: Boolean = true,
        val isLooping: Boolean = false
    )

    init {
        initializeTts()
    }

    private fun initializeTts() {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("LatinAudioPlayer", "Error creating TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupLanguage()
            setupProgressListener()
        } else {
            Log.e("LatinAudioPlayer", "TTS Initialization failed")
        }
    }

    private fun setupLanguage(isLatin: Boolean = _playbackState.value.isLatin, useEcclesiastical: Boolean = _playbackState.value.useEcclesiastical) {
        // Sync Media3 ExoPlayer track selection parameters for active user settings
        exoPlayerManager.toggleLanguageTrack(isLatin)

        tts?.let { t ->
            if (!isLatin) {
                if (activeLanguageLocale != Locale.ENGLISH && t.isLanguageAvailable(activeLanguageLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    t.language = activeLanguageLocale
                    Log.d("LatinAudioPlayer", "Using custom selected language locale: $activeLanguageLocale")
                } else {
                    // English Audio with English (UK / British accent) for traditional English prayers
                    val ukLocale = Locale.UK
                    if (t.isLanguageAvailable(ukLocale) >= TextToSpeech.LANG_AVAILABLE) {
                        t.language = ukLocale
                        Log.d("LatinAudioPlayer", "Using English UK (British accent) voice for prayer")
                    } else {
                        t.language = Locale.ENGLISH
                        Log.d("LatinAudioPlayer", "Using default English voice for prayer")
                    }
                }
            } else {
                val laLocale = Locale.forLanguageTag("la")
                val isLatinAvailable = t.isLanguageAvailable(laLocale)
                
                if (useEcclesiastical) {
                    // Ecclesiastical Latin pronunciation is phonetically almost identical to Italian
                    val itLocale = Locale.ITALY
                    if (t.isLanguageAvailable(itLocale) >= TextToSpeech.LANG_AVAILABLE) {
                        t.language = itLocale
                        Log.d("LatinAudioPlayer", "Using Ecclesiastical Latin (Italian fallback) voice")
                    } else if (isLatinAvailable >= TextToSpeech.LANG_AVAILABLE) {
                        t.language = laLocale
                        Log.d("LatinAudioPlayer", "Using Latin voice")
                    } else {
                        t.language = Locale.UK
                        Log.d("LatinAudioPlayer", "Using default voice")
                    }
                } else {
                    if (isLatinAvailable >= TextToSpeech.LANG_AVAILABLE) {
                        t.language = laLocale
                        Log.d("LatinAudioPlayer", "Using standard Latin voice")
                    } else {
                        t.language = Locale.ITALY
                        Log.d("LatinAudioPlayer", "Fallback to Italian voice")
                    }
                }
            }
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
            }

            override fun onDone(utteranceId: String?) {
                val state = _playbackState.value
                if (state.isLooping && state.currentId == utteranceId && !state.currentText.isNullOrBlank()) {
                    val cleanedText = cleanLatinText(state.currentText)
                    val params = android.os.Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                    tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                } else {
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentId = null, currentText = null)
                    utteranceId?.let { _onAudioCompleted.tryEmit(it) }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _playbackState.value = _playbackState.value.copy(isPlaying = false, currentId = null, currentText = null)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _playbackState.value = _playbackState.value.copy(isPlaying = false, currentId = null, currentText = null)
            }
        })
    }

    fun play(
        id: String,
        text: String,
        speed: Float = 0.80f,
        pitch: Float = 1.0f,
        useEcclesiastical: Boolean = true,
        isLatin: Boolean = true
    ) {
        if (!isInitialized) {
            initializeTts()
            return
        }

        // Stop current speaking directly
        tts?.stop()

        _playbackState.value = AudioPlaybackState(
            isPlaying = true,
            currentId = id,
            currentText = text,
            speed = speed,
            pitch = pitch,
            useEcclesiastical = useEcclesiastical,
            isLatin = isLatin,
            isLooping = _playbackState.value.isLooping
        )

        // Set speed and pitch
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
        
        // Setup proper locale
        setupLanguage(isLatin = isLatin, useEcclesiastical = useEcclesiastical)

        // Clean text from symbols
        val cleanedText = cleanLatinText(text)

        // Speak
        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id)
        tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, id)
    }

    fun stop() {
        tts?.stop()
        _playbackState.value = _playbackState.value.copy(isPlaying = false, currentId = null, currentText = null)
    }

    fun setSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(speed = speed)
        tts?.setSpeechRate(speed)
    }

    fun toggleEcclesiastical(useEcclesiastical: Boolean) {
        _playbackState.value = _playbackState.value.copy(useEcclesiastical = useEcclesiastical)
        setupLanguage()
    }

    fun toggleLooping() {
        _playbackState.value = _playbackState.value.copy(isLooping = !_playbackState.value.isLooping)
    }

    fun release() {
        try {
            exoPlayerManager.release()
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("LatinAudioPlayer", "Error shutting down TTS or ExoPlayer", e)
        }
    }

    private fun cleanLatinText(input: String): String {
        return input
            .replace("☩", "")
            .replace("✔", "")
            .replace("✘", "")
            .replace("•", " ")
            .replace(Regex("[\\r\\n]+"), ". ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
