package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Spiritual Duties
    @Query("SELECT * FROM spiritual_duties ORDER BY priority DESC, reminderTime ASC")
    fun getAllDuties(): Flow<List<SpiritualDuty>>

    @Query("SELECT * FROM spiritual_duties ORDER BY priority DESC, reminderTime ASC")
    suspend fun getAllDutiesSync(): List<SpiritualDuty>

    @Query("SELECT COUNT(*) FROM spiritual_duties")
    suspend fun getDutiesCount(): Int

    @Query("SELECT * FROM spiritual_duties WHERE frequency = :frequency ORDER BY reminderTime ASC")
    fun getDutiesByFrequency(frequency: String): Flow<List<SpiritualDuty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuty(duty: SpiritualDuty): Long

    @Update
    suspend fun updateDuty(duty: SpiritualDuty)

    @Query("DELETE FROM spiritual_duties WHERE id = :id")
    suspend fun deleteDuty(id: Int)

    @Query("SELECT * FROM spiritual_duties WHERE id = :id")
    suspend fun getDutyById(id: Int): SpiritualDuty?

    // Duty Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DutyCompletionLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyLogs(logs: List<DutyCompletionLog>)

    @Query("SELECT * FROM duty_logs WHERE dutyId = :dutyId ORDER BY date DESC")
    fun getLogsForDuty(dutyId: Int): Flow<List<DutyCompletionLog>>

    @Query("SELECT * FROM duty_logs ORDER BY date DESC")
    suspend fun getAllDutyLogsSync(): List<DutyCompletionLog>

    // Saint Reflections
    @Query("SELECT * FROM saint_reflections WHERE saintId = :saintId")
    fun getSaintReflection(saintId: String): Flow<SaintReflection?>

    @Query("SELECT * FROM saint_reflections WHERE isBookmarked = 1")
    fun getBookmarkedSaints(): Flow<List<SaintReflection>>

    @Query("SELECT * FROM saint_reflections")
    fun getAllSaintReflections(): Flow<List<SaintReflection>>

    @Query("SELECT * FROM saint_reflections")
    suspend fun getAllSaintReflectionsSync(): List<SaintReflection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSaintReflection(reflection: SaintReflection)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaintReflections(reflections: List<SaintReflection>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuties(duties: List<SpiritualDuty>)

    // Daily Examination
    @Query("SELECT * FROM daily_examinations WHERE date = :date")
    fun getExaminationForDate(date: String): Flow<DailyExamination?>

    @Query("SELECT * FROM daily_examinations ORDER BY date DESC LIMIT 30")
    fun getRecentExaminations(): Flow<List<DailyExamination>>

    @Query("SELECT * FROM daily_examinations ORDER BY date DESC")
    suspend fun getAllExaminationsSync(): List<DailyExamination>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExamination(examination: DailyExamination)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExaminations(examinations: List<DailyExamination>)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // Calendar Entries (1954 Roman Calendar Local Storage)
    @Query("SELECT * FROM calendar_entries ORDER BY month ASC, day ASC")
    fun getAllCalendarEntries(): Flow<List<CalendarEntry>>

    @Query("SELECT * FROM calendar_entries WHERE dateString = :dateString LIMIT 1")
    fun getCalendarEntryByDate(dateString: String): Flow<CalendarEntry?>

    @Query("SELECT * FROM calendar_entries WHERE feastTitle LIKE '%' || :query || '%' OR saintName LIKE '%' || :query || '%' OR commemorated LIKE '%' || :query || '%' OR feastRank LIKE '%' || :query || '%' OR season LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' OR virtues LIKE '%' || :query || '%' ORDER BY month ASC, day ASC")
    fun searchCalendarEntries(query: String): Flow<List<CalendarEntry>>

    @Query("SELECT COUNT(*) FROM calendar_entries")
    suspend fun getCalendarEntriesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntries(entries: List<CalendarEntry>)

    // Prayer Library (100% Offline Local Search)
    @Query("SELECT * FROM prayers ORDER BY category ASC, title ASC")
    fun getAllPrayers(): Flow<List<PrayerEntity>>

    @Query("SELECT * FROM prayers WHERE category = :category ORDER BY title ASC")
    fun getPrayersByCategory(category: String): Flow<List<PrayerEntity>>

    @Query("SELECT * FROM prayers WHERE title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR englishText LIKE '%' || :query || '%' OR latinText LIKE '%' || :query || '%' ORDER BY category ASC, title ASC")
    fun searchPrayers(query: String): Flow<List<PrayerEntity>>

    @Query("SELECT COUNT(*) FROM prayers")
    suspend fun getPrayersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayers(prayers: List<PrayerEntity>)

    // Breviary Entries (1954 Divinum Officium)
    @Query("SELECT * FROM breviary_entries WHERE (year = :year AND month = :month AND day = :day) OR dateCode = :dateCode OR dateCode = :fullDateCode ORDER BY hourId ASC")
    fun getBreviaryEntriesForDate(year: Int, month: Int, day: Int, dateCode: String = "", fullDateCode: String = ""): Flow<List<BreviaryEntryEntity>>

    @Query("SELECT * FROM breviary_entries WHERE (year = :year AND month = :month AND day = :day) OR dateCode = :dateCode OR dateCode = :fullDateCode ORDER BY hourId ASC")
    suspend fun getBreviaryEntriesForDateSync(year: Int, month: Int, day: Int, dateCode: String = "", fullDateCode: String = ""): List<BreviaryEntryEntity>

    @Query("SELECT * FROM breviary_entries WHERE ((year = :year AND month = :month AND day = :day) OR dateCode = :dateCode OR dateCode = :fullDateCode) AND hourId = :hourId LIMIT 1")
    suspend fun getBreviaryHour(year: Int, month: Int, day: Int, hourId: String, dateCode: String = "", fullDateCode: String = ""): BreviaryEntryEntity?

    @Query("SELECT COUNT(*) FROM breviary_entries")
    suspend fun getBreviaryEntriesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreviaryEntries(entries: List<BreviaryEntryEntity>)

    // Catechism Articles (100% Local Storage & Offline Search)
    @Query("SELECT * FROM catechism_articles ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    fun getAllCatechismArticles(): Flow<List<CatechismArticle>>

    @Query("SELECT * FROM catechism_articles ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    suspend fun getAllCatechismArticlesSync(): List<CatechismArticle>

    @Query("SELECT * FROM catechism_articles WHERE section = :section OR section LIKE '%' || :section || '%' OR :section LIKE '%' || section || '%' ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    fun getCatechismArticlesBySection(section: String): Flow<List<CatechismArticle>>

    @Query("SELECT * FROM catechism_articles WHERE title LIKE '%' || :query || '%' OR question LIKE '%' || :query || '%' OR answerText LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%' OR section LIKE '%' || :query || '%' OR citations LIKE '%' || :query || '%' ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    fun searchCatechismArticles(query: String): Flow<List<CatechismArticle>>

    @Query("SELECT * FROM catechism_articles WHERE title LIKE '%' || :query || '%' OR question LIKE '%' || :query || '%' OR answerText LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%' OR section LIKE '%' || :query || '%' OR citations LIKE '%' || :query || '%' ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    suspend fun searchCatechismArticlesSync(query: String): List<CatechismArticle>

    @Query("""
        SELECT c.* FROM catechism_articles c
        JOIN catechism_articles_fts fts ON c.id = fts.articleId
        WHERE catechism_articles_fts MATCH :ftsQuery
        ORDER BY CAST(REPLACE(c.id, 'cat_', '') AS INTEGER) ASC
    """)
    fun searchCatechismArticlesFts(ftsQuery: String): Flow<List<CatechismArticle>>

    @Query("""
        SELECT c.* FROM catechism_articles c
        JOIN catechism_articles_fts fts ON c.id = fts.articleId
        WHERE catechism_articles_fts MATCH :ftsQuery
        ORDER BY CAST(REPLACE(c.id, 'cat_', '') AS INTEGER) ASC
    """)
    suspend fun searchCatechismArticlesFtsSync(ftsQuery: String): List<CatechismArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatechismArticlesFts(ftsArticles: List<CatechismArticleFts>)

    @Query("DELETE FROM catechism_articles_fts")
    suspend fun clearCatechismFts()

    @Query("SELECT COUNT(*) FROM catechism_articles_fts")
    suspend fun getCatechismFtsCount(): Int

    @Query("SELECT * FROM catechism_articles WHERE isBookmarked = 1 ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    fun getBookmarkedCatechismArticles(): Flow<List<CatechismArticle>>

    @Query("SELECT * FROM catechism_articles WHERE isBookmarked = 1 ORDER BY CAST(REPLACE(id, 'cat_', '') AS INTEGER) ASC")
    suspend fun getBookmarkedCatechismArticlesSync(): List<CatechismArticle>

    @Query("SELECT COUNT(*) FROM catechism_articles")
    suspend fun getCatechismCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatechismArticles(articles: List<CatechismArticle>)

    @Update
    suspend fun updateCatechismArticle(article: CatechismArticle)

    // Saint quotes queries
    @Query("SELECT * FROM saint_quotes WHERE saintId = :saintId ORDER BY fetchedTimestamp DESC")
    fun getQuotesForSaint(saintId: String): Flow<List<SaintQuoteEntity>>

    @Query("SELECT * FROM saint_quotes WHERE saintId = :saintId ORDER BY fetchedTimestamp DESC")
    suspend fun getQuotesForSaintSync(saintId: String): List<SaintQuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaintQuotes(quotes: List<SaintQuoteEntity>)

    @Query("DELETE FROM saint_quotes WHERE saintId = :saintId")
    suspend fun deleteQuotesForSaint(saintId: String)

    // Call Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLog): Long

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLog>>

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCallLogs(limit: Int = 50): Flow<List<CallLog>>

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    suspend fun getAllCallLogsSync(): List<CallLog>

    @Query("DELETE FROM call_logs WHERE id = :id")
    suspend fun deleteCallLog(id: Int)

    @Query("DELETE FROM call_logs")
    suspend fun clearCallLogs()
}


