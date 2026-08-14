package com.example.util

import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val localName: String, val locale: Locale) {
    ENGLISH("en", "English", "English", Locale.ENGLISH);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return ENGLISH
        }
    }
}

object AppTranslationHelper {
    fun init(context: android.content.Context) {}

    fun translate(text: String, targetLanguage: AppLanguage): String {
        return text
    }

    fun translateDynamic(text: String, targetLanguage: AppLanguage): String {
        return text
    }

    fun translatePrayerText(prayerId: String, defaultText: String, targetLanguage: AppLanguage): String {
        return defaultText
    }
}
