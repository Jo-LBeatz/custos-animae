package com.example.util

import com.example.data.prayers.PrayerItem

enum class PrayerMode(val code: String, val label: String, val badgeText: String, val description: String) {
    INDIVIDUAL(
        code = "INDIVIDUAL",
        label = "Praying Solo (Personal)",
        badgeText = "Solo (I / Me)",
        description = "Personal prayer mode using first-person singular (I / me / my)."
    ),
    GROUP(
        code = "GROUP",
        label = "Praying in a Group (Communal)",
        badgeText = "Group (We / Us)",
        description = "Communal prayer mode using plural language (We / us / our)."
    ),
    TRADITIONAL(
        code = "TRADITIONAL",
        label = "Traditional (Canonical As Written)",
        badgeText = "Traditional (As Written)",
        description = "Exact canonical texts as written in traditional prayer books without any changes."
    );

    companion object {
        fun fromCode(code: String?): PrayerMode {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: TRADITIONAL
        }
    }
}

object PrayerPerspectiveFormatter {

    /**
     * Identifies if a prayer text line or block is strictly the "Our Father" (Pater Noster)
     * or "Hail Mary" (Ave Maria) prayer, which MUST remain in their exact canonical
     * form regardless of user perspective mode selection.
     */
    fun isOurFatherOrHailMary(text: String): Boolean {
        val clean = text.lowercase().trim()
        if (clean.isBlank()) return false

        // Our Father indicators (English & Latin)
        val isOurFather = (
            clean.contains("our father, who art in heaven") ||
            clean.contains("pater noster, qui es in caelis") ||
            clean.startsWith("our father") ||
            clean.startsWith("pater noster") ||
            (clean.contains("hallowed be thy name") && clean.contains("daily bread"))
        )

        // Hail Mary indicators (English & Latin)
        val isHailMary = (
            clean.contains("hail mary, full of grace") ||
            clean.contains("ave maria, gratia plena") ||
            clean.startsWith("hail mary") ||
            clean.startsWith("ave maria") ||
            (clean.contains("full of grace, the lord is with thee") && clean.contains("fruit of thy womb"))
        )

        return isOurFather || isHailMary
    }

    private val phrasesList = listOf(
        // High priority multi-word phrases for all other prayers (Saint intercessions, Litanies, Rosary Fatima/Closing, etc.)
        Regex("(?i)\\bdefend us in battle\\b") to "defend me in battle",
        Regex("(?i)\\bbe our protection\\b") to "be my protection",
        Regex("(?i)\\bwe humbly pray\\b") to "I humbly pray",
        Regex("(?i)\\bobtain for us\\b") to "obtain for me",
        Regex("(?i)\\bextend thy protection over us\\b") to "extend thy protection over me",
        Regex("(?i)\\bpray for us, O Holy Mother of God\\b") to "pray for me, O Holy Mother of God",
        Regex("(?i)\\bpray for us\\b") to "pray for me",
        Regex("(?i)\\bintercede for us\\b") to "intercede for me",
        Regex("(?i)\\bhave mercy on us\\b") to "have mercy on me",
        Regex("(?i)\\bprotect us\\b") to "protect me",
        Regex("(?i)\\bguide us\\b") to "guide me",
        Regex("(?i)\\bbless us\\b") to "bless me",
        Regex("(?i)\\bsave us\\b") to "save me",
        Regex("(?i)\\bhelp us\\b") to "help me",
        Regex("(?i)\\bfor us\\b") to "for me",
        Regex("(?i)\\bover us\\b") to "over me",
        Regex("(?i)\\bwith us\\b") to "with me",
        Regex("(?i)\\bto us\\b") to "to me",
        Regex("(?i)\\bupon us\\b") to "upon me",
        Regex("(?i)\\bagainst us\\b") to "against me",
        
        // Single word transformations with case preservation
        Regex("\\bOur\\b") to "My",
        Regex("\\bour\\b") to "my",
        Regex("\\bOUR\\b") to "MY",
        Regex("\\bWe\\b") to "I",
        Regex("\\bwe\\b") to "I",
        Regex("\\bWE\\b") to "I",
        Regex("\\bUs\\b") to "Me",
        Regex("\\bus\\b") to "me",
        Regex("\\bUS\\b") to "ME"
    )

    fun format(text: String, mode: PrayerMode): String {
        if (mode == PrayerMode.TRADITIONAL || mode == PrayerMode.GROUP || text.isBlank()) return text

        // Split by lines to preserve Our Father & Hail Mary lines in combined/multi-line prayer texts
        val lines = text.split("\n")
        val resultLines = lines.map { line ->
            if (isOurFatherOrHailMary(line)) {
                // EXEMPT "Our Father" and "Hail Mary" - Keep exact traditional/canonical text!
                line
            } else {
                var transformed = line
                for ((regex, replacement) in phrasesList) {
                    transformed = regex.replace(transformed, replacement)
                }
                transformed
            }
        }
        return resultLines.joinToString("\n")
    }
}

fun PrayerItem.getFormattedEnglishText(mode: PrayerMode): String {
    return PrayerPerspectiveFormatter.format(englishText, mode)
}

fun PrayerItem.getFormattedText(isLatin: Boolean, mode: PrayerMode): String {
    return if (isLatin && latinText.isNotBlank()) {
        latinText
    } else {
        PrayerPerspectiveFormatter.format(englishText, mode)
    }
}
