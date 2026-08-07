package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for storing and retrieving history of answered, missed, or declined Rule of Life calls.
 */
class CallLogRepository(private val appDao: AppDao) {

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

    fun getRecentCallLogs(limit: Int = 50): Flow<List<CallLog>> {
        return appDao.getRecentCallLogs(limit)
    }

    suspend fun getAllCallLogsSync(): List<CallLog> {
        return appDao.getAllCallLogsSync()
    }

    suspend fun deleteCallLog(id: Int) {
        appDao.deleteCallLog(id)
    }

    suspend fun clearCallLogs() {
        appDao.clearCallLogs()
    }

    companion object {
        @Volatile
        private var INSTANCE: CallLogRepository? = null

        fun getInstance(context: Context): CallLogRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val instance = CallLogRepository(db.appDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
