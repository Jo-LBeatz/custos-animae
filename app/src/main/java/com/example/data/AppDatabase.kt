package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SpiritualDuty::class,
        DutyCompletionLog::class,
        SaintReflection::class,
        DailyExamination::class,
        UserProfile::class,
        CalendarEntry::class,
        PrayerEntity::class,
        BreviaryEntryEntity::class,
        SaintQuoteEntity::class,
        CallLog::class,
        NovenaEntity::class,
        NovenaProgressEntity::class
    ],
    version = 31,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "custos_animae_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
