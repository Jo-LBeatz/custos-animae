package com.example.data

import android.content.Context
import android.util.Log
import org.json.JSONObject

object LiturgicalJsonPrepopulator {

    private const val TAG = "LiturgicalPrepopulator"
    private const val ASSET_FILE_NAME = "liturgical_data_1954.json"

    suspend fun populateFromJsonAsset(context: Context, appDao: AppDao) {
        try {
            val jsonString = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
            val rootObject = JSONObject(jsonString)

            // 1. Calendar Entries
            if (rootObject.has("calendar_entries")) {
                val calArray = rootObject.getJSONArray("calendar_entries")
                val calendarEntries = mutableListOf<CalendarEntry>()
                for (i in 0 until calArray.length()) {
                    val obj = calArray.getJSONObject(i)
                    val dCode = obj.optString("dateCode", "01-01")
                    val parts = dCode.split("-")
                    val m = parts.getOrNull(0)?.toIntOrNull() ?: 1
                    val d = parts.getOrNull(1)?.toIntOrNull() ?: 1
                    val saint = com.example.data.calendar.SaintData.getSaintForDate(dCode)
                    calendarEntries.add(
                        CalendarEntry(
                            id = dCode,
                            dateString = dCode,
                            month = m,
                            day = d,
                            feastTitle = obj.optString("title").ifBlank { saint?.feastTitle ?: "" },
                            feastRank = obj.optString("rank").ifBlank { saint?.feastRank ?: "" },
                            season = obj.optString("season"),
                            isFastingDay = obj.optBoolean("isFastDay", false),
                            isAbstinenceDay = obj.optBoolean("isAbstinenceDay", false),
                            saintName = saint?.saintName ?: "",
                            commemorated = obj.optString("commemorations").ifBlank { saint?.commemorated ?: "" },
                            biography = saint?.biography ?: "",
                            historicityNote = saint?.historicity ?: "",
                            virtues = saint?.virtues?.joinToString(", ") ?: "",
                            quote = saint?.quote ?: "",
                            meditation = saint?.meditation ?: "",
                            resolution = saint?.resolution ?: "",
                            intercessoryPrayer = saint?.intercessoryPrayer ?: "",
                            notes = obj.optString("rubricsNote")
                        )
                    )
                }
                if (calendarEntries.isNotEmpty()) {
                    appDao.insertCalendarEntries(calendarEntries)
                }
            }

            // 2. Prayers
            if (rootObject.has("prayers")) {
                val prayerArray = rootObject.getJSONArray("prayers")
                val prayers = mutableListOf<PrayerEntity>()
                for (i in 0 until prayerArray.length()) {
                    val obj = prayerArray.getJSONObject(i)
                    prayers.add(
                        PrayerEntity(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            category = obj.optString("category"),
                            subtitle = obj.optString("subtitle"),
                            englishText = obj.optString("englishText"),
                            latinText = obj.optString("latinText"),
                            notes = obj.optString("notes")
                        )
                    )
                }
                if (prayers.isNotEmpty()) {
                    appDao.insertPrayers(prayers)
                }
            }

            // 4. Breviary Entries
            if (rootObject.has("breviary_entries")) {
                val breviaryArray = rootObject.getJSONArray("breviary_entries")
                val breviaryEntries = mutableListOf<BreviaryEntryEntity>()
                for (i in 0 until breviaryArray.length()) {
                    val obj = breviaryArray.getJSONObject(i)
                    breviaryEntries.add(
                        BreviaryEntryEntity(
                            id = obj.optString("id"),
                            dateCode = obj.optString("dateCode"),
                            year = obj.optInt("year", 2026),
                            month = obj.optInt("month", 7),
                            day = obj.optInt("day", 31),
                            hourId = obj.optString("hourId"),
                            title = obj.optString("title"),
                            latinTitle = obj.optString("latinTitle"),
                            season = obj.optString("season", "Time after Pentecost"),
                            openingVersicleLatin = obj.optString("openingVersicleLatin", "V. Deus in adiutorium meum intende. R. Domine ad adiuvandum me festina."),
                            openingVersicleEnglish = obj.optString("openingVersicleEnglish", "V. O God, come to my assistance. R. O Lord, make haste to help me."),
                            invitatoryLatin = obj.optString("invitatoryLatin", ""),
                            invitatoryEnglish = obj.optString("invitatoryEnglish", ""),
                            hymnLatin = obj.optString("hymnLatin"),
                            hymnEnglish = obj.optString("hymnEnglish"),
                            psalmsSummary = obj.optString("psalmsLatin"),
                            capitulumLatin = obj.optString("capitulumLatin", ""),
                            capitulumEnglish = obj.optString("capitulumEnglish", ""),
                            readingsLatin = obj.optString("readingsLatin"),
                            readingsEnglish = obj.optString("readingsEnglish"),
                            canticleTitle = obj.optString("canticleTitle", "Benedictus / Magnificat"),
                            canticleAntiphonLatin = obj.optString("canticleAntiphonLatin", ""),
                            canticleAntiphonEnglish = obj.optString("canticleAntiphonEnglish", ""),
                            canticleLatin = obj.optString("canticleLatin", ""),
                            canticleEnglish = obj.optString("canticleEnglish", ""),
                            collectLatin = obj.optString("oratioLatin"),
                            collectEnglish = obj.optString("oratioEnglish"),
                            marianAntiphonTitle = obj.optString("marianAntiphonTitle", "Salve Regina"),
                            marianAntiphonLatin = obj.optString("marianAntiphonLatin", ""),
                            marianAntiphonEnglish = obj.optString("marianAntiphonEnglish", ""),
                            rubrics = obj.optString("rubrics", "1954 Rubrics")
                        )
                    )
                }
                if (breviaryEntries.isNotEmpty()) {
                    appDao.insertBreviaryEntries(breviaryEntries)
                }
            }

            Log.d(TAG, "Successfully pre-populated Room DB from asset JSON file.")
        } catch (e: Exception) {
            Log.e(TAG, "Error pre-populating Room DB from JSON asset", e)
        }
    }
}
