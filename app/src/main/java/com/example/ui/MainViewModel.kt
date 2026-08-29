package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalendarEntry
import com.example.data.DailyExamination
import com.example.data.SaintReflection
import com.example.data.SpiritualDuty
import com.example.data.SpiritualRepository
import com.example.data.UserProfile
import com.example.data.PrayerEntity
import com.example.data.calendar.LiturgicalDayInfo
import com.example.data.calendar.Roman1954Calendar
import com.example.data.calendar.SaintData
import com.example.data.calendar.SaintProfile
import com.example.notifications.NotificationHelper
import com.example.notifications.NotificationScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper

enum class PageViewMode {
    PURE_CONTENT,       // View WITHOUT top and bottom elements for pure content visibility
    STANDARD_WITH_BARS, // View WITH top and bottom elements always visible
    DYNAMIC_AUTO_HIDE   // Dynamic auto-hide top/bottom elements on scroll
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SpiritualRepository

    val userProfile: StateFlow<UserProfile?>
    val allDuties: StateFlow<List<SpiritualDuty>>
    val bookmarkedSaints: StateFlow<List<SaintReflection>>
    val allSaintReflections: StateFlow<List<SaintReflection>>
    val allCalendarEntries: StateFlow<List<CalendarEntry>>
    val recentExaminations: StateFlow<List<DailyExamination>>
    val todaySaintQuotes: StateFlow<List<com.example.data.SaintQuoteEntity>>
    val allNovenasFromDb: StateFlow<List<com.example.data.NovenaEntity>>
    val allNovenaProgressLogs: StateFlow<List<com.example.data.NovenaProgressEntity>>

    // Selected Tab
    private val _currentTab = MutableStateFlow(0) // 0: Rule, 1: Saint, 2: Prayers, 3: Examination, 4: Calendar
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Immersive Reading Mode: Bottom Nav Visibility
    private val _isBottomNavVisible = MutableStateFlow(true)
    val isBottomNavVisible: StateFlow<Boolean> = _isBottomNavVisible.asStateFlow()

    // Immersive Reading Mode: Top Header Visibility
    private val _isTopHeaderVisible = MutableStateFlow(true)
    val isTopHeaderVisible: StateFlow<Boolean> = _isTopHeaderVisible.asStateFlow()

    // Central Reading Progress Indicator (0.0f to 1.0f)
    private val _readingProgress = MutableStateFlow(0f)
    val readingProgress: StateFlow<Float> = _readingProgress.asStateFlow()

    // Selected Novena Dialog
    private val _selectedNovenaForDialog = MutableStateFlow<com.example.data.novena.NovenaDefinition?>(null)
    val selectedNovenaForDialog: StateFlow<com.example.data.novena.NovenaDefinition?> = _selectedNovenaForDialog.asStateFlow()

    fun openNovenaDialog(novenaId: String) {
        val found = com.example.data.novena.NovenaData.allNovenas.find { it.id == novenaId }
            ?: com.example.data.novena.NovenaData.getMovableNovenasForYear(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)).find { it.id == novenaId }
            ?: com.example.data.novena.NovenaData.allNovenas.firstOrNull()
        _selectedNovenaForDialog.value = found
    }

    fun openNovenaDialogByDefinition(novena: com.example.data.novena.NovenaDefinition) {
        _selectedNovenaForDialog.value = novena
    }

    fun closeNovenaDialog() {
        _selectedNovenaForDialog.value = null
    }

    fun markNovenaDayPrayed(novenaId: String, dayNumber: Int, notes: String = "") {
        viewModelScope.launch {
            repository.recordNovenaDayPrayed(novenaId, dayNumber, notes)
        }
    }

    // Reading View Text Size Preference (Persists across sessions)
    private val sharedPrefs = application.getSharedPreferences("user_preferences", android.content.Context.MODE_PRIVATE)
    private val _readingTextSize = MutableStateFlow(sharedPrefs.getFloat("reading_text_size", 14f))
    val readingTextSize: StateFlow<Float> = _readingTextSize.asStateFlow()

    // Page Viewing Mode Preference (Pure Content View without top/bottom bars vs Standard with bars vs Dynamic Auto-Hide)
    private val _pageViewMode = MutableStateFlow<PageViewMode>(
        try {
            PageViewMode.valueOf(
                sharedPrefs.getString("page_view_mode", PageViewMode.DYNAMIC_AUTO_HIDE.name)
                    ?: PageViewMode.DYNAMIC_AUTO_HIDE.name
            )
        } catch (e: Exception) {
            PageViewMode.DYNAMIC_AUTO_HIDE
        }
    )
    val pageViewMode: StateFlow<PageViewMode> = _pageViewMode.asStateFlow()

    // Page Visibility Prompt Dialog State
    private val _showPageVisibilityPrompt = MutableStateFlow<Boolean>(
        sharedPrefs.getBoolean("show_page_visibility_prompt_on_launch", true)
    )
    val showPageVisibilityPrompt: StateFlow<Boolean> = _showPageVisibilityPrompt.asStateFlow()

    fun updateReadingTextSize(newSize: Float) {
        viewModelScope.launch {
            _readingTextSize.value = newSize
            sharedPrefs.edit().putFloat("reading_text_size", newSize).apply()
        }
    }

    fun setPageViewMode(mode: PageViewMode) {
        viewModelScope.launch {
            _pageViewMode.value = mode
            sharedPrefs.edit().putString("page_view_mode", mode.name).apply()
            applyPageViewMode(mode)
        }
    }

    fun applyPageViewMode(mode: PageViewMode = _pageViewMode.value) {
        when (mode) {
            PageViewMode.PURE_CONTENT -> {
                _isBottomNavVisible.value = false
                _isTopHeaderVisible.value = false
            }
            PageViewMode.STANDARD_WITH_BARS -> {
                _isBottomNavVisible.value = true
                _isTopHeaderVisible.value = true
            }
            PageViewMode.DYNAMIC_AUTO_HIDE -> {
                _isBottomNavVisible.value = true
                _isTopHeaderVisible.value = true
            }
        }
    }

    fun openPageVisibilityPrompt() {
        _showPageVisibilityPrompt.value = true
    }

    fun dismissPageVisibilityPrompt(dontAskAgainOnLaunch: Boolean = false) {
        _showPageVisibilityPrompt.value = false
        if (dontAskAgainOnLaunch) {
            sharedPrefs.edit().putBoolean("show_page_visibility_prompt_on_launch", false).apply()
        }
    }

    // App UI Theme Preference (Persists across sessions)
    private val _appTheme = MutableStateFlow(sharedPrefs.getString("app_theme", "CATHEDRAL_DARK") ?: "CATHEDRAL_DARK")
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    // Dark Mode Mode Preference ("DARK", "LIGHT", "SYSTEM")
    private val _darkModePreference = MutableStateFlow(
        sharedPrefs.getString(
            "dark_mode_preference",
            if (_appTheme.value == "CATHEDRAL_LIGHT") "LIGHT" else "DARK"
        ) ?: "DARK"
    )
    val darkModePreference: StateFlow<String> = _darkModePreference.asStateFlow()

    fun updateAppTheme(newTheme: String) {
        viewModelScope.launch {
            _appTheme.value = newTheme
            sharedPrefs.edit().putString("app_theme", newTheme).apply()
            // If switching explicitly to Cathedral Light, adjust default dark mode preference to LIGHT
            if (newTheme == "CATHEDRAL_LIGHT" && _darkModePreference.value == "DARK") {
                _darkModePreference.value = "LIGHT"
                sharedPrefs.edit().putString("dark_mode_preference", "LIGHT").apply()
            }
        }
    }

    fun updateDarkModePreference(newMode: String) {
        viewModelScope.launch {
            _darkModePreference.value = newMode
            sharedPrefs.edit().putString("dark_mode_preference", newMode).apply()
        }
    }

    // Custom Ringtone URI Preference for Rule of Life Calls
    private val _customRingtoneUri = MutableStateFlow(sharedPrefs.getString("custom_ringtone_uri", null))
    val customRingtoneUri: StateFlow<String?> = _customRingtoneUri.asStateFlow()

    fun setCustomRingtoneUri(uriString: String?) {
        viewModelScope.launch {
            _customRingtoneUri.value = uriString
            if (uriString != null) {
                sharedPrefs.edit().putString("custom_ringtone_uri", uriString).apply()
            } else {
                sharedPrefs.edit().remove("custom_ringtone_uri").apply()
            }
        }
    }

    // Virtue & Purity Counter State (Days without relapse into sin)
    private val initialCleanSince = sharedPrefs.getLong("virtue_clean_since_timestamp", 0L).let {
        if (it == 0L) {
            val now = System.currentTimeMillis()
            sharedPrefs.edit().putLong("virtue_clean_since_timestamp", now).apply()
            now
        } else {
            it
        }
    }
    private val _virtueCleanSinceTimestamp = MutableStateFlow(initialCleanSince)
    val virtueCleanSinceTimestamp: StateFlow<Long> = _virtueCleanSinceTimestamp.asStateFlow()

    private val _virtueLongestStreakDays = MutableStateFlow(sharedPrefs.getInt("virtue_longest_streak_days", 0))
    val virtueLongestStreakDays: StateFlow<Int> = _virtueLongestStreakDays.asStateFlow()

    private fun parseRelapseLogs(jsonStr: String?): List<com.example.data.RelapseLog> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        val list = mutableListOf<com.example.data.RelapseLog>()
        try {
            val array = org.json.JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    com.example.data.RelapseLog(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        streakDays = obj.optInt("streakDays", 0),
                        streakHours = obj.optInt("streakHours", 0),
                        triggerNote = obj.optString("triggerNote", ""),
                        resolutionNote = obj.optString("resolutionNote", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun serializeRelapseLogs(logs: List<com.example.data.RelapseLog>): String {
        val array = org.json.JSONArray()
        for (log in logs) {
            val obj = org.json.JSONObject()
            obj.put("id", log.id)
            obj.put("timestamp", log.timestamp)
            obj.put("streakDays", log.streakDays)
            obj.put("streakHours", log.streakHours)
            obj.put("triggerNote", log.triggerNote)
            obj.put("resolutionNote", log.resolutionNote)
            array.put(obj)
        }
        return array.toString()
    }

    private val _virtueRelapseLogs = MutableStateFlow(parseRelapseLogs(sharedPrefs.getString("virtue_relapse_logs_json", "[]")))
    val virtueRelapseLogs: StateFlow<List<com.example.data.RelapseLog>> = _virtueRelapseLogs.asStateFlow()

    fun resetVirtueTimer(
        triggerNote: String = "",
        resolutionNote: String = "",
        customResetTimestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val currentStart = _virtueCleanSinceTimestamp.value
            val elapsedMs = maxOf(0L, customResetTimestamp - currentStart)
            val streakDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(elapsedMs).toInt()
            val streakHours = (java.util.concurrent.TimeUnit.MILLISECONDS.toHours(elapsedMs) % 24).toInt()

            val currentLongest = _virtueLongestStreakDays.value
            if (streakDays > currentLongest) {
                _virtueLongestStreakDays.value = streakDays
                sharedPrefs.edit().putInt("virtue_longest_streak_days", streakDays).apply()
            }

            val newLog = com.example.data.RelapseLog(
                timestamp = customResetTimestamp,
                streakDays = streakDays,
                streakHours = streakHours,
                triggerNote = triggerNote,
                resolutionNote = resolutionNote
            )
            val updatedLogs = listOf(newLog) + _virtueRelapseLogs.value
            _virtueRelapseLogs.value = updatedLogs
            sharedPrefs.edit().putString("virtue_relapse_logs_json", serializeRelapseLogs(updatedLogs)).apply()

            _virtueCleanSinceTimestamp.value = customResetTimestamp
            sharedPrefs.edit().putLong("virtue_clean_since_timestamp", customResetTimestamp).apply()
        }
    }

    fun updateVirtueStartTimestamp(newTimestamp: Long) {
        viewModelScope.launch {
            _virtueCleanSinceTimestamp.value = newTimestamp
            sharedPrefs.edit().putLong("virtue_clean_since_timestamp", newTimestamp).apply()
        }
    }

    fun deleteRelapseLog(logId: String) {
        viewModelScope.launch {
            val updated = _virtueRelapseLogs.value.filter { it.id != logId }
            _virtueRelapseLogs.value = updated
            sharedPrefs.edit().putString("virtue_relapse_logs_json", serializeRelapseLogs(updated)).apply()
        }
    }

    // Latin Mode Toggle for Prayer Library
    private val _isLatinMode = MutableStateFlow(false)
    val isLatinMode: StateFlow<Boolean> = _isLatinMode.asStateFlow()

    // Persisted Manual Devotional Intention
    private val _manualDevotionalIntention = MutableStateFlow(sharedPrefs.getString("manual_devotional_intention", "") ?: "")
    val manualDevotionalIntention: StateFlow<String> = _manualDevotionalIntention.asStateFlow()

    fun updateManualDevotionalIntention(intention: String) {
        viewModelScope.launch {
            _manualDevotionalIntention.value = intention
            sharedPrefs.edit().putString("manual_devotional_intention", intention).apply()
        }
    }


    // App-wide language setting (Indian languages + English)
    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromCode(sharedPrefs.getString("app_language_code", AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code)
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun updateAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            _appLanguage.value = language
            sharedPrefs.edit().putString("app_language_code", language.code).apply()
            java.util.Locale.setDefault(language.locale)
            latinAudioPlayer.setLanguageLocale(language.locale)
        }
    }

    // Latin / English Audio Pronunciation & Media3 ExoPlayer Manager
    private val latinAudioPlayer = com.example.util.LatinAudioPlayer(application)
    val latinAudioState = latinAudioPlayer.playbackState
    val onLatinAudioCompleted = latinAudioPlayer.onAudioCompleted

    // Media3 ExoPlayer instance for multi-language audio playback
    val prayerExoPlayerManager = latinAudioPlayer.exoPlayerManager

    fun playPrayerAudioStream(
        id: String,
        uri: String,
        title: String,
        isLatin: Boolean = _isLatinMode.value,
        speed: Float = 1.0f
    ) {
        prayerExoPlayerManager.playPrayerAudio(
            id = id,
            mediaUri = uri,
            title = title,
            isLatin = isLatin,
            speed = speed
        )
    }

    fun setAudioTrackLanguage(isLatin: Boolean) {
        prayerExoPlayerManager.toggleLanguageTrack(isLatin)
    }

    fun playLatinAudio(
        id: String,
        text: String,
        speed: Float = 0.80f,
        useEcclesiastical: Boolean = true,
        isLatin: Boolean = _isLatinMode.value
    ) {
        val finalSpeechText = if (!isLatin) {
            AppTranslationHelper.translatePrayerText(id, text, _appLanguage.value)
        } else {
            text
        }
        latinAudioPlayer.play(
            id = id,
            text = finalSpeechText,
            speed = speed,
            useEcclesiastical = useEcclesiastical,
            isLatin = isLatin
        )
    }

    fun playPrayerAudio(
        id: String,
        text: String,
        speed: Float = 0.80f,
        useEcclesiastical: Boolean = true,
        isLatin: Boolean = _isLatinMode.value
    ) {
        playLatinAudio(id, text, speed, useEcclesiastical, isLatin)
    }

    fun stopLatinAudio() {
        latinAudioPlayer.stop()
    }

    fun setLatinAudioSpeed(speed: Float) {
        latinAudioPlayer.setSpeed(speed)
    }

    fun toggleLatinAudioEcclesiastical(useEcclesiastical: Boolean) {
        latinAudioPlayer.toggleEcclesiastical(useEcclesiastical)
    }

    fun toggleLatinAudioLooping() {
        latinAudioPlayer.toggleLooping()
    }

    // Selected Saint Search/Filter
    private val _saintSearchQuery = MutableStateFlow("")
    val saintSearchQuery: StateFlow<String> = _saintSearchQuery.asStateFlow()

    // 1954 Roman Calendar Local Storage Search & Filter
    private val _calendarSearchQuery = MutableStateFlow("")
    val calendarSearchQuery: StateFlow<String> = _calendarSearchQuery.asStateFlow()

    private val _calendarFilter = MutableStateFlow("ALL") // ALL, VIGILS, CLASS1, FASTING, EMBER
    val calendarFilter: StateFlow<String> = _calendarFilter.asStateFlow()

    val calendarSearchResults: StateFlow<List<CalendarEntry>>

    // Prayer Library Local Search & Bookmarks (100% Offline Room DB)
    private val _prayerSearchQuery = MutableStateFlow("")
    val prayerSearchQuery: StateFlow<String> = _prayerSearchQuery.asStateFlow()
    val prayerSearchResults: StateFlow<List<PrayerEntity>>
    val bookmarkedPrayers: StateFlow<List<PrayerEntity>>

    // Reflection Guard Dialog State
    private val _pendingReflectionDuty = MutableStateFlow<SpiritualDuty?>(null)
    val pendingReflectionDuty: StateFlow<SpiritualDuty?> = _pendingReflectionDuty.asStateFlow()

    // Liturgical Calendar Selected Date
    private val _selectedCalendarDate = MutableStateFlow(Calendar.getInstance())
    val selectedCalendarDate: StateFlow<Calendar> = _selectedCalendarDate.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SpiritualRepository(db.appDao(), application)
        
        // Load initial translation locale settings to audio player
        latinAudioPlayer.setLanguageLocale(_appLanguage.value.locale)

        // Seed 1954 Roman Calendar, Novenas, and Prayers into local Room storage
        viewModelScope.launch {
            repository.ensureCalendarSeeded()
            repository.ensurePrayersSeeded()
            repository.ensureNovenasSeeded()
            repository.resetExpiredDuties()
            repository.hardRefreshForDate(_selectedCalendarDate.value)
            
            // Trigger background worker to fetch or fallback quotes for today's Saint of the Day
            com.example.worker.SaintQuoteWorker.enqueueFetch(application)
        }


        userProfile = repository.userProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )

        allDuties = repository.allDuties.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        bookmarkedSaints = repository.bookmarkedSaints.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allSaintReflections = repository.allSaintReflections.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allCalendarEntries = repository.getAllCalendarEntries().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        recentExaminations = repository.recentExaminations.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allNovenasFromDb = repository.allNovenas.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allNovenaProgressLogs = repository.allNovenaLogs.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        todaySaintQuotes = flowOf(Unit).flatMapLatest {
            val cal = Calendar.getInstance()
            val mmdd = String.format("%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            repository.getQuotesForSaint(mmdd)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        calendarSearchResults = combine(
            _calendarSearchQuery,
            _calendarFilter
        ) { query, filter ->
            Pair(query, filter)
        }.flatMapLatest { (query, filter) ->
            repository.searchCalendarEntries(query).combine(MutableStateFlow(filter)) { list, activeFilter ->
                when (activeFilter) {
                    "VIGILS" -> list.filter { it.isVigil }
                    "CLASS1" -> list.filter { it.feastRank.contains("I Class", ignoreCase = true) }
                    "FASTING" -> list.filter { it.isFastingDay || it.isAbstinenceDay }
                    "EMBER" -> list.filter { it.isEmberDay }
                    else -> list
                }
            }
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        prayerSearchResults = _prayerSearchQuery.flatMapLatest { query ->
            repository.searchPrayers(query)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        bookmarkedPrayers = repository.bookmarkedPrayers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Automatically schedule device notifications whenever duties or user profile are updated
        viewModelScope.launch {
            combine(allDuties, userProfile) { dutiesList, profile ->
                Pair(dutiesList, profile)
            }.collect { (dutiesList, profile) ->
                val saintTime = profile?.saintNotificationTime ?: "07:30"
                com.example.notifications.NotificationScheduler.scheduleAll(
                    application,
                    dutiesList,
                    saintTime,
                    profile
                )
            }
        }
    }

    fun updateSaintNotificationTime(timeStr: String) {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                val updated = profile.copy(saintNotificationTime = timeStr)
                repository.saveUserProfile(updated)
                com.example.notifications.NotificationScheduler.scheduleSaintOfDay(getApplication(), timeStr)
            }
        }
    }

    fun updateNotificationPreferences(
        morningTime: String,
        eveningTime: String,
        saintTime: String,
        enabled: Boolean,
        angelusMorning: String = "06:00",
        angelusNoon: String = "12:00",
        angelusEvening: String = "18:00",
        angelusEnabled: Boolean = true,
        sacredHeartTime: String = "08:00",
        sacredHeartEnabled: Boolean = true,
        immaculateHeartTime: String = "08:00",
        immaculateHeartEnabled: Boolean = true,
        liturgicalSeasonEnabled: Boolean = true,
        novenaNotificationsEnabled: Boolean = true,
        novenaNotificationTime: String = "08:00"
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: com.example.data.UserProfile()
            val updated = current.copy(
                morningPrayerTime = morningTime,
                eveningPrayerTime = eveningTime,
                saintNotificationTime = saintTime,
                notificationEnabled = enabled,
                angelusMorningTime = angelusMorning,
                angelusNoonTime = angelusNoon,
                angelusEveningTime = angelusEvening,
                angelusEnabled = angelusEnabled,
                sacredHeartTime = sacredHeartTime,
                sacredHeartEnabled = sacredHeartEnabled,
                immaculateHeartTime = immaculateHeartTime,
                immaculateHeartEnabled = immaculateHeartEnabled,
                liturgicalSeasonNotificationsEnabled = liturgicalSeasonEnabled,
                novenaNotificationsEnabled = novenaNotificationsEnabled,
                novenaNotificationTime = novenaNotificationTime
            )
            repository.saveUserProfile(updated)
            com.example.notifications.NotificationScheduler.scheduleAll(
                getApplication(),
                allDuties.value,
                saintTime,
                updated
            )
        }
    }

    fun triggerTestNovenaNotification() {
        val intent = android.content.Intent(getApplication(), com.example.notifications.NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TYPE", "TEST_NOVENA_START")
        }
        getApplication<android.app.Application>().sendBroadcast(intent)
    }

    fun updateDutyReminderTime(dutyId: Int, newTime: String) {
        viewModelScope.launch {
            val duty = allDuties.value.find { it.id == dutyId }
            if (duty != null) {
                com.example.notifications.NotificationScheduler.cancelDutyNotification(getApplication(), dutyId)
                val updated = duty.copy(reminderTime = newTime)
                repository.insertDuty(updated)
                val currentDuties = allDuties.value.map { if (it.id == dutyId) updated else it }
                com.example.notifications.NotificationScheduler.scheduleAll(
                    getApplication(),
                    currentDuties,
                    userProfile.value?.saintNotificationTime ?: "07:30",
                    userProfile.value
                )
            }
        }
    }

    fun addCustomPrayerDuty(title: String, description: String, reminderTime: String, category: String = "PRAYER") {
        viewModelScope.launch {
            val newDuty = com.example.data.SpiritualDuty(
                title = title,
                description = description,
                frequency = "DAILY",
                reminderTime = reminderTime,
                priority = "SACRED",
                category = category,
                isCompleted = false
            )
            val newId = repository.insertDuty(newDuty)
            val savedDuty = newDuty.copy(id = newId.toInt())
            com.example.notifications.NotificationScheduler.scheduleAll(
                getApplication(),
                allDuties.value + savedDuty,
                userProfile.value?.saintNotificationTime ?: "07:30",
                userProfile.value
            )
        }
    }

    fun testNotificationNow(title: String, message: String) {
        com.example.notifications.NotificationScheduler.triggerDutyNotificationNow(
            getApplication(),
            title,
            message
        )
    }

    fun setTab(tabIndex: Int) {
        _currentTab.value = tabIndex
        _readingProgress.value = 0f
        when (_pageViewMode.value) {
            PageViewMode.PURE_CONTENT -> {
                _isBottomNavVisible.value = false
                _isTopHeaderVisible.value = false
            }
            PageViewMode.STANDARD_WITH_BARS, PageViewMode.DYNAMIC_AUTO_HIDE -> {
                _isBottomNavVisible.value = true
                _isTopHeaderVisible.value = true
            }
        }
    }

    fun setBottomNavVisible(visible: Boolean) {
        _isBottomNavVisible.value = visible
    }

    fun setTopHeaderVisible(visible: Boolean) {
        _isTopHeaderVisible.value = visible
    }

    fun setReadingProgress(progress: Float) {
        _readingProgress.value = progress.coerceIn(0f, 1f)
    }

    fun toggleLatinMode() {
        val newMode = !_isLatinMode.value
        _isLatinMode.value = newMode
        setAudioTrackLanguage(newMode)
    }

    fun setSaintSearchQuery(query: String) {
        _saintSearchQuery.value = query
    }

    fun setCalendarSearchQuery(query: String) {
        _calendarSearchQuery.value = query
    }

    fun setCalendarFilter(filter: String) {
        _calendarFilter.value = filter
    }

    fun setPrayerSearchQuery(query: String) {
        _prayerSearchQuery.value = query
    }

    fun togglePrayerBookmark(prayerId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.togglePrayerBookmark(prayerId, currentStatus)
        }
    }

    fun setCalendarDate(calendar: Calendar) {
        _selectedCalendarDate.value = calendar
        viewModelScope.launch {
            repository.hardRefreshForDate(calendar)
        }
    }

    fun verifyAndRectifySelectedDate() {
        viewModelScope.launch {
            repository.hardRefreshForDate(_selectedCalendarDate.value)
        }
    }

    fun resetExpiredDuties() {
        viewModelScope.launch {
            repository.resetExpiredDuties()
        }
    }


    fun setupGenderProfile(gender: String) {
        viewModelScope.launch {
            repository.setupUserProfileAndRule(gender)
        }
    }

    fun setupGenderProfileWithNotifications(
        gender: String,
        morningTime: String,
        eveningTime: String,
        saintTime: String,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            val profile = repository.setupUserProfileAndRule(gender)
            val updated = profile.copy(
                morningPrayerTime = morningTime,
                eveningPrayerTime = eveningTime,
                saintNotificationTime = saintTime,
                notificationEnabled = enabled
            )
            repository.saveUserProfile(updated)
            com.example.notifications.NotificationScheduler.scheduleAll(getApplication(), allDuties.value, saintTime, updated)
        }
    }

    fun toggleDutyCompletion(duty: SpiritualDuty) {
        viewModelScope.launch {
            repository.toggleDutyCompletion(duty)
            val isNowCompleted = !duty.isCompleted
            val updatedDuty = duty.copy(isCompleted = isNowCompleted)
            val currentDuties = allDuties.value.map { if (it.id == duty.id) updatedDuty else it }
            com.example.notifications.NotificationScheduler.scheduleAll(
                getApplication(),
                currentDuties,
                userProfile.value?.saintNotificationTime ?: "07:30",
                userProfile.value
            )
        }
    }

    fun checkAndSaveDuty(duty: SpiritualDuty, isConfirmedByGuard: Boolean = false) {
        if (!isConfirmedByGuard && needsSpiritualReflection(duty.title, duty.description)) {
            _pendingReflectionDuty.value = duty
            return
        }

        viewModelScope.launch {
            val isNew = duty.id == 0
            if (!isNew) {
                com.example.notifications.NotificationScheduler.cancelDutyNotification(getApplication(), duty.id)
            }
            val savedDuty = if (isNew) {
                val newId = repository.insertDuty(duty)
                duty.copy(id = newId.toInt())
            } else {
                repository.updateDuty(duty)
                duty
            }

            val currentDuties = if (isNew) allDuties.value + savedDuty else allDuties.value.map { if (it.id == savedDuty.id) savedDuty else it }
            com.example.notifications.NotificationScheduler.scheduleAll(
                getApplication(),
                currentDuties,
                userProfile.value?.saintNotificationTime ?: "07:30",
                userProfile.value
            )
            _pendingReflectionDuty.value = null
        }
    }

    fun confirmPendingDuty() {
        val pending = _pendingReflectionDuty.value ?: return
        viewModelScope.launch {
            val isNew = pending.id == 0
            if (!isNew) {
                com.example.notifications.NotificationScheduler.cancelDutyNotification(getApplication(), pending.id)
            }
            val savedDuty = if (isNew) {
                val newId = repository.insertDuty(pending)
                pending.copy(id = newId.toInt())
            } else {
                repository.updateDuty(pending)
                pending
            }

            val currentDuties = if (isNew) allDuties.value + savedDuty else allDuties.value.map { if (it.id == savedDuty.id) savedDuty else it }
            com.example.notifications.NotificationScheduler.scheduleAll(
                getApplication(),
                currentDuties,
                userProfile.value?.saintNotificationTime ?: "07:30",
                userProfile.value
            )
            _pendingReflectionDuty.value = null
        }
    }

    fun dismissPendingDuty() {
        _pendingReflectionDuty.value = null
    }

    fun deleteDuty(dutyId: Int) {
        viewModelScope.launch {
            repository.deleteDuty(dutyId)
            com.example.notifications.NotificationScheduler.cancelDutyNotification(getApplication(), dutyId)
        }
    }

    private fun needsSpiritualReflection(title: String, description: String): Boolean {
        val text = "$title $description".lowercase(Locale.getDefault())
        val spiritualKeywords = listOf("pray", "rosary", "mass", "fast", "charity", "read", "bible", "gospel", "saint", "examen", "confession", "sacrament", "virtue", "god", "church", "adoration", "devotion", "silence", "work", "duty", "family", "help", "penance")
        val worldlyKeywords = listOf("game", "gaming", "movie", "tv", "party", "social media", "scroll", "bar", "gambling", "shopping", "gossip", "idle", "lounge")

        if (worldlyKeywords.any { text.contains(it) }) return true
        if (!spiritualKeywords.any { text.contains(it) }) return true
        return false
    }

    fun getSaintReflection(saintId: String): Flow<SaintReflection?> = repository.getSaintReflection(saintId)

    fun saveSaintReflection(saintId: String, userReflection: String, resolution: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            val reflection = SaintReflection(
                saintId = saintId,
                date = repository.getTodayString(),
                userReflection = userReflection,
                personalResolution = resolution,
                isBookmarked = isBookmarked
            )
            repository.saveSaintReflection(reflection)
        }
    }

    fun saveExamination(dutiesFulfilled: Boolean, prayer: String, charity: String, temptation: String, resolution: String, gratitude: String, selectedFaults: String = "") {
        viewModelScope.launch {
            val exam = DailyExamination(
                date = repository.getTodayString(),
                dutiesFulfilled = dutiesFulfilled,
                prayerFaithfulness = prayer,
                charityPracticed = charity,
                temptationResisted = temptation,
                concreteResolution = resolution,
                gratitudeNotes = gratitude,
                selectedFaults = selectedFaults
            )
            repository.saveExamination(exam)
        }
    }

    fun getTodayLiturgicalInfo(): LiturgicalDayInfo {
        val cal = Calendar.getInstance()
        return Roman1954Calendar.getLiturgicalInfoForDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun getTodaySaint(): SaintProfile {
        val cal = Calendar.getInstance()
        val mmdd = String.format("%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        return SaintData.getSaintForDate(mmdd) ?: SaintData.saintList.first()
    }

    fun triggerTestNotification(title: String, message: String) {
        NotificationHelper.showNotification(getApplication(), title, message)
    }

    fun syncDutiesToCalendar(context: android.content.Context, calendarId: Long) {
        viewModelScope.launch {
            val duties = allDuties.value
            var successCount = 0
            duties.forEach { duty ->
                val success = com.example.util.CalendarSyncHelper.cloneDutyToCalendar(context, calendarId, duty)
                if (success) successCount++
            }
            triggerTestNotification(
                title = "Sync Complete",
                message = "Successfully cloned $successCount out of ${duties.size} spiritual duties to your phone calendar."
            )
        }
    }



    suspend fun exportEncryptedBackup(
        password: String,
        outputStream: java.io.OutputStream
    ): com.example.util.BackupResult {
        val appDao = AppDatabase.getDatabase(getApplication()).appDao()
        return com.example.util.BackupRestoreManager.createEncryptedBackup(
            context = getApplication(),
            appDao = appDao,
            password = password,
            outputStream = outputStream
        )
    }

    suspend fun restoreEncryptedBackup(
        password: String,
        inputStream: java.io.InputStream
    ): com.example.util.RestoreResult {
        val appDao = AppDatabase.getDatabase(getApplication()).appDao()
        return com.example.util.BackupRestoreManager.restoreEncryptedBackup(
            context = getApplication(),
            appDao = appDao,
            password = password,
            inputStream = inputStream
        )
    }

    suspend fun exportExaminationsToPdf(
        password: String?,
        outputStream: java.io.OutputStream
    ): String {
        val appDao = AppDatabase.getDatabase(getApplication()).appDao()
        val examinations = appDao.getAllExaminationsSync()
        return com.example.util.PdfExportManager.exportExaminationsToPdf(
            context = getApplication(),
            examinations = examinations,
            password = password,
            outputStream = outputStream
        )
    }

    suspend fun exportRuleOfLifeHistoryToText(
        outputStream: java.io.OutputStream
    ): Boolean {
        return try {
            val report = repository.generateRuleOfLifeReport()
            outputStream.use { os ->
                os.write(report.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Failed to export Rule of Life report", e)
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        latinAudioPlayer.release()
    }
}
