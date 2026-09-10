package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.data.calendar.Roman1954Calendar
import com.example.data.calendar.Roman1954DataSeeder
import com.example.data.calendar.SaintData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SpiritualRepository(
    private val appDao: AppDao,
    private val context: Context? = null
) {


    val allDuties: Flow<List<SpiritualDuty>> = appDao.getAllDuties()
    val userProfile: Flow<UserProfile?> = appDao.getUserProfile()
    val bookmarkedSaints: Flow<List<SaintReflection>> = appDao.getBookmarkedSaints()
    val allSaintReflections: Flow<List<SaintReflection>> = appDao.getAllSaintReflections()
    val recentExaminations: Flow<List<DailyExamination>> = appDao.getRecentExaminations()
    val allCallLogs: Flow<List<CallLog>> = appDao.getAllCallLogs()

    suspend fun logCall(title: String, message: String, status: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val callLog = CallLog(
            title = title,
            message = message,
            status = status,
            timestamp = System.currentTimeMillis(),
            date = currentDate
        )
        return appDao.insertCallLog(callLog)
    }

    /**
     * Checks if active internet connection is available on the device.
     */
    @Suppress("DEPRECATION")
    fun isNetworkAvailable(): Boolean {
        val ctx = context ?: return false
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    suspend fun ensureJsonPrepopulated() = withContext(Dispatchers.IO) {
        context?.let { ctx ->
            LiturgicalJsonPrepopulator.populateFromJsonAsset(ctx, appDao)
        }
    }

    suspend fun ensureCalendarSeeded() = withContext(Dispatchers.IO) {
        ensureJsonPrepopulated()
        val entries = Roman1954DataSeeder.getInitialCalendarEntries()
        appDao.insertCalendarEntries(entries)
    }

    suspend fun ensurePrayersSeeded() = withContext(Dispatchers.IO) {
        ensureJsonPrepopulated()
        val prayers = PrayerAndMissalDataSeeder.getInitialPrayers()
        if (appDao.getPrayersCount() < prayers.size) {
            appDao.insertPrayers(prayers)
        }
    }

    suspend fun ensureMissalSeeded() {
        ensureJsonPrepopulated()
    }

    suspend fun hardRefreshForDate(cal: Calendar) {
        // Deprecated
    }









    val bookmarkedPrayers: Flow<List<PrayerEntity>> = appDao.getBookmarkedPrayers()

    fun searchPrayers(query: String): Flow<List<PrayerEntity>> {
        return if (query.isBlank()) {
            appDao.getAllPrayers()
        } else {
            appDao.searchPrayers(query.trim())
        }
    }

    suspend fun togglePrayerBookmark(prayerId: String, currentStatus: Boolean) {
        val existing = appDao.getPrayerById(prayerId)
        val newStatus = !currentStatus
        if (existing != null) {
            appDao.updatePrayerBookmark(prayerId, newStatus)
        } else {
            val allInitial = PrayerAndMissalDataSeeder.getInitialPrayers()
            val found = allInitial.find { it.id == prayerId }
            if (found != null) {
                appDao.insertPrayers(listOf(found.copy(isBookmarked = newStatus)))
                appDao.updatePrayerBookmark(prayerId, newStatus)
            }
        }
    }

    fun getPrayersByCategory(category: String): Flow<List<PrayerEntity>> {
        return appDao.getPrayersByCategory(category)
    }

    fun searchCalendarEntries(query: String): Flow<List<CalendarEntry>> {

        return if (query.isBlank()) {
            appDao.getAllCalendarEntries()
        } else {
            appDao.searchCalendarEntries(query.trim())
        }
    }

    fun getCalendarEntryByDate(dateString: String): Flow<CalendarEntry?> {
        return appDao.getCalendarEntryByDate(dateString)
    }

    fun getAllCalendarEntries(): Flow<List<CalendarEntry>> {
        return appDao.getAllCalendarEntries()
    }

    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    suspend fun setupUserProfileAndRule(gender: String): UserProfile {
        val profile = UserProfile(
            id = 1,
            gender = gender.uppercase(),
            isConfigured = true,
            notificationEnabled = true
        )
        appDao.saveUserProfile(profile)

        // Seed default spiritual rule tasks based on gender
        seedDefaultDuties(gender.uppercase())
        return profile
    }

        private suspend fun seedDefaultDuties(gender: String) {
        if (appDao.getDutiesCount() > 0) return

        val duties = if (gender == "MALE") {
            // Rule of St. Benedict (Regula Benedicti) for Men
            listOf(
                SpiritualDuty(
                    title = "Morning Offering & Prayer (Regula Benedicti)",
                    description = "Offer morning prayers and the first fruits of the day in peaceful recollection, as taught by St. Benedict.",
                    frequency = "DAILY",
                    reminderTime = "06:30",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Lectio Divina & Holy Scripture",
                    description = "Engage in 15-20 minutes of contemplative reading of Sacred Scripture and spiritual writings.",
                    frequency = "DAILY",
                    reminderTime = "08:00",
                    priority = "MEDIUM",
                    category = "READING",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Midday Prayer & Noon Angelus",
                    description = "Perform the midday Angelus and prayer; prefer nothing whatever to the love of God.",
                    frequency = "DAILY",
                    reminderTime = "12:00",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Manual Labor & Stewardship (Labora)",
                    description = "Fulfill daily labor and earthly duties with exact diligence, patience, and humility as unto Christ.",
                    frequency = "DAILY",
                    reminderTime = "14:00",
                    priority = "MEDIUM",
                    category = "DUTY",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Night Prayer & Examination of Conscience",
                    description = "Examine conscience thoroughly and pray in peaceful silence before retiring for rest.",
                    frequency = "DAILY",
                    reminderTime = "21:30",
                    priority = "SACRED",
                    category = "REPENTANCE",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Friday Fast & Benedictine Abstinence",
                    description = "Observe traditional Friday penance and abstinence from meat in union with Christ's Passion.",
                    frequency = "WEEKLY",
                    scheduledDaysOfWeek = "Friday",
                    reminderTime = "08:00",
                    priority = "SACRED",
                    category = "FASTING",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Monthly Sacramental Confession & Recollection",
                    description = "Approach the Sacrament of Penance and devote time to silent spiritual recollection.",
                    frequency = "MONTHLY",
                    scheduledDaysOfMonth = "1ST_Saturday",
                    reminderTime = "10:00",
                    priority = "SACRED",
                    category = "REPENTANCE",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Annual Spiritual Renewal & Retreat",
                    description = "Dedicate an annual period of days to prayer, retreat, and total consecration.",
                    frequency = "YEARLY",
                    scheduledDaysOfYear = "08-15",
                    reminderTime = "09:00",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                )
            )
        } else {
            // Regula Donati (Rule of Donatus of Besançon for Nuns/Women)
            listOf(
                SpiritualDuty(
                    title = "Morning Consecration & Prayer (Regula Donati)",
                    description = "Offer morning prayers, labor, and heart to Christ through the Blessed Virgin Mary.",
                    frequency = "DAILY",
                    reminderTime = "07:00",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Midday Prayer & Angelus",
                    description = "Recite the midday Angelus and pray devotions in peaceful recollection.",
                    frequency = "DAILY",
                    reminderTime = "11:30",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Hidden Act of Divine Charity",
                    description = "Perform one unacknowledged act of selfless charity, gentleness, and patience toward others.",
                    frequency = "DAILY",
                    reminderTime = "15:00",
                    priority = "MEDIUM",
                    category = "CHARITY",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Spiritual Reading of Saintly Virtues",
                    description = "Read 15 minutes of patristic writings and lives of holy virgins to grow in monastic virtue.",
                    frequency = "DAILY",
                    reminderTime = "17:00",
                    priority = "MEDIUM",
                    category = "READING",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Evening Examen & Night Prayer",
                    description = "Examine purity of thought, speech, and charity in peaceful monastic silence.",
                    frequency = "DAILY",
                    reminderTime = "21:00",
                    priority = "SACRED",
                    category = "REPENTANCE",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Friday Mortification & Silence",
                    description = "Observe Friday penance, holy silence, and mortification of the senses in monastic union.",
                    frequency = "WEEKLY",
                    scheduledDaysOfWeek = "Friday",
                    reminderTime = "08:00",
                    priority = "SACRED",
                    category = "FASTING",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Monthly Sacramental Confession & Adoration",
                    description = "Receive holy absolution and spend an hour before the Most Blessed Sacrament.",
                    frequency = "MONTHLY",
                    scheduledDaysOfMonth = "1ST_Saturday",
                    reminderTime = "10:00",
                    priority = "SACRED",
                    category = "REPENTANCE",
                    isSuggestedRule = true
                ),
                SpiritualDuty(
                    title = "Annual Spiritual Renewal Retreat",
                    description = "Set aside a holy retreat for spiritual examination, silence, and total self-offering.",
                    frequency = "YEARLY",
                    scheduledDaysOfYear = "08-15",
                    reminderTime = "09:00",
                    priority = "SACRED",
                    category = "PRAYER",
                    isSuggestedRule = true
                )
            )
        }

        duties.forEach { appDao.insertDuty(it) }
    }

    suspend fun insertDuty(duty: SpiritualDuty): Long = appDao.insertDuty(duty)

    suspend fun updateDuty(duty: SpiritualDuty) = appDao.updateDuty(duty)

    suspend fun deleteDuty(id: Int) = appDao.deleteDuty(id)

    suspend fun toggleDutyCompletion(duty: SpiritualDuty) {
        val today = getTodayString()
        val isAlreadyCompletedToday = duty.completedDate == today && duty.isCompleted

        val newCompleted = !isAlreadyCompletedToday
        val newStreak = if (newCompleted) duty.streakCount + 1 else maxOf(0, duty.streakCount - 1)
        val newDate = if (newCompleted) today else duty.completedDate
        val newTimestamp = if (newCompleted) System.currentTimeMillis() else 0L

        val updated = duty.copy(
            isCompleted = newCompleted,
            completedDate = newDate,
            completionTimestamp = newTimestamp,
            streakCount = newStreak
        )
        appDao.updateDuty(updated)

        if (newCompleted) {
            appDao.insertLog(DutyCompletionLog(dutyId = duty.id, date = today, notes = "Completed"))
        }
    }

    fun getSaintReflection(saintId: String): Flow<SaintReflection?> = appDao.getSaintReflection(saintId)

    suspend fun saveSaintReflection(reflection: SaintReflection) = appDao.saveSaintReflection(reflection)

    fun getExaminationForDate(date: String): Flow<DailyExamination?> = appDao.getExaminationForDate(date)

    suspend fun saveExamination(examination: DailyExamination) = appDao.saveExamination(examination)

    suspend fun saveUserProfile(profile: UserProfile) = appDao.saveUserProfile(profile)

    suspend fun getAllDutiesSync(): List<SpiritualDuty> = appDao.getAllDutiesSync()
    suspend fun getUserProfileSync(): UserProfile? = appDao.getUserProfileSync()

    suspend fun resetExpiredDuties() {
        val today = getTodayString()
        val duties = appDao.getAllDutiesSync()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = Date()
        val calToday = Calendar.getInstance().apply { time = todayDate }

        var anyUpdated = false
        val updatedDuties = duties.map { duty ->
            if (duty.isCompleted && duty.completedDate.isNotBlank()) {
                val compDateStr = duty.completedDate
                if (compDateStr != today) {
                    var shouldReset = false
                    try {
                        val compDate = sdf.parse(compDateStr)
                        if (compDate != null) {
                            val calComp = Calendar.getInstance().apply { time = compDate }
                            when (duty.frequency.uppercase()) {
                                "DAILY" -> {
                                    shouldReset = true
                                }
                                "WEEKLY" -> {
                                    val sameWeek = calComp.get(Calendar.WEEK_OF_YEAR) == calToday.get(Calendar.WEEK_OF_YEAR) &&
                                                   calComp.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)
                                    shouldReset = !sameWeek
                                }
                                "MONTHLY" -> {
                                    val sameMonth = calComp.get(Calendar.MONTH) == calToday.get(Calendar.MONTH) &&
                                                    calComp.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)
                                    shouldReset = !sameMonth
                                }
                                "YEARLY" -> {
                                    val sameYear = calComp.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)
                                    shouldReset = !sameYear
                                }
                                else -> {
                                    shouldReset = true
                                }
                            }
                        } else {
                            shouldReset = true
                        }
                    } catch (e: Exception) {
                        shouldReset = true
                    }

                    if (shouldReset) {
                        anyUpdated = true
                        duty.copy(isCompleted = false)
                    } else {
                        duty
                    }
                } else {
                    duty
                }
            } else {
                duty
            }
        }

        if (anyUpdated) {
            appDao.insertDuties(updatedDuties)
            Log.d("SpiritualRepository", "Reset expired completed duties in Room database.")
        }
    }

    // Saint Quotes repository methods
    fun getQuotesForSaint(saintId: String): Flow<List<SaintQuoteEntity>> = appDao.getQuotesForSaint(saintId)

    suspend fun getQuotesForSaintSync(saintId: String): List<SaintQuoteEntity> = appDao.getQuotesForSaintSync(saintId)

    suspend fun insertSaintQuotes(quotes: List<SaintQuoteEntity>) = appDao.insertSaintQuotes(quotes)

    suspend fun deleteQuotesForSaint(saintId: String) = appDao.deleteQuotesForSaint(saintId)

    suspend fun generateRuleOfLifeReport(): String {
        val duties = appDao.getAllDutiesSync()
        val logs = appDao.getAllDutyLogsSync()
        
        val sb = java.lang.StringBuilder()
        sb.append("========================================================\n")
        sb.append("                  CUSTOS ANIMAE - RULE OF LIFE          \n")
        sb.append("                  SPIRITUAL COMPLETION REPORT           \n")
        sb.append("========================================================\n\n")
        
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sb.append("Generated on: ").append(sdf.format(Date())).append("\n\n")
        
        sb.append("--------------------------------------------------------\n")
        sb.append("1. CURRENT ACTIVE RULE OF LIFE\n")
        sb.append("--------------------------------------------------------\n")
        if (duties.isEmpty()) {
            sb.append("No active duties or devotions found in your Rule of Life.\n")
        } else {
            duties.forEachIndexed { idx, duty ->
                val status = if (duty.isCompleted) "[✔] COMPLETED TODAY" else "[ ] PENDING TODAY"
                sb.append("${idx + 1}. ${duty.title}\n")
                sb.append("   - Category: ${duty.category}\n")
                sb.append("   - Frequency: ${duty.frequency}\n")
                if (duty.reminderTime.isNotBlank()) {
                    sb.append("   - Daily Alarm: ${duty.reminderTime}\n")
                }
                sb.append("   - Current Streak: ${duty.streakCount} days\n")
                sb.append("   - Status: ${status}\n")
                if (duty.completedDate.isNotBlank()) {
                    sb.append("   - Last Completed Date: ${duty.completedDate}\n")
                }
                if (duty.personalNotes.isNotBlank()) {
                    sb.append("   - Rule Notes: ${duty.personalNotes}\n")
                }
                sb.append("\n")
            }
        }
        
        sb.append("--------------------------------------------------------\n")
        sb.append("2. HISTORICAL COMPLETION RECORD\n")
        sb.append("--------------------------------------------------------\n")
        if (logs.isEmpty()) {
            sb.append("No completed duties logged yet. Keep walking the narrow path!\n")
        } else {
            // Group logs by date
            val logsByDate = logs.groupBy { it.date }.toSortedMap(reverseOrder())
            logsByDate.forEach { (date, dateLogs) ->
                sb.append("### Date: $date\n")
                dateLogs.forEach { log ->
                    val duty = duties.find { it.id == log.dutyId }
                    val dutyTitle = duty?.title ?: "Unknown Duty #${log.dutyId}"
                    val categoryStr = duty?.category ?: "SPIRITUAL"
                    sb.append("  [✔] $dutyTitle ($categoryStr)")
                    if (log.notes.isNotBlank() && log.notes != "Completed") {
                        sb.append(" - Notes: ${log.notes}")
                    }
                    sb.append("\n")
                }
                sb.append("\n")
            }
        }
        
        sb.append("========================================================\n")
        sb.append("                 End of Spiritual Report                \n")
        sb.append("          \"Age quod agis. Prefer nothing to Christ.\"\n")
        sb.append("========================================================\n")
        
        return sb.toString()
    }
}
