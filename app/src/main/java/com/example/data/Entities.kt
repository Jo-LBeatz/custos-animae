package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spiritual_duties",
    indices = [Index(value = ["priority", "reminderTime"])]
)
data class SpiritualDuty(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val frequency: String = "DAILY", // DAILY, WEEKLY, MONTHLY, YEARLY
    val reminderTime: String = "08:00", // HH:mm
    val priority: String = "SACRED", // SACRED, MEDIUM, LOW
    val category: String = "PRAYER", // PRAYER, FASTING, CHARITY, REPENTANCE, READING, DUTY
    val isCompleted: Boolean = false,
    val completedDate: String = "", // YYYY-MM-DD
    val completionTimestamp: Long = 0L, // exact timestamp of completion
    val personalNotes: String = "",
    val streakCount: Int = 0,
    val isSuggestedRule: Boolean = false,
    val scheduledDaysOfWeek: String = "", // comma-separated e.g. "Monday,Wednesday,Friday" or day numbers
    val scheduledDaysOfMonth: String = "", // comma-separated e.g. "1,15,30"
    val scheduledDaysOfYear: String = "" // comma-separated e.g. "08-15,12-25"
)

@Entity(
    tableName = "duty_logs",
    indices = [Index(value = ["dutyId"]), Index(value = ["date"])]
)
data class DutyCompletionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dutyId: Int,
    val date: String, // YYYY-MM-DD
    val notes: String = ""
)

@Entity(tableName = "saint_reflections")
data class SaintReflection(
    @PrimaryKey val saintId: String, // e.g., "07-31" or saint code
    val date: String,
    val userReflection: String = "",
    val personalResolution: String = "",
    val isBookmarked: Boolean = false
)

@Entity(tableName = "daily_examinations")
data class DailyExamination(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val dutiesFulfilled: Boolean = false,
    val prayerFaithfulness: String = "",
    val charityPracticed: String = "",
    val temptationResisted: String = "",
    val concreteResolution: String = "",
    val gratitudeNotes: String = "",
    val selectedFaults: String = "", // Comma-separated fault IDs e.g. "P_VANITY,C_ANGER"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val gender: String = "MALE", // MALE or FEMALE
    val isConfigured: Boolean = false,
    val notificationEnabled: Boolean = true,
    val morningPrayerTime: String = "07:00",
    val eveningPrayerTime: String = "21:00",
    val saintNotificationTime: String = "07:30",
    val angelusMorningTime: String = "06:00",
    val angelusNoonTime: String = "12:00",
    val angelusEveningTime: String = "18:00",
    val angelusEnabled: Boolean = true,
    val sacredHeartEnabled: Boolean = true,
    val sacredHeartTime: String = "08:00",
    val immaculateHeartEnabled: Boolean = true,
    val immaculateHeartTime: String = "08:00",
    val liturgicalSeasonNotificationsEnabled: Boolean = true,
    val novenaNotificationsEnabled: Boolean = true,
    val novenaNotificationTime: String = "08:00"
)

@Entity(
    tableName = "calendar_entries",
    indices = [Index(value = ["dateString"]), Index(value = ["month", "day"])]
)
data class CalendarEntry(
    @PrimaryKey val id: String, // MM-DD format e.g. "01-01" or "12-24"
    val dateString: String,
    val month: Int,
    val day: Int,
    val feastTitle: String,
    val feastRank: String,
    val season: String,
    val isVigil: Boolean = false,
    val isEmberDay: Boolean = false,
    val isRogationDay: Boolean = false,
    val isFastingDay: Boolean = false,
    val isAbstinenceDay: Boolean = false,
    val saintName: String = "",
    val commemorated: String = "",
    val biography: String = "",
    @ColumnInfo(name = "historicity_note") val historicityNote: String = "",
    val virtues: String = "",
    val quote: String = "",
    val meditation: String = "",
    val resolution: String = "",
    val intercessoryPrayer: String = "",
    val notes: String = ""
) {
    val historicity: String get() = historicityNote
}

@Entity(
    tableName = "prayers",
    indices = [Index(value = ["category"]), Index(value = ["title"])]
)
data class PrayerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val subtitle: String = "",
    val englishText: String,
    val latinText: String = "",
    val notes: String = "",
    val isBookmarked: Boolean = false
)

@Entity(
    tableName = "breviary_entries",
    indices = [
        Index(value = ["year", "month", "day"]),
        Index(value = ["year", "month", "day", "hourId"]),
        Index(value = ["dateCode"]),
        Index(value = ["hourId"]),
        Index(value = ["dateCode", "hourId"])
    ]
)
data class BreviaryEntryEntity(
    @PrimaryKey val id: String, // e.g., "07-31_matins" or "2026-07-31_matins"
    val dateCode: String,
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hourId: String,
    val title: String,
    val latinTitle: String,
    val season: String,
    val openingVersicleLatin: String,
    val openingVersicleEnglish: String,
    val invitatoryLatin: String,
    val invitatoryEnglish: String,
    val hymnLatin: String,
    val hymnEnglish: String,
    val psalmsSummary: String,
    val capitulumLatin: String,
    val capitulumEnglish: String,
    val readingsLatin: String,
    val readingsEnglish: String,
    val canticleTitle: String,
    val canticleAntiphonLatin: String,
    val canticleAntiphonEnglish: String,
    val canticleLatin: String,
    val canticleEnglish: String,
    val collectLatin: String,
    val collectEnglish: String,
    val marianAntiphonTitle: String,
    val marianAntiphonLatin: String,
    val marianAntiphonEnglish: String,
    val rubrics: String
)

@Entity(
    tableName = "saint_quotes",
    indices = [Index(value = ["saintId"])]
)
data class SaintQuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val saintId: String,       // e.g., "08-15"
    val saintName: String,
    val quoteText: String,
    val sourceWork: String = "",
    val fetchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "call_logs",
    indices = [Index(value = ["timestamp"]), Index(value = ["date"])]
)
data class CallLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String = "",
    val status: String, // "ANSWERED", "MISSED", "DECLINED"
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "" // YYYY-MM-DD
)

data class RelapseLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val streakDays: Int = 0,
    val streakHours: Int = 0,
    val triggerNote: String = "",
    val resolutionNote: String = ""
)

@Entity(
    tableName = "novenas",
    indices = [Index(value = ["category"]), Index(value = ["fixedStartMonth", "fixedStartDay"])]
)
data class NovenaEntity(
    @PrimaryKey val id: String,
    val feastTitle: String,
    val novenaTitle: String,
    val fixedStartMonth: Int? = null,
    val fixedStartDay: Int? = null,
    val fixedFeastMonth: Int? = null,
    val fixedFeastDay: Int? = null,
    val category: String = "SAINT",
    val summary: String = "",
    val intention: String = "",
    val englishPrayer: String = "",
    val latinPrayer: String = "",
    val isMovable: Boolean = false,
    val movableType: String = "",
    val isEnrolled: Boolean = true,
    val lastPrayedDay: Int = 0,
    val lastPrayedDate: String = "", // YYYY-MM-DD
    val completedCount: Int = 0
)

@Entity(
    tableName = "novena_progress_logs",
    indices = [Index(value = ["novenaId"]), Index(value = ["datePrayed"])]
)
data class NovenaProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val novenaId: String,
    val dayNumber: Int, // 1..9
    val datePrayed: String, // YYYY-MM-DD
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)






