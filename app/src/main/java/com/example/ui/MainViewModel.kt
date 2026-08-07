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
import com.example.data.CatechismArticle
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

    // Reading View Text Size Preference (Persists across sessions)
    private val sharedPrefs = application.getSharedPreferences("user_preferences", android.content.Context.MODE_PRIVATE)
    private val _readingTextSize = MutableStateFlow(sharedPrefs.getFloat("reading_text_size", 14f))
    val readingTextSize: StateFlow<Float> = _readingTextSize.asStateFlow()

    fun updateReadingTextSize(newSize: Float) {
        viewModelScope.launch {
            _readingTextSize.value = newSize
            sharedPrefs.edit().putFloat("reading_text_size", newSize).apply()
        }
    }

    // App UI Theme Preference (Persists across sessions)
    private val _appTheme = MutableStateFlow(sharedPrefs.getString("app_theme", "CATHEDRAL_DARK") ?: "CATHEDRAL_DARK")
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    fun updateAppTheme(newTheme: String) {
        viewModelScope.launch {
            _appTheme.value = newTheme
            sharedPrefs.edit().putString("app_theme", newTheme).apply()
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

    // Latin Mode Toggle for Prayer Library
    private val _isLatinMode = MutableStateFlow(false)
    val isLatinMode: StateFlow<Boolean> = _isLatinMode.asStateFlow()

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
        latinAudioPlayer.play(
            id = id,
            text = text,
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

    // Prayer Library Local Search (100% Offline Room DB)
    private val _prayerSearchQuery = MutableStateFlow("")
    val prayerSearchQuery: StateFlow<String> = _prayerSearchQuery.asStateFlow()
    val prayerSearchResults: StateFlow<List<PrayerEntity>>

    // Catechism Local Storage & Offline Search
    private val _catechismSearchQuery = MutableStateFlow("")
    val catechismSearchQuery: StateFlow<String> = _catechismSearchQuery.asStateFlow()

    private val _selectedCatechismSection = MutableStateFlow("ALL")
    val selectedCatechismSection: StateFlow<String> = _selectedCatechismSection.asStateFlow()

    val catechismSearchResults: StateFlow<List<CatechismArticle>>

    // Offline AI Question & Answer Search State
    private val _aiQuestionInput = MutableStateFlow("")
    val aiQuestionInput: StateFlow<String> = _aiQuestionInput.asStateFlow()

    private val _aiAnswerResult = MutableStateFlow<String?>(null)
    val aiAnswerResult: StateFlow<String?> = _aiAnswerResult.asStateFlow()

    private val _matchedArticlesForAi = MutableStateFlow<List<CatechismArticle>>(emptyList())
    val matchedArticlesForAi: StateFlow<List<CatechismArticle>> = _matchedArticlesForAi.asStateFlow()

    private val _isAiSearching = MutableStateFlow(false)
    val isAiSearching: StateFlow<Boolean> = _isAiSearching.asStateFlow()

    // Reflection Guard Dialog State
    private val _pendingReflectionDuty = MutableStateFlow<SpiritualDuty?>(null)
    val pendingReflectionDuty: StateFlow<SpiritualDuty?> = _pendingReflectionDuty.asStateFlow()

    // Liturgical Calendar Selected Date
    private val _selectedCalendarDate = MutableStateFlow(Calendar.getInstance())
    val selectedCalendarDate: StateFlow<Calendar> = _selectedCalendarDate.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SpiritualRepository(db.appDao(), application)

        // Seed 1954 Roman Calendar, Prayers, and Catechism into local Room storage
        viewModelScope.launch {
            repository.ensureCalendarSeeded()
            repository.ensurePrayersSeeded()
            repository.ensureCatechismSeeded()
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

        catechismSearchResults = combine(
            _catechismSearchQuery,
            _selectedCatechismSection
        ) { query, section ->
            Pair(query, section)
        }.flatMapLatest { (query, section) ->
            if (query.isBlank() && (section.equals("ALL", ignoreCase = true) || section.isBlank())) {
                repository.getAllCatechismArticles()
            } else if (query.isBlank()) {
                repository.getCatechismArticlesBySection(section)
            } else {
                repository.searchCatechismArticles(query).combine(MutableStateFlow(section)) { list, activeSection ->
                    if (activeSection.equals("ALL", ignoreCase = true) || activeSection.isBlank()) {
                        list
                    } else {
                        list.filter { article ->
                            article.section.contains(activeSection, ignoreCase = true) ||
                            activeSection.contains(article.section, ignoreCase = true)
                        }
                    }
                }
            }
        }.map { list ->
            list.sortedBy { article ->
                val numStr = article.articleNumber.replace("[^0-9]".toRegex(), "")
                numStr.toIntOrNull() ?: article.id.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
            }
        }.stateIn(
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
        liturgicalSeasonEnabled: Boolean = true
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
                liturgicalSeasonNotificationsEnabled = liturgicalSeasonEnabled
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

    fun updateDutyReminderTime(dutyId: Int, newTime: String) {
        viewModelScope.launch {
            val duty = allDuties.value.find { it.id == dutyId }
            if (duty != null) {
                val updated = duty.copy(reminderTime = newTime)
                repository.insertDuty(updated)
                com.example.notifications.NotificationScheduler.scheduleAll(
                    getApplication(),
                    allDuties.value,
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
        _isBottomNavVisible.value = true
        _isTopHeaderVisible.value = true
        _readingProgress.value = 0f
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
        val spiritualKeywords = listOf("pray", "rosary", "mass", "fast", "charity", "read", "bible", "gospel", "saint", "examen", "confession", "sacrament", "virtue", "god", "church", "vespers", "lauds", "silence", "work", "duty", "family", "help", "penance")
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
                val success = com.example.utils.CalendarSyncHelper.cloneDutyToCalendar(context, calendarId, duty)
                if (success) successCount++
            }
            triggerTestNotification(
                title = "Sync Complete",
                message = "Successfully cloned $successCount out of ${duties.size} spiritual duties to your phone calendar."
            )
        }
    }



    // Catechism Functions
    fun setCatechismSearchQuery(query: String) {
        _catechismSearchQuery.value = query
    }

    fun setSelectedCatechismSection(section: String) {
        _selectedCatechismSection.value = section
    }

    fun setAiQuestionInput(input: String) {
        _aiQuestionInput.value = input
    }

    fun askCatechismQuestion(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isAiSearching.value = true
            _aiQuestionInput.value = question
            _aiAnswerResult.value = null
            _matchedArticlesForAi.value = emptyList()

            // Perform offline NLP & keyword score search across local Catechism Room database
            val rawMatches = repository.searchCatechismArticlesSync(question)
            val allArticles = if (rawMatches.isEmpty()) repository.getAllCatechismArticlesSync() else rawMatches

            // Tokenize question words
            val tokens = question.lowercase(Locale.getDefault())
                .replace(Regex("[^a-zA-Z0-9\\s]"), "")
                .split("\\s+".toRegex())
                .filter { it.length > 2 }

            // Score each article based on keyword frequency in title, question, answer, keywords
            val scoredArticles = allArticles.map { article ->
                var score = 0
                val titleLower = article.title.lowercase(Locale.getDefault())
                val questionLower = article.question.lowercase(Locale.getDefault())
                val answerLower = article.answerText.lowercase(Locale.getDefault())
                val keywordsLower = article.keywords.lowercase(Locale.getDefault())

                tokens.forEach { token ->
                    if (titleLower.contains(token)) score += 10
                    if (questionLower.contains(token)) score += 8
                    if (keywordsLower.contains(token)) score += 6
                    if (answerLower.contains(token)) score += 2
                }
                Pair(article, score)
            }.sortedByDescending { it.second }.filter { it.second > 0 || rawMatches.contains(it.first) }

            val topMatches = scoredArticles.take(3).map { it.first }
            _matchedArticlesForAi.value = topMatches

            if (topMatches.isEmpty()) {
                val fallbackArticles = allArticles.take(2)
                _matchedArticlesForAi.value = fallbackArticles
                val answerSb = StringBuilder()
                answerSb.append("### Dogmatic Catholic Response:\n\n")
                answerSb.append("Based on the Traditional Catholic Catechism, regarding your query: *\"$question\"*\n\n")
                answerSb.append("The Catholic Faith teaches that all truth revealed by Almighty God must be firmly believed. ")
                answerSb.append("Here are the closest relevant Catechism Articles for your reflection:\n\n")
                fallbackArticles.forEach { article ->
                    answerSb.append("• **[${article.articleNumber}] ${article.title}** (${article.section})\n")
                    answerSb.append("  *\"${article.question}\"*\n")
                    answerSb.append("  ${article.answerText}\n")
                    if (article.citations.isNotBlank()) {
                        answerSb.append("  *Magisterial Citation:* ${article.citations}\n")
                    }
                    answerSb.append("\n")
                }
                _aiAnswerResult.value = answerSb.toString()
            } else {
                val primary = topMatches.first()
                val answerSb = StringBuilder()
                answerSb.append("### Dogmatic Catholic Response:\n\n")
                answerSb.append("According to the Traditional Catholic Catechism (vaticancatholic.com/catechism/):\n\n")
                answerSb.append("**Core Article Referenced:** [${primary.articleNumber}] ${primary.title} (${primary.section})\n\n")
                answerSb.append("**Question:** ${primary.question}\n\n")
                answerSb.append("**Dogmatic Answer:** ${primary.answerText}\n\n")
                if (primary.citations.isNotBlank()) {
                    answerSb.append("**Magisterial Citations & Scripture:** ${primary.citations}\n\n")
                }

                if (topMatches.size > 1) {
                    answerSb.append("---\n**Additional Supporting Catechism Articles:**\n\n")
                    topMatches.drop(1).forEach { related ->
                        answerSb.append("• **[${related.articleNumber}] ${related.title}**: ${related.answerText.take(180)}...\n")
                        if (related.citations.isNotBlank()) {
                            answerSb.append("  *(Ref: ${related.citations})*\n")
                        }
                    }
                }
                _aiAnswerResult.value = answerSb.toString()
            }
            _isAiSearching.value = false
        }
    }

    fun toggleCatechismBookmark(article: CatechismArticle) {
        viewModelScope.launch {
            repository.toggleCatechismBookmark(article)
        }
    }

    suspend fun exportEncryptedBackup(
        password: String,
        outputStream: java.io.OutputStream
    ): com.example.utils.BackupResult {
        val appDao = AppDatabase.getDatabase(getApplication()).appDao()
        return com.example.utils.BackupRestoreManager.createEncryptedBackup(
            context = getApplication(),
            appDao = appDao,
            password = password,
            outputStream = outputStream
        )
    }

    suspend fun restoreEncryptedBackup(
        password: String,
        inputStream: java.io.InputStream
    ): com.example.utils.RestoreResult {
        val appDao = AppDatabase.getDatabase(getApplication()).appDao()
        return com.example.utils.BackupRestoreManager.restoreEncryptedBackup(
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
        return com.example.utils.PdfExportManager.exportExaminationsToPdf(
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
