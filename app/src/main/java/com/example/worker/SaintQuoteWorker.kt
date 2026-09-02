package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.AppDatabase
import com.example.data.SaintQuoteEntity
import com.example.data.calendar.SaintData
import java.util.Calendar

class SaintQuoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SaintQuoteWorker", "Starting SaintQuoteWorker - Traditional Catholic Quote Engine")
        val db = AppDatabase.getDatabase(applicationContext)

        // 1. Get the Saint of the Day according to the 1954 Roman Rite
        val cal = Calendar.getInstance()
        val mmdd = String.format("%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        val saint = SaintData.getSaintForDate(mmdd) ?: SaintData.saintList.first()

        Log.d("SaintQuoteWorker", "Current Saint of the Day: ${saint.saintName} ($mmdd)")

        // 2. Check if quotes are already cached in DB for today's Saint
        val existingQuotes = db.appDao().getQuotesForSaintSync(mmdd)
        if (existingQuotes.isNotEmpty()) {
            Log.d("SaintQuoteWorker", "Quotes for $mmdd (${saint.saintName}) already exist in Room database.")
            return Result.success()
        }

        // 3. Load 100% authentic, traditional Catholic writings and quotes from the Saints and Doctors of the Church
        val traditionalQuotes = mutableListOf<SaintQuoteEntity>()

        if (saint.quote.isNotBlank()) {
            traditionalQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = saint.quote,
                    sourceWork = "Traditional Roman Martyrology & Writings of ${saint.saintName}"
                )
            )
        }

        if (saint.meditation.isNotBlank()) {
            traditionalQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = saint.meditation,
                    sourceWork = "1954 Roman Rite Sacred Reflection"
                )
            )
        }

        if (saint.intercessoryPrayer.isNotBlank()) {
            traditionalQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = saint.intercessoryPrayer,
                    sourceWork = "Traditional Collect Prayer"
                )
            )
        }

        // Always include today's quote from Desert Fathers & Traditional Saints for Rule of Life discipline
        val desertFatherWisdom = com.example.data.calendar.DesertFathersAndSaintsQuotes.getDailyQuoteForDate()
        traditionalQuotes.add(
            SaintQuoteEntity(
                saintId = mmdd,
                saintName = "${desertFatherWisdom.saintOrFatherName} (${desertFatherWisdom.era})",
                quoteText = desertFatherWisdom.quoteText,
                sourceWork = "${desertFatherWisdom.sourceWork} • [Rule Focus: ${desertFatherWisdom.disciplineCategory}]"
            )
        )

        // Add additional traditional Patristic / Doctor of the Church quotations if list was empty
        if (traditionalQuotes.size == 1 && saint.quote.isBlank()) {
            traditionalQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = "He who wishes to love God must surrender all things to Him, for God does not accept a divided heart.",
                    sourceWork = "St. Alphonsus Liguori, Doctor of the Church"
                )
            )
            traditionalQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = "He cannot have God for his Father who has not the Church for his Mother.",
                    sourceWork = "St. Cyprian of Carthage, Bishop and Martyr"
                )
            )
        }

        // 4. Store quotes in Room Database
        try {
            db.appDao().deleteQuotesForSaint(mmdd)
            db.appDao().insertSaintQuotes(traditionalQuotes)
            Log.d("SaintQuoteWorker", "Successfully stored ${traditionalQuotes.size} authentic traditional quotes in Room database.")
        } catch (e: Exception) {
            Log.e("SaintQuoteWorker", "Failed to store quotes in database: ${e.message}", e)
            return Result.failure()
        }

        return Result.success()
    }

    companion object {
        fun enqueueFetch(context: Context) {
            val request = OneTimeWorkRequestBuilder<SaintQuoteWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(request)
            Log.d("SaintQuoteWorker", "SaintQuoteWorker work request enqueued.")
        }
    }
}
