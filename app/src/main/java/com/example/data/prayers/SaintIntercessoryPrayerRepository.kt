package com.example.data.prayers

import android.content.Context
import android.util.Log
import com.example.data.calendar.SaintData
import com.example.data.calendar.SaintProfile
import com.example.util.NetworkConnectivityMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

data class SaintPrayerContent(
    val saintId: String,
    val saintName: String,
    val feastTitle: String,
    val feastRank: String,
    val intercessoryPrayerEnglish: String,
    val intercessoryPrayerLatin: String,
    val collectEnglish: String,
    val collectLatin: String,
    val versicleLatin: String,
    val versicleEnglish: String,
    val responseLatin: String,
    val responseEnglish: String,
    val concludingFormulaLatin: String,
    val concludingFormulaEnglish: String,
    val sourceCitation: String = "1954 Missale Romanum & Breviarium Romanum (Pope Pius XII Edition)",
    val isRemoteFetched: Boolean = false,
    val isOfflineFallback: Boolean = false,
    val rubricNote: String = "1954 Missale Romanum & Breviarium Romanum • Exclusiviter Liturgicum"
)

sealed class SaintPrayerUiState {
    object Loading : SaintPrayerUiState()
    data class Success(val prayer: SaintPrayerContent) : SaintPrayerUiState()
    data class OfflineFallback(val prayer: SaintPrayerContent) : SaintPrayerUiState()
    data class Error(val message: String, val fallbackPrayer: SaintPrayerContent?) : SaintPrayerUiState()
}

/**
 * Authentic 100% offline Local Repository for traditional 1954 Roman Rite Intercessory Prayers
 * and Collects (Missale Romanum & Breviarium Romanum, Pope Pius XII Edition).
 */
class SaintIntercessoryPrayerRepository(
    private val context: Context
) {
    // In-memory cache for ultra-fast switching between saints
    private val memoryCache = ConcurrentHashMap<String, SaintPrayerContent>()

    /**
     * Retrieves the authentic traditional 1954 Roman Rite prayer for a saint profile strictly offline.
     */
    suspend fun getSaintPrayer(
        saintProfile: SaintProfile,
        forceRefresh: Boolean = false
    ): SaintPrayerContent = withContext(Dispatchers.IO) {
        val cacheKey = saintProfile.saintId
        if (!forceRefresh && memoryCache.containsKey(cacheKey)) {
            return@withContext memoryCache[cacheKey]!!
        }

        val prayerData = Traditional1954PrayerRegistry.getTraditionalPrayer(context, saintProfile)
        memoryCache[saintProfile.saintId] = prayerData
        return@withContext prayerData
    }

    /**
     * Local Collect from the 1954 Roman Missal asset.
     */
    fun getLocalCollectFallback(saint: SaintProfile): SaintPrayerContent {
        val collectData = Traditional1954PrayerRegistry.getTraditionalCollect(context, saint)
        memoryCache[saint.saintId] = collectData
        return collectData
    }
}

/**
 * Authentic Registry of Traditional Latin and English 1954 Roman Rite Collects,
 * Intercessions, and Versicles loaded from local hardcoded JSON asset.
 */
object Traditional1954PrayerRegistry {

    private var cachedJsonObj: org.json.JSONObject? = null

    private fun getJsonAsset(context: Context): org.json.JSONObject {
        cachedJsonObj?.let { return it }
        return try {
            val jsonString = context.assets.open("traditional_intercessory_prayers_1954.json")
                .bufferedReader().use { it.readText() }
            val obj = org.json.JSONObject(jsonString)
            cachedJsonObj = obj
            obj
        } catch (e: Exception) {
            Log.e("1954PrayerRegistry", "Error reading local JSON asset", e)
            org.json.JSONObject()
        }
    }

    fun getTraditionalPrayer(context: Context, saint: SaintProfile): SaintPrayerContent {
        val root = getJsonAsset(context)
        val categories = root.optJSONObject("categories")
        val concluding = root.optJSONObject("concludingFormula")

        val concludingLat = concluding?.optString("latin")
            ?: "Per Dominum nostrum Jesum Christum, Filium tuum, qui tecum vivit et regnat in unitate Spiritus Sancti, Deus, per omnia sæcula sæculorum. ℟. Amen."
        val concludingEng = concluding?.optString("english")
            ?: "Through Our Lord Jesus Christ, Thy Son, who livest and reignest with Thee in the unity of the Holy Ghost, God, world without end. ℟. Amen."

        val saintName = saint.saintName
        val title = saint.feastTitle
        val isMary = title.contains("Mary", ignoreCase = true) || (title.contains("Virgin", ignoreCase = true) && title.contains("Blessed", ignoreCase = true)) || title.contains("Our Lady", ignoreCase = true) || title.contains("Purification", ignoreCase = true) || title.contains("Assumption", ignoreCase = true) || title.contains("Immaculate", ignoreCase = true)
        val isJoseph = title.contains("Joseph", ignoreCase = true) || saintName.contains("Joseph", ignoreCase = true)
        val isApostle = title.contains("Apostle", ignoreCase = true) || title.contains("Evangelist", ignoreCase = true)
        val isMartyr = title.contains("Martyr", ignoreCase = true)
        val isDoctor = title.contains("Doctor", ignoreCase = true)
        val isPope = title.contains("Pope", ignoreCase = true) || title.contains("Pontiff", ignoreCase = true)
        val isBishop = title.contains("Bishop", ignoreCase = true)
        val isVirgin = title.contains("Virgin", ignoreCase = true)
        val isAngels = title.contains("Angel", ignoreCase = true) || title.contains("Michael", ignoreCase = true) || title.contains("Gabriel", ignoreCase = true) || title.contains("Raphael", ignoreCase = true)

        val catKey = when {
            isJoseph -> "joseph"
            isMary -> "mary"
            isApostle -> "apostle"
            isAngels -> "angels"
            isMartyr -> "martyr"
            isDoctor -> "doctor"
            isPope || isBishop -> "pope_bishop"
            isVirgin -> "virgin"
            else -> "confessor_common"
        }

        val catObj = categories?.optJSONObject(catKey)

        val intercessionLatRaw = catObj?.optString("intercessionLatin")
            ?: "Sancte {saintName}, qui per virtutes tuas Deo placuisti et ad gloriam cœlestem ascendisti: accipe preces nostras, et apud thronum divinæ Maiestatis esto nobis fidelis intercessor."
        val intercessionEngRaw = catObj?.optString("intercessionEnglish")
            ?: "O Holy {saintName}, who by thy virtues didst please God and ascend to heavenly glory: receive our prayers, and before the throne of divine Majesty be our faithful intercessor."

        val collectLatRaw = catObj?.optString("collectLatin")
            ?: "Deus, qui nos beati {saintName} Confessoris tui annua solemnitate lætificas: concede propitius; ut, cuius natalitia colimus, etiam actiones imitemur."
        val collectEngRaw = catObj?.optString("collectEnglish")
            ?: "O God, who dost gladden us by the yearly festival of blessed {saintName}, Thy Confessor: mercifully grant, that as we celebrate his heavenly birthday, we may also imitate his holy actions."

        val versicleLatRaw = catObj?.optString("versicleLatin") ?: "℣. Ora pro nobis, beate {saintName}."
        val versicleEngRaw = catObj?.optString("versicleEnglish") ?: "℣. Pray for us, blessed {saintName}."
        val responseLat = catObj?.optString("responseLatin") ?: "℟. Ut digni efficiamur promissionibus Christi."
        val responseEng = catObj?.optString("responseEnglish") ?: "℟. That we may be made worthy of the promises of Christ."

        val intercessionLat = intercessionLatRaw.replace("{saintName}", saintName)
        val intercessionEng = intercessionEngRaw.replace("{saintName}", saintName)
        val collectLat = collectLatRaw.replace("{saintName}", saintName)
        val collectEng = collectEngRaw.replace("{saintName}", saintName)
        val versicleLat = versicleLatRaw.replace("{saintName}", saintName)
        val versicleEng = versicleEngRaw.replace("{saintName}", saintName)

        return SaintPrayerContent(
            saintId = saint.saintId,
            saintName = saint.saintName,
            feastTitle = saint.feastTitle,
            feastRank = saint.feastRank,
            intercessoryPrayerEnglish = intercessionEng,
            intercessoryPrayerLatin = intercessionLat,
            collectEnglish = collectEng,
            collectLatin = collectLat,
            versicleLatin = versicleLat,
            versicleEnglish = versicleEng,
            responseLatin = responseLat,
            responseEnglish = responseEng,
            concludingFormulaLatin = concludingLat,
            concludingFormulaEnglish = concludingEng,
            sourceCitation = "1954 Missale Romanum & Breviarium Romanum (Asset Resource)",
            isRemoteFetched = false,
            isOfflineFallback = false
        )
    }

    fun getTraditionalCollect(context: Context, saint: SaintProfile): SaintPrayerContent {
        val prayer = getTraditionalPrayer(context, saint)
        return prayer.copy(
            intercessoryPrayerEnglish = prayer.collectEnglish,
            intercessoryPrayerLatin = prayer.collectLatin,
            sourceCitation = "1954 Roman Missal • Collect Prayer (Asset Resource)"
        )
    }
}
