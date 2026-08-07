package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.SaintQuoteEntity
import com.example.data.calendar.SaintData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SaintQuoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SaintQuoteWorker", "Starting SaintQuoteWorker")
        val db = AppDatabase.getDatabase(applicationContext)

        // 1. Get the Saint of the Day
        val cal = Calendar.getInstance()
        val mmdd = String.format("%02d-%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        val saint = SaintData.getSaintForDate(mmdd) ?: SaintData.saintList.first()

        Log.d("SaintQuoteWorker", "Current Saint of the Day: ${saint.saintName} ($mmdd)")

        // 2. Check if quotes are already cached in DB for today's Saint to avoid double fetching
        val existingQuotes = db.appDao().getQuotesForSaintSync(mmdd)
        if (existingQuotes.isNotEmpty()) {
            Log.d("SaintQuoteWorker", "Quotes for $mmdd (${saint.saintName}) already exist in Room database.")
            return Result.success()
        }

        // 3. Attempt to fetch quotes via Gemini API
        val fetchedQuotes = mutableListOf<SaintQuoteEntity>()
        var apiSuccess = false

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                val prompt = """
                    Generate 3 inspiring, spiritual and faithful quotes attributed to or associated with Saint ${saint.saintName} (${saint.feastTitle}), who is commemorated on $mmdd.
                    Format the response STRICTLY as a JSON array of objects with 'quoteText' and 'sourceWork' keys.
                    Example format:
                    [
                      {"quoteText": "Love God and do what you will.", "sourceWork": "Homilies on 1 John"},
                      {"quoteText": "Patience is the companion of wisdom.", "sourceWork": "De Patientia"}
                    ]
                    Do not include any markdown formatting, backticks, or prefix like ```json. Return only the raw JSON array.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("responseSchema", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply {
                                put("type", "OBJECT")
                                put("properties", JSONObject().apply {
                                    put("quoteText", JSONObject().apply {
                                        put("type", "STRING")
                                    })
                                    put("sourceWork", JSONObject().apply {
                                        put("type", "STRING")
                                    })
                                })
                                put("required", JSONArray().apply {
                                    put("quoteText")
                                })
                            })
                        })
                    })
                }

                val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                Log.d("SaintQuoteWorker", "Calling Gemini API to fetch quotes for ${saint.saintName}")
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseStr = response.body?.string()
                        if (!responseStr.isNullOrBlank()) {
                            Log.d("SaintQuoteWorker", "Gemini response received: $responseStr")
                            val json = JSONObject(responseStr)
                            val candidates = json.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val text = candidates.getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text")

                                Log.d("SaintQuoteWorker", "Extracted JSON text: $text")
                                
                                val sanitizedText = text.trim()
                                val jsonArray = try {
                                    JSONArray(sanitizedText)
                                } catch (e: Exception) {
                                    Log.w("SaintQuoteWorker", "Initial JSON parsing failed, attempting repair & extraction", e)
                                    // Robust regex extraction fallback for malformed JSON blocks
                                    val list = JSONArray()
                                    val objectPattern = Regex("""\{[^{}]+\}""")
                                    val objectMatches = objectPattern.findAll(sanitizedText)
                                    for (objMatch in objectMatches) {
                                        val objStr = objMatch.value
                                        val quoteMatch = Regex(""""quoteText"\s*:\s*"([^"\\]*(?:\\.[^"\\]*)*)"""").find(objStr)
                                        val sourceMatch = Regex(""""sourceWork"\s*:\s*"([^"\\]*(?:\\.[^"\\]*)*)"""").find(objStr)
                                        val qText = quoteMatch?.groups[1]?.value
                                        val sWork = sourceMatch?.groups[1]?.value ?: ""
                                        if (qText != null) {
                                            list.put(JSONObject().apply {
                                                put("quoteText", qText)
                                                put("sourceWork", sWork)
                                            })
                                        }
                                    }
                                    list
                                }

                                for (i in 0 until jsonArray.length()) {
                                    val obj = jsonArray.getJSONObject(i)
                                    val quoteText = obj.getString("quoteText")
                                    val sourceWork = obj.optString("sourceWork", "")
                                    fetchedQuotes.add(
                                        SaintQuoteEntity(
                                            saintId = mmdd,
                                            saintName = saint.saintName,
                                            quoteText = quoteText,
                                            sourceWork = sourceWork
                                        )
                                    )
                                }
                                apiSuccess = fetchedQuotes.isNotEmpty()
                            }
                        }
                    } else {
                        Log.e("SaintQuoteWorker", "Gemini API call failed with code: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SaintQuoteWorker", "Error while calling Gemini API: ${e.message}", e)
            }
        } else {
            Log.w("SaintQuoteWorker", "Gemini API key is blank or placeholder, using offline fallbacks")
        }

        // 4. Fallback generation if API fails or is not configured
        if (!apiSuccess) {
            Log.d("SaintQuoteWorker", "Generating fallback offline quotes for ${saint.saintName}")
            val saintQuote = saint.quote.ifBlank { "Begin with prayer, continue with devotion, end with praise." }
            
            // We can split/reformat the default quote, or add standard inspirational spiritual quotes
            fetchedQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = saintQuote,
                    sourceWork = "Traditional Reflection"
                )
            )

            // Let's add standard encouraging spiritual quotes for fallback variety
            if (saint.virtues.isNotEmpty()) {
                val virtueStr = saint.virtues.joinToString(", ")
                fetchedQuotes.add(
                    SaintQuoteEntity(
                        saintId = mmdd,
                        saintName = saint.saintName,
                        quoteText = "Let us labor diligently to acquire the virtues of $virtueStr, that our souls may resemble the stained glass reflecting His divine light.",
                        sourceWork = "Custos Animae Rule"
                    )
                )
            } else {
                fetchedQuotes.add(
                    SaintQuoteEntity(
                        saintId = mmdd,
                        saintName = saint.saintName,
                        quoteText = "In all things, maintain the sacred rules and disciplines, walking in humility and quiet meditation.",
                        sourceWork = "Regula Animae"
                    )
                )
            }

            fetchedQuotes.add(
                SaintQuoteEntity(
                    saintId = mmdd,
                    saintName = saint.saintName,
                    quoteText = "Turn away from worldly distractions, and let your heart find peace in holy silence and sacred duties.",
                    sourceWork = "Sacred Traditions"
                )
            )
        }

        // 5. Store quotes in Room Database
        try {
            db.appDao().deleteQuotesForSaint(mmdd) // Clear old ones if any
            db.appDao().insertSaintQuotes(fetchedQuotes)
            Log.d("SaintQuoteWorker", "Successfully stored ${fetchedQuotes.size} quotes in Room database.")
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
