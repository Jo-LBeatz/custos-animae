package com.example.util

import android.content.Context
import android.util.Base64
import com.example.data.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

sealed class BackupResult {
    data class Success(val summary: String) : BackupResult()
    data class Error(val message: String) : BackupResult()
}

sealed class RestoreResult {
    data class Success(val restoredCountSummary: String) : RestoreResult()
    data class Error(val message: String) : RestoreResult()
}

object BackupRestoreManager {

    private const val KDF_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH_BITS = 256
    private const val TAG_LENGTH_BITS = 128
    private const val SALT_SIZE_BYTES = 16
    private const val IV_SIZE_BYTES = 12

    suspend fun createEncryptedBackup(
        context: Context,
        appDao: AppDao,
        password: String,
        outputStream: OutputStream
    ): BackupResult {
        return try {
            if (password.length < 4) {
                return BackupResult.Error("Backup password must be at least 4 characters long.")
            }

            // 1. Gather all user data from Room DB
            val examinations = appDao.getAllExaminationsSync()
            val saintReflections = appDao.getAllSaintReflectionsSync()
            val duties = appDao.getAllDutiesSync()
            val dutyLogs = appDao.getAllDutyLogsSync()
            val userProfile = appDao.getUserProfileSync()

            // 2. Gather SharedPreferences for confession ledger security
            val ledgerSharedPref = context.getSharedPreferences("confession_ledger_pref", Context.MODE_PRIVATE)
            val ledgerPwd = ledgerSharedPref.getString("ledger_pwd", "") ?: ""
            val ledgerSecurityQ = ledgerSharedPref.getString("ledger_security_q", "") ?: ""
            val ledgerSecurityA = ledgerSharedPref.getString("ledger_security_a", "") ?: ""

            // 3. Build Inner Payload JSON
            val innerJson = JSONObject()
            innerJson.put("exportTimestamp", System.currentTimeMillis())

            // Examinations
            val examArray = JSONArray()
            examinations.forEach { exam ->
                val obj = JSONObject()
                obj.put("date", exam.date)
                obj.put("dutiesFulfilled", exam.dutiesFulfilled)
                obj.put("prayerFaithfulness", exam.prayerFaithfulness)
                obj.put("charityPracticed", exam.charityPracticed)
                obj.put("temptationResisted", exam.temptationResisted)
                obj.put("concreteResolution", exam.concreteResolution)
                obj.put("gratitudeNotes", exam.gratitudeNotes)
                obj.put("selectedFaults", exam.selectedFaults)
                obj.put("timestamp", exam.timestamp)
                examArray.put(obj)
            }
            innerJson.put("examinations", examArray)

            // Saint Reflections
            val saintArray = JSONArray()
            saintReflections.forEach { sr ->
                val obj = JSONObject()
                obj.put("saintId", sr.saintId)
                obj.put("date", sr.date)
                obj.put("userReflection", sr.userReflection)
                obj.put("personalResolution", sr.personalResolution)
                obj.put("isBookmarked", sr.isBookmarked)
                saintArray.put(obj)
            }
            innerJson.put("saintReflections", saintArray)

            // Spiritual Duties
            val dutiesArray = JSONArray()
            duties.forEach { duty ->
                val obj = JSONObject()
                obj.put("id", duty.id)
                obj.put("title", duty.title)
                obj.put("description", duty.description)
                obj.put("frequency", duty.frequency)
                obj.put("reminderTime", duty.reminderTime)
                obj.put("priority", duty.priority)
                obj.put("category", duty.category)
                obj.put("isCompleted", duty.isCompleted)
                obj.put("completedDate", duty.completedDate)
                obj.put("completionTimestamp", duty.completionTimestamp)
                obj.put("personalNotes", duty.personalNotes)
                obj.put("streakCount", duty.streakCount)
                obj.put("isSuggestedRule", duty.isSuggestedRule)
                obj.put("scheduledDaysOfWeek", duty.scheduledDaysOfWeek)
                obj.put("scheduledDaysOfMonth", duty.scheduledDaysOfMonth)
                obj.put("scheduledDaysOfYear", duty.scheduledDaysOfYear)
                dutiesArray.put(obj)
            }
            innerJson.put("spiritualDuties", dutiesArray)

            // Duty Logs
            val logsArray = JSONArray()
            dutyLogs.forEach { log ->
                val obj = JSONObject()
                obj.put("id", log.id)
                obj.put("dutyId", log.dutyId)
                obj.put("date", log.date)
                obj.put("notes", log.notes)
                logsArray.put(obj)
            }
            innerJson.put("dutyLogs", logsArray)

            // User Profile
            userProfile?.let { prof ->
                val profObj = JSONObject()
                profObj.put("id", prof.id)
                profObj.put("gender", prof.gender)
                profObj.put("isConfigured", prof.isConfigured)
                profObj.put("notificationEnabled", prof.notificationEnabled)
                profObj.put("morningPrayerTime", prof.morningPrayerTime)
                profObj.put("eveningPrayerTime", prof.eveningPrayerTime)
                profObj.put("saintNotificationTime", prof.saintNotificationTime)
                profObj.put("angelusMorningTime", prof.angelusMorningTime)
                profObj.put("angelusNoonTime", prof.angelusNoonTime)
                profObj.put("angelusEveningTime", prof.angelusEveningTime)
                profObj.put("angelusEnabled", prof.angelusEnabled)
                profObj.put("sacredHeartEnabled", prof.sacredHeartEnabled)
                profObj.put("sacredHeartTime", prof.sacredHeartTime)
                profObj.put("immaculateHeartEnabled", prof.immaculateHeartEnabled)
                profObj.put("immaculateHeartTime", prof.immaculateHeartTime)
                profObj.put("liturgicalSeasonNotificationsEnabled", prof.liturgicalSeasonNotificationsEnabled)
                innerJson.put("userProfile", profObj)
            }

            // Ledger Prefs
            val ledgerObj = JSONObject()
            ledgerObj.put("ledger_pwd", ledgerPwd)
            ledgerObj.put("ledger_security_q", ledgerSecurityQ)
            ledgerObj.put("ledger_security_a", ledgerSecurityA)
            innerJson.put("ledgerPrefs", ledgerObj)

            // User Preferences (Virtue Counter, Streaks, Relapse Logs, UI Preferences)
            val userPrefs = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val prefsObj = JSONObject()
            prefsObj.put("virtue_clean_since_timestamp", userPrefs.getLong("virtue_clean_since_timestamp", 0L))
            prefsObj.put("virtue_longest_streak_days", userPrefs.getInt("virtue_longest_streak_days", 0))
            prefsObj.put("virtue_relapse_logs_json", userPrefs.getString("virtue_relapse_logs_json", "[]"))
            prefsObj.put("reading_text_size", userPrefs.getFloat("reading_text_size", 14f).toDouble())
            prefsObj.put("page_view_mode", userPrefs.getString("page_view_mode", "DYNAMIC_AUTO_HIDE"))
            prefsObj.put("app_theme", userPrefs.getString("app_theme", "CATHEDRAL_DARK"))
            prefsObj.put("dark_mode_preference", userPrefs.getString("dark_mode_preference", "DARK"))
            prefsObj.put("manual_devotional_intention", userPrefs.getString("manual_devotional_intention", ""))
            innerJson.put("userPreferences", prefsObj)

            val innerBytes = innerJson.toString().toByteArray(Charsets.UTF_8)

            // 4. Generate Random Salt and IV
            val secureRandom = SecureRandom()
            val salt = ByteArray(SALT_SIZE_BYTES)
            secureRandom.nextBytes(salt)

            val iv = ByteArray(IV_SIZE_BYTES)
            secureRandom.nextBytes(iv)

            // 5. Derive AES Key
            val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BITS)
            val factory = SecretKeyFactory.getInstance(KDF_ALGORITHM)
            val keyBytes = factory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(keyBytes, "AES")

            // 6. Encrypt
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
            val cipherTextBytes = cipher.doFinal(innerBytes)

            // 7. Outer Encrypted Container JSON
            val outerJson = JSONObject()
            outerJson.put("app", "Custos Animae")
            outerJson.put("version", 1)
            outerJson.put("kdf", KDF_ALGORITHM)
            outerJson.put("iterations", ITERATION_COUNT)
            outerJson.put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            outerJson.put("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            outerJson.put("cipherText", Base64.encodeToString(cipherTextBytes, Base64.NO_WRAP))

            // 8. Write to OutputStream
            outputStream.use { stream ->
                stream.write(outerJson.toString(2).toByteArray(Charsets.UTF_8))
                stream.flush()
            }

            val summary = "${examinations.size} Examen entries, ${saintReflections.size} Saint reflections, ${duties.size} Spiritual duties, and app settings backed up securely."
            BackupResult.Success(summary)
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult.Error("Failed to generate encrypted backup: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun restoreEncryptedBackup(
        context: Context,
        appDao: AppDao,
        password: String,
        inputStream: InputStream
    ): RestoreResult {
        return try {
            val jsonString = inputStream.use { it.bufferedReader(Charsets.UTF_8).readText() }

            val outerJson = JSONObject(jsonString)
            if (!outerJson.has("app") || outerJson.getString("app") != "Custos Animae") {
                return RestoreResult.Error("Unrecognized or invalid backup file format.")
            }

            val salt = Base64.decode(outerJson.getString("salt"), Base64.NO_WRAP)
            val iv = Base64.decode(outerJson.getString("iv"), Base64.NO_WRAP)
            val cipherTextBytes = Base64.decode(outerJson.getString("cipherText"), Base64.NO_WRAP)

            // Derive key
            val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BITS)
            val factory = SecretKeyFactory.getInstance(KDF_ALGORITHM)
            val keyBytes = factory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(keyBytes, "AES")

            // Decrypt
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val decryptedBytes = try {
                cipher.doFinal(cipherTextBytes)
            } catch (e: Exception) {
                return RestoreResult.Error("Incorrect password or corrupted/modified backup file.")
            }

            val innerJson = JSONObject(String(decryptedBytes, Charsets.UTF_8))

            // Parse and restore Examinations
            var examCount = 0
            if (innerJson.has("examinations")) {
                val examArray = innerJson.getJSONArray("examinations")
                val examList = mutableListOf<DailyExamination>()
                for (i in 0 until examArray.length()) {
                    val obj = examArray.getJSONObject(i)
                    examList.add(
                        DailyExamination(
                            date = obj.getString("date"),
                            dutiesFulfilled = obj.optBoolean("dutiesFulfilled", false),
                            prayerFaithfulness = obj.optString("prayerFaithfulness", ""),
                            charityPracticed = obj.optString("charityPracticed", ""),
                            temptationResisted = obj.optString("temptationResisted", ""),
                            concreteResolution = obj.optString("concreteResolution", ""),
                            gratitudeNotes = obj.optString("gratitudeNotes", ""),
                            selectedFaults = obj.optString("selectedFaults", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                if (examList.isNotEmpty()) {
                    appDao.insertExaminations(examList)
                    examCount = examList.size
                }
            }

            // Parse and restore Saint Reflections
            var saintCount = 0
            if (innerJson.has("saintReflections")) {
                val saintArray = innerJson.getJSONArray("saintReflections")
                val saintList = mutableListOf<SaintReflection>()
                for (i in 0 until saintArray.length()) {
                    val obj = saintArray.getJSONObject(i)
                    saintList.add(
                        SaintReflection(
                            saintId = obj.getString("saintId"),
                            date = obj.optString("date", ""),
                            userReflection = obj.optString("userReflection", ""),
                            personalResolution = obj.optString("personalResolution", ""),
                            isBookmarked = obj.optBoolean("isBookmarked", false)
                        )
                    )
                }
                if (saintList.isNotEmpty()) {
                    appDao.insertSaintReflections(saintList)
                    saintCount = saintList.size
                }
            }

            // Parse and restore Spiritual Duties
            var dutyCount = 0
            if (innerJson.has("spiritualDuties")) {
                val dutiesArray = innerJson.getJSONArray("spiritualDuties")
                val dutyList = mutableListOf<SpiritualDuty>()
                for (i in 0 until dutiesArray.length()) {
                    val obj = dutiesArray.getJSONObject(i)
                    dutyList.add(
                        SpiritualDuty(
                            id = obj.optInt("id", 0),
                            title = obj.getString("title"),
                            description = obj.optString("description", ""),
                            frequency = obj.optString("frequency", "DAILY"),
                            reminderTime = obj.optString("reminderTime", "08:00"),
                            priority = obj.optString("priority", "SACRED"),
                            category = obj.optString("category", "PRAYER"),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            completedDate = obj.optString("completedDate", ""),
                            completionTimestamp = obj.optLong("completionTimestamp", 0L),
                            personalNotes = obj.optString("personalNotes", ""),
                            streakCount = obj.optInt("streakCount", 0),
                            isSuggestedRule = obj.optBoolean("isSuggestedRule", false),
                            scheduledDaysOfWeek = obj.optString("scheduledDaysOfWeek", ""),
                            scheduledDaysOfMonth = obj.optString("scheduledDaysOfMonth", ""),
                            scheduledDaysOfYear = obj.optString("scheduledDaysOfYear", "")
                        )
                    )
                }
                if (dutyList.isNotEmpty()) {
                    appDao.insertDuties(dutyList)
                    dutyCount = dutyList.size
                }
            }

            // Parse and restore Duty Logs
            var logCount = 0
            if (innerJson.has("dutyLogs")) {
                val logsArray = innerJson.getJSONArray("dutyLogs")
                val logList = mutableListOf<DutyCompletionLog>()
                for (i in 0 until logsArray.length()) {
                    val obj = logsArray.getJSONObject(i)
                    logList.add(
                        DutyCompletionLog(
                            id = obj.optInt("id", 0),
                            dutyId = obj.getInt("dutyId"),
                            date = obj.getString("date"),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                if (logList.isNotEmpty()) {
                    appDao.insertDutyLogs(logList)
                    logCount = logList.size
                }
            }

            // Parse and restore User Profile
            if (innerJson.has("userProfile")) {
                val profObj = innerJson.getJSONObject("userProfile")
                val profile = UserProfile(
                    id = 1,
                    gender = profObj.optString("gender", "MALE"),
                    isConfigured = profObj.optBoolean("isConfigured", true),
                    notificationEnabled = profObj.optBoolean("notificationEnabled", true),
                    morningPrayerTime = profObj.optString("morningPrayerTime", "07:00"),
                    eveningPrayerTime = profObj.optString("eveningPrayerTime", "21:00"),
                    saintNotificationTime = profObj.optString("saintNotificationTime", "07:30"),
                    angelusMorningTime = profObj.optString("angelusMorningTime", "06:00"),
                    angelusNoonTime = profObj.optString("angelusNoonTime", "12:00"),
                    angelusEveningTime = profObj.optString("angelusEveningTime", "18:00"),
                    angelusEnabled = profObj.optBoolean("angelusEnabled", true),
                    sacredHeartEnabled = profObj.optBoolean("sacredHeartEnabled", true),
                    sacredHeartTime = profObj.optString("sacredHeartTime", "08:00"),
                    immaculateHeartEnabled = profObj.optBoolean("immaculateHeartEnabled", true),
                    immaculateHeartTime = profObj.optString("immaculateHeartTime", "08:00"),
                    liturgicalSeasonNotificationsEnabled = profObj.optBoolean("liturgicalSeasonNotificationsEnabled", true)
                )
                appDao.saveUserProfile(profile)
            }

            // Restore Ledger SharedPreferences
            if (innerJson.has("ledgerPrefs")) {
                val ledgerObj = innerJson.getJSONObject("ledgerPrefs")
                val pwd = ledgerObj.optString("ledger_pwd", "")
                val q = ledgerObj.optString("ledger_security_q", "")
                val a = ledgerObj.optString("ledger_security_a", "")

                val ledgerSharedPref = context.getSharedPreferences("confession_ledger_pref", Context.MODE_PRIVATE)
                val editor = ledgerSharedPref.edit()
                if (pwd.isNotEmpty()) editor.putString("ledger_pwd", pwd)
                if (q.isNotEmpty()) editor.putString("ledger_security_q", q)
                if (a.isNotEmpty()) editor.putString("ledger_security_a", a)
                editor.apply()
            }

            // Restore User Preferences (Virtue Counter, Streaks, Relapse Logs, UI Preferences)
            if (innerJson.has("userPreferences")) {
                val prefsObj = innerJson.getJSONObject("userPreferences")
                val userPrefs = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                val userEditor = userPrefs.edit()

                if (prefsObj.has("virtue_clean_since_timestamp")) {
                    userEditor.putLong("virtue_clean_since_timestamp", prefsObj.getLong("virtue_clean_since_timestamp"))
                }
                if (prefsObj.has("virtue_longest_streak_days")) {
                    userEditor.putInt("virtue_longest_streak_days", prefsObj.getInt("virtue_longest_streak_days"))
                }
                if (prefsObj.has("virtue_relapse_logs_json")) {
                    userEditor.putString("virtue_relapse_logs_json", prefsObj.getString("virtue_relapse_logs_json"))
                }
                if (prefsObj.has("reading_text_size")) {
                    userEditor.putFloat("reading_text_size", prefsObj.getDouble("reading_text_size").toFloat())
                }
                if (prefsObj.has("page_view_mode")) {
                    userEditor.putString("page_view_mode", prefsObj.getString("page_view_mode"))
                }
                if (prefsObj.has("app_theme")) {
                    userEditor.putString("app_theme", prefsObj.getString("app_theme"))
                }
                if (prefsObj.has("dark_mode_preference")) {
                    userEditor.putString("dark_mode_preference", prefsObj.getString("dark_mode_preference"))
                }
                if (prefsObj.has("manual_devotional_intention")) {
                    userEditor.putString("manual_devotional_intention", prefsObj.getString("manual_devotional_intention"))
                }
                userEditor.apply()
            }

            val summaryText = "Restored: $examCount Examen entries, $saintCount reflections, $dutyCount duties, $logCount logs, and settings."
            RestoreResult.Success(summaryText)
        } catch (e: Exception) {
            e.printStackTrace()
            RestoreResult.Error("Restore failed: ${e.localizedMessage ?: e.message}")
        }
    }
}
