package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// -------------------------------------------------------------
// 1. CATHEDRAL GOTHIC PALETTES
// -------------------------------------------------------------
private val DarkCathedralColorScheme = darkColorScheme(
    primary = StaticGoldSacred,
    onPrimary = Color.Black,
    primaryContainer = StaticCrimsonBurgundy,
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldBright,
    onSecondary = Color.Black,
    secondaryContainer = StaticDarkCathedralSurfaceVariant,
    onSecondaryContainer = StaticTextGoldHighlight,
    tertiary = StaticCrimsonLight,
    onTertiary = Color.White,
    background = StaticDarkCathedralBackground,
    onBackground = StaticParchmentLight,
    surface = StaticDarkCathedralSurface,
    onSurface = StaticParchmentLight,
    surfaceVariant = StaticDarkCathedralSurfaceVariant,
    onSurfaceVariant = StaticTextMutedParchment,
    outline = StaticGoldDark,
    outlineVariant = StaticDarkCathedralCardBorder
)

private val LightCathedralColorScheme = lightColorScheme(
    primary = StaticCrimsonBurgundy,        // Rich Missal Rubric Red (#800020)
    onPrimary = StaticGoldBright,           // Radiant Illuminated Gold on red
    primaryContainer = Color(0xFFF2EAD8),   // Soft Warm Aged Parchment Vellum
    onPrimaryContainer = StaticGoldenBrown, // Deep Monastic Ink Brown
    secondary = StaticWarmBronze,           // Antique Bronze Gold
    onSecondary = StaticParchmentLight,
    secondaryContainer = Color(0xFFECE2CB), // Muted Ivory Vellum Tint
    onSecondaryContainer = StaticGoldenBrown, // Rich Golden Ink
    tertiary = StaticGoldenBrown,           // Deep Monastic Ink Brown
    onTertiary = StaticLightGold,           // Light Gold
    background = Color(0xFFFAF5EA),         // Authentic Muted Ancient Parchment Background
    onBackground = Color(0xFF241C15),       // Deep Ancient Monastic Ink text
    surface = Color(0xFFF4EEE0),            // Traditional Prayer Book Page Parchment Surface
    onSurface = Color(0xFF241C15),          // Deep Ancient Monastic Ink text
    surfaceVariant = Color(0xFFEAE2CE),     // Aged Prayer Book Card Vellum
    onSurfaceVariant = Color(0xFF4A3D30),   // Muted Antique Warm Ink text
    outline = StaticWarmBronze,             // Antique Bronze outline
    outlineVariant = Color(0xFFD3C6AE)      // Aged Parchment Card border
)

// -------------------------------------------------------------
// 2. ROSA MYSTICA (MARIAN ROSE & MINT) PALETTES
// -------------------------------------------------------------
private val DarkRosaMysticaColorScheme = darkColorScheme(
    primary = Color(0xFFF09AB1),            // Luminous Mystic Rose
    onPrimary = Color(0xFF3B0B19),          // Deep Velvet Rose
    primaryContainer = Color(0xFF4D1C2A),   // Deep Plum Rose Container
    onPrimaryContainer = Color(0xFFFFD9E2), // Soft Rose Cream
    secondary = Color(0xFFA5DCBE),          // Glowing Mint Frost Accent
    onSecondary = Color(0xFF0D3320),
    secondaryContainer = Color(0xFF1E382A), // Dark Mint Pine Container
    onSecondaryContainer = Color(0xFFD6F5E3),
    tertiary = Color(0xFFD9C6E2),          // Frosted Lavender Mist
    onTertiary = Color(0xFF2C1A38),
    background = Color(0xFF140B10),         // Velvet Mystic Rose-Black
    onBackground = Color(0xFFFDF0F3),       // Soft Rose Parchment White
    surface = Color(0xFF1F1018),            // Midnight Rose Surface
    onSurface = Color(0xFFFDF0F3),
    surfaceVariant = Color(0xFF2C1622),     // Velvet Rose-Plum Surface Variant
    onSurfaceVariant = Color(0xFFDECAD2),   // Muted Rose Ash
    outline = Color(0xFFC77891),            // Mystic Rose Outline
    outlineVariant = Color(0xFF482736)      // Velvet Rose Card Border
)

private val LightRosaMysticaColorScheme = lightColorScheme(
    primary = StaticPastelRoseAccent,         // Soft Pastel Rose (#D98A9F)
    onPrimary = Color.White,
    primaryContainer = StaticSoftRose,        // Soft Rose (#F7D9E0)
    onPrimaryContainer = StaticPastelRoseText,// Muted Soft Berry-Rose Text (#6B3D4B)
    secondary = StaticPastelMintAccent,       // Soft Pastel Mint (#9ECBAF)
    onSecondary = Color.White,
    secondaryContainer = StaticMintFrost,     // Mint Frost (#D6E9DC)
    onSecondaryContainer = StaticPastelMintText, // Muted Soft Mint Text (#385E48)
    tertiary = StaticPastelLavenderAccent,    // Soft Pastel Lavender (#B5A1BD)
    onTertiary = Color.White,
    tertiaryContainer = StaticLavenderMist,   // Lavender Mist (#C6B7CC)
    onTertiaryContainer = StaticPastelLavenderText, // Muted Soft Lavender Text (#4A3554)
    background = StaticBlushCream,            // Rose Base Color: Blush Cream (#FDEEF1)
    onBackground = StaticDarkTextCharcoal,    // Soft Warm Charcoal Text (#23161B)
    surface = StaticSoftRose,                 // Soft Rose (#F7D9E0)
    onSurface = StaticDarkTextCharcoal,       // Soft Warm Charcoal Text
    surfaceVariant = StaticBlushCream,        // Blush Cream (#FDEEF1)
    onSurfaceVariant = StaticMutedTextRose,   // Muted Soft Rose Text (#5E424B)
    outline = StaticLavenderMist,             // Lavender Mist (#C6B7CC)
    outlineVariant = Color(0xFFF1DEE4)        // Soft Rose Accent Border
)

// -------------------------------------------------------------
// 3. PENITENTIAL VIOLET (LENT & ADVENT) PALETTES
// -------------------------------------------------------------
private val DarkPenitentialVioletColorScheme = darkColorScheme(
    primary = Color(0xFFCE93D8),            // Luminous Violet Light
    onPrimary = Color(0xFF2E004F),
    primaryContainer = Color(0xFF4A148C),   // Deep Imperial Violet
    onPrimaryContainer = StaticGoldBright,  // #FFD700
    secondary = StaticGoldSacred,           // Sacred Gold
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E1C44), // Dark Violet Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFF48FB1),           // Rose-Violet Accent
    onTertiary = Color(0xFF491024),
    background = Color(0xFF0F0B18),         // Obsidian Violet-Black
    onBackground = Color(0xFFF4EEF8),
    surface = Color(0xFF1B1229),            // Deep Imperial Amethyst Surface
    onSurface = Color(0xFFF4EEF8),
    surfaceVariant = Color(0xFF271A3B),
    onSurfaceVariant = Color(0xFFD1C4E9),   // Light Lavender text
    outline = Color(0xFF9C27B0),
    outlineVariant = Color(0xFF4A3266)      // Violet Card Border
)

private val LightPenitentialVioletColorScheme = lightColorScheme(
    primary = Color(0xFF6A1B9A),            // Imperial Liturgical Violet
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1DCF7),   // Soft Lilac Container
    onPrimaryContainer = Color(0xFF38006B), // Deep Violet text
    secondary = StaticWarmBronze,           // Bronze Gold
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF6ECF9),
    onSecondaryContainer = Color(0xFF511D6B),
    tertiary = Color(0xFF8E24AA),
    onTertiary = Color.White,
    background = Color(0xFFFAF6FC),         // Pale Violet-Tinted Alabaster
    onBackground = Color(0xFF1E1424),       // Dark Violet-Charcoal text
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1424),
    surfaceVariant = Color(0xFFF0E5F5),     // Pale Lilac Mist Card
    onSurfaceVariant = Color(0xFF553D61),
    outline = Color(0xFF8E24AA),
    outlineVariant = Color(0xFFD4BFE0)
)

// -------------------------------------------------------------
// 4. MARIAN BLUE (FEASTS OF OUR LADY) PALETTES
// -------------------------------------------------------------
private val DarkMarianBlueColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),            // Luminous Celestial Azure
    onPrimary = Color(0xFF002244),
    primaryContainer = Color(0xFF0D2D5E),   // Deep Ultramarine
    onPrimaryContainer = StaticGoldBright,  // Stella Maris Gold
    secondary = StaticGoldSacred,           // Gold Sacred
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF173054), // Dark Blue Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFF90CAF9),           // Soft Cyan Blue
    onTertiary = Color(0xFF0D325E),
    background = Color(0xFF070D18),         // Starry Midnight Abyss
    onBackground = Color(0xFFF0F6FF),       // Celestial Light Blue text
    surface = Color(0xFF0F1B2E),            // Deep Starry Cathedral Surface
    onSurface = Color(0xFFF0F6FF),
    surfaceVariant = Color(0xFF182842),
    onSurfaceVariant = Color(0xFFC2D9F7),   // Muted Starry Blue text
    outline = Color(0xFF1E88E5),
    outlineVariant = Color(0xFF2D4B75)      // Blue Card Border
)

private val LightMarianBlueColorScheme = lightColorScheme(
    primary = Color(0xFF0D47A1),            // Royal Marian Ultramarine
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8EAFD),
    onPrimaryContainer = Color(0xFF052A6E),
    secondary = StaticWarmBronze,           // Ocean Bronze Gold
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECF4FC),
    onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = Color(0xFF1565C0),
    onTertiary = Color.White,
    background = Color(0xFFF4F8FD),         // Soft Azure Dawn Mist
    onBackground = Color(0xFF101C2B),       // Deep Navy Charcoal text
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF101C2B),
    surfaceVariant = Color(0xFFE5EFF9),     // Pale Azure Mist Card
    onSurfaceVariant = Color(0xFF384B61),
    outline = Color(0xFF1976D2),
    outlineVariant = Color(0xFFB8D3EE)
)

// -------------------------------------------------------------
// 5. MARTYR CRIMSON (FEASTS OF MARTYRS) PALETTES
// -------------------------------------------------------------
private val DarkMartyrCrimsonColorScheme = darkColorScheme(
    primary = Color(0xFFEF5350),            // Vivid Martyr Ruby
    onPrimary = Color(0xFF400008),
    primaryContainer = Color(0xFF5A0818),   // Deep Crimson Container
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldSacred,           // Crown of Martyrs Gold
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D151D), // Dark Ruby Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFFF8A80),           // Vivid Coral-Rose
    onTertiary = Color(0xFF4E1117),
    background = Color(0xFF13080A),         // Obsidian Crimson-Black
    onBackground = Color(0xFFFFF0F2),       // Soft Ruby Parchment text
    surface = Color(0xFF220E13),            // Deep Crimson Velvet Surface
    onSurface = Color(0xFFFFF0F2),
    surfaceVariant = Color(0xFF32161C),
    onSurfaceVariant = Color(0xFFFFCDD2),   // Muted Ruby text
    outline = StaticCrimsonBurgundy,
    outlineVariant = Color(0xFF5C262F)      // Crimson Card Border
)

private val LightMartyrCrimsonColorScheme = lightColorScheme(
    primary = Color(0xFF900C1E),            // Cardinal Martyr Red
    onPrimary = StaticGoldBright,           // Gold on Cardinal Red
    primaryContainer = Color(0xFFFCE4E6),
    onPrimaryContainer = Color(0xFF540410),
    secondary = StaticWarmBronze,           // Bronze Gold
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAF0F1),
    onSecondaryContainer = Color(0xFF7A0A19),
    tertiary = Color(0xFFB71C1C),
    onTertiary = Color.White,
    background = Color(0xFFFDF6F6),         // Warm Alabaster with soft crimson warmth
    onBackground = Color(0xFF261214),       // Charcoal Ruby text
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF261214),
    surfaceVariant = Color(0xFFF9EAEB),     // Pale Crimson-Rose Card
    onSurfaceVariant = Color(0xFF5C3B3E),
    outline = Color(0xFFB71C1C),
    outlineVariant = Color(0xFFE2BFC3)
)

// -------------------------------------------------------------
// 6. ORDINARY GREEN (ORDINARY TIME) PALETTES
// -------------------------------------------------------------
private val DarkOrdinaryGreenColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),            // Liturgical Emerald Light
    onPrimary = Color(0xFF00330D),
    primaryContainer = Color(0xFF144D1E),   // Deep Forest Green Container
    onPrimaryContainer = StaticGoldBright,  // Harvest Gold
    secondary = StaticGoldSacred,           // Sacred Gold
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A3821), // Dark Spruce Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFA5D6A7),           // Soft Meadow Sage
    onTertiary = Color(0xFF083812),
    background = Color(0xFF071109),         // Abyssal Spruce Green-Black
    onBackground = Color(0xFFF1F8F2),       // Soft Emerald Parchment text
    surface = Color(0xFF0F2113),            // Deep Emerald Cathedral Surface
    onSurface = Color(0xFFF1F8F2),
    surfaceVariant = Color(0xFF17301D),
    onSurfaceVariant = Color(0xFFC8E6C9),   // Muted Meadow Green text
    outline = Color(0xFF2E7D32),
    outlineVariant = Color(0xFF2C5435)      // Spruce Card Border
)

private val LightOrdinaryGreenColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),            // Deep Liturgical Forest Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8ECD9),
    onPrimaryContainer = Color(0xFF0B330F),
    secondary = StaticWarmBronze,           // Olive Bronze
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEBF5EC),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    background = Color(0xFFF4F8F4),         // Soft Meadow Ivory
    onBackground = Color(0xFF111F13),       // Deep Forest Charcoal text
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111F13),
    surfaceVariant = Color(0xFFE4F0E6),     // Pale Willow Card
    onSurfaceVariant = Color(0xFF384F3B),
    outline = Color(0xFF2E7D32),
    outlineVariant = Color(0xFFB5D4BB)
)

/**
 * Returns the customized, personalized ColorScheme for any theme and dark/light preference.
 */
fun getAppColorScheme(themeId: String, isDark: Boolean): ColorScheme {
    return when (themeId) {
        "ROSA_MYSTICA", "SACRED_ROSE" -> if (isDark) DarkRosaMysticaColorScheme else LightRosaMysticaColorScheme
        "PENITENTIAL_VIOLET" -> if (isDark) DarkPenitentialVioletColorScheme else LightPenitentialVioletColorScheme
        "MARIAN_BLUE" -> if (isDark) DarkMarianBlueColorScheme else LightMarianBlueColorScheme
        "MARTYR_CRIMSON" -> if (isDark) DarkMartyrCrimsonColorScheme else LightMartyrCrimsonColorScheme
        "ORDINARY_GREEN" -> if (isDark) DarkOrdinaryGreenColorScheme else LightOrdinaryGreenColorScheme
        "CATHEDRAL_LIGHT" -> if (isDark) DarkCathedralColorScheme else LightCathedralColorScheme
        "CATHEDRAL_DARK", "CATHEDRAL" -> if (isDark) DarkCathedralColorScheme else LightCathedralColorScheme
        else -> if (isDark) DarkCathedralColorScheme else LightCathedralColorScheme
    }
}

@Composable
fun MyApplicationTheme(
    themeId: String = "CATHEDRAL_DARK",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Resolve effective dark/light mode for the given themeId and darkTheme setting
    val effectiveIsDark = when (themeId) {
        "CATHEDRAL_LIGHT" -> darkTheme // Can be viewed in either, defaults according to darkTheme
        "CATHEDRAL_DARK" -> darkTheme
        else -> darkTheme
    }

    val colorScheme = getAppColorScheme(themeId, effectiveIsDark)

    // Dynamically update LiturgicalTheme state based on active theme & personalized mode
    when (themeId) {
        "ROSA_MYSTICA", "SACRED_ROSE" -> {
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = Color(0xFFF09AB1)        // Luminous Mystic Rose
                LiturgicalTheme.goldBright = Color(0xFFA5DCBE)        // Glowing Mint Frost
                LiturgicalTheme.goldLight = Color(0xFFD9C6E2)         // Frosted Lavender Mist
                LiturgicalTheme.goldDark = Color(0xFFD9B875)          // Honey Gold Accent
                LiturgicalTheme.crimsonBurgundy = Color(0xFF4D1C2A)   // Deep Plum Rose
                LiturgicalTheme.crimsonDeep = Color(0xFF3B0B19)       // Deep Rose Velvet
                LiturgicalTheme.crimsonLight = Color(0xFFF09AB1)      // Luminous Mystic Rose
                LiturgicalTheme.darkCathedralBackground = Color(0xFF140B10) // Velvet Mystic Rose-Black
                LiturgicalTheme.darkCathedralSurface = Color(0xFF1F1018)    // Midnight Rose Surface
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFF2C1622) // Velvet Rose-Plum
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFF482736) // Velvet Rose Card Border
                LiturgicalTheme.parchmentLight = Color(0xFFFDF0F3)    // Soft Rose Parchment White
                LiturgicalTheme.parchmentCard = Color(0xFF2C1622)
                LiturgicalTheme.textGoldHighlight = Color(0xFFA5DCBE) // Mint Highlight
                LiturgicalTheme.textMutedParchment = Color(0xFFDECAD2)
            } else {
                LiturgicalTheme.goldSacred = StaticPastelRoseAccent
                LiturgicalTheme.goldBright = StaticPastelMintAccent
                LiturgicalTheme.goldLight = StaticLavenderMist
                LiturgicalTheme.goldDark = StaticPastelGoldAccent
                LiturgicalTheme.crimsonBurgundy = StaticPastelRoseAccent
                LiturgicalTheme.crimsonDeep = StaticSoftRose
                LiturgicalTheme.crimsonLight = StaticPastelRoseSoft
                LiturgicalTheme.darkCathedralBackground = StaticBlushCream
                LiturgicalTheme.darkCathedralSurface = StaticSoftRose
                LiturgicalTheme.darkCathedralSurfaceVariant = StaticMintFrost
                LiturgicalTheme.darkCathedralCardBorder = StaticLavenderMist
                LiturgicalTheme.parchmentLight = StaticDarkTextCharcoal
                LiturgicalTheme.parchmentCard = StaticMintFrost
                LiturgicalTheme.textGoldHighlight = StaticPastelRoseText
                LiturgicalTheme.textMutedParchment = StaticMutedTextRose
            }
        }
        "PENITENTIAL_VIOLET" -> {
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = Color(0xFFCE93D8)
                LiturgicalTheme.goldBright = StaticGoldBright
                LiturgicalTheme.goldLight = Color(0xFFE1BEE7)
                LiturgicalTheme.goldDark = Color(0xFF8E24AA)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF4A148C)
                LiturgicalTheme.crimsonDeep = Color(0xFF2E004F)
                LiturgicalTheme.crimsonLight = Color(0xFFF48FB1)
                LiturgicalTheme.darkCathedralBackground = Color(0xFF0F0B18)
                LiturgicalTheme.darkCathedralSurface = Color(0xFF1B1229)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFF271A3B)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFF4A3266)
                LiturgicalTheme.parchmentLight = Color(0xFFF4EEF8)
                LiturgicalTheme.parchmentCard = Color(0xFF271A3B)
                LiturgicalTheme.textGoldHighlight = Color(0xFFFFE082)
                LiturgicalTheme.textMutedParchment = Color(0xFFD1C4E9)
            } else {
                LiturgicalTheme.goldSacred = Color(0xFF6A1B9A)
                LiturgicalTheme.goldBright = StaticWarmBronze
                LiturgicalTheme.goldLight = Color(0xFF8E24AA)
                LiturgicalTheme.goldDark = Color(0xFF4A148C)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF6A1B9A)
                LiturgicalTheme.crimsonDeep = Color(0xFF38006B)
                LiturgicalTheme.crimsonLight = Color(0xFFAB47BC)
                LiturgicalTheme.darkCathedralBackground = Color(0xFFFAF6FC)
                LiturgicalTheme.darkCathedralSurface = Color(0xFFFFFFFF)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFFF0E5F5)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFFD4BFE0)
                LiturgicalTheme.parchmentLight = Color(0xFF1E1424)
                LiturgicalTheme.parchmentCard = Color(0xFFF0E5F5)
                LiturgicalTheme.textGoldHighlight = Color(0xFF6A1B9A)
                LiturgicalTheme.textMutedParchment = Color(0xFF553D61)
            }
        }
        "MARIAN_BLUE" -> {
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = Color(0xFF64B5F6)
                LiturgicalTheme.goldBright = StaticGoldBright
                LiturgicalTheme.goldLight = Color(0xFF90CAF9)
                LiturgicalTheme.goldDark = Color(0xFF1E88E5)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF0D2D5E)
                LiturgicalTheme.crimsonDeep = Color(0xFF071936)
                LiturgicalTheme.crimsonLight = Color(0xFF64B5F6)
                LiturgicalTheme.darkCathedralBackground = Color(0xFF070D18)
                LiturgicalTheme.darkCathedralSurface = Color(0xFF0F1B2E)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFF182842)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFF2D4B75)
                LiturgicalTheme.parchmentLight = Color(0xFFF0F6FF)
                LiturgicalTheme.parchmentCard = Color(0xFF182842)
                LiturgicalTheme.textGoldHighlight = Color(0xFFFFE082)
                LiturgicalTheme.textMutedParchment = Color(0xFFC2D9F7)
            } else {
                LiturgicalTheme.goldSacred = Color(0xFF0D47A1)
                LiturgicalTheme.goldBright = StaticWarmBronze
                LiturgicalTheme.goldLight = Color(0xFF1565C0)
                LiturgicalTheme.goldDark = Color(0xFF002171)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF0D47A1)
                LiturgicalTheme.crimsonDeep = Color(0xFF052A6E)
                LiturgicalTheme.crimsonLight = Color(0xFF1976D2)
                LiturgicalTheme.darkCathedralBackground = Color(0xFFF4F8FD)
                LiturgicalTheme.darkCathedralSurface = Color(0xFFFFFFFF)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFFE5EFF9)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFFB8D3EE)
                LiturgicalTheme.parchmentLight = Color(0xFF101C2B)
                LiturgicalTheme.parchmentCard = Color(0xFFE5EFF9)
                LiturgicalTheme.textGoldHighlight = Color(0xFF0D47A1)
                LiturgicalTheme.textMutedParchment = Color(0xFF384B61)
            }
        }
        "MARTYR_CRIMSON" -> {
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = Color(0xFFEF5350)
                LiturgicalTheme.goldBright = StaticGoldBright
                LiturgicalTheme.goldLight = Color(0xFFFF8A80)
                LiturgicalTheme.goldDark = StaticCrimsonBurgundy
                LiturgicalTheme.crimsonBurgundy = Color(0xFF5A0818)
                LiturgicalTheme.crimsonDeep = Color(0xFF400008)
                LiturgicalTheme.crimsonLight = Color(0xFFEF5350)
                LiturgicalTheme.darkCathedralBackground = Color(0xFF13080A)
                LiturgicalTheme.darkCathedralSurface = Color(0xFF220E13)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFF32161C)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFF5C262F)
                LiturgicalTheme.parchmentLight = Color(0xFFFFF0F2)
                LiturgicalTheme.parchmentCard = Color(0xFF32161C)
                LiturgicalTheme.textGoldHighlight = Color(0xFFFFE082)
                LiturgicalTheme.textMutedParchment = Color(0xFFFFCDD2)
            } else {
                LiturgicalTheme.goldSacred = Color(0xFF900C1E)
                LiturgicalTheme.goldBright = StaticWarmBronze
                LiturgicalTheme.goldLight = Color(0xFFB71C1C)
                LiturgicalTheme.goldDark = Color(0xFF540410)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF900C1E)
                LiturgicalTheme.crimsonDeep = Color(0xFF540410)
                LiturgicalTheme.crimsonLight = Color(0xFFB71C1C)
                LiturgicalTheme.darkCathedralBackground = Color(0xFFFDF6F6)
                LiturgicalTheme.darkCathedralSurface = Color(0xFFFFFFFF)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFFF9EAEB)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFFE2BFC3)
                LiturgicalTheme.parchmentLight = Color(0xFF261214)
                LiturgicalTheme.parchmentCard = Color(0xFFF9EAEB)
                LiturgicalTheme.textGoldHighlight = Color(0xFF900C1E)
                LiturgicalTheme.textMutedParchment = Color(0xFF5C3B3E)
            }
        }
        "ORDINARY_GREEN" -> {
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = Color(0xFF66BB6A)
                LiturgicalTheme.goldBright = StaticGoldBright
                LiturgicalTheme.goldLight = Color(0xFFA5D6A7)
                LiturgicalTheme.goldDark = Color(0xFF2E7D32)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF144D1E)
                LiturgicalTheme.crimsonDeep = Color(0xFF00330D)
                LiturgicalTheme.crimsonLight = Color(0xFF66BB6A)
                LiturgicalTheme.darkCathedralBackground = Color(0xFF071109)
                LiturgicalTheme.darkCathedralSurface = Color(0xFF0F2113)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFF17301D)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFF2C5435)
                LiturgicalTheme.parchmentLight = Color(0xFFF1F8F2)
                LiturgicalTheme.parchmentCard = Color(0xFF17301D)
                LiturgicalTheme.textGoldHighlight = Color(0xFFFFE082)
                LiturgicalTheme.textMutedParchment = Color(0xFFC8E6C9)
            } else {
                LiturgicalTheme.goldSacred = Color(0xFF1B5E20)
                LiturgicalTheme.goldBright = StaticWarmBronze
                LiturgicalTheme.goldLight = Color(0xFF2E7D32)
                LiturgicalTheme.goldDark = Color(0xFF0B330F)
                LiturgicalTheme.crimsonBurgundy = Color(0xFF1B5E20)
                LiturgicalTheme.crimsonDeep = Color(0xFF0B330F)
                LiturgicalTheme.crimsonLight = Color(0xFF2E7D32)
                LiturgicalTheme.darkCathedralBackground = Color(0xFFF4F8F4)
                LiturgicalTheme.darkCathedralSurface = Color(0xFFFFFFFF)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFFE4F0E6)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFFB5D4BB)
                LiturgicalTheme.parchmentLight = Color(0xFF111F13)
                LiturgicalTheme.parchmentCard = Color(0xFFE4F0E6)
                LiturgicalTheme.textGoldHighlight = Color(0xFF1B5E20)
                LiturgicalTheme.textMutedParchment = Color(0xFF384F3B)
            }
        }
        else -> {
            // Cathedral Gothic (Dark & Light)
            if (effectiveIsDark) {
                LiturgicalTheme.goldSacred = StaticGoldSacred
                LiturgicalTheme.goldBright = StaticGoldBright
                LiturgicalTheme.goldLight = StaticGoldLight
                LiturgicalTheme.goldDark = StaticGoldDark
                LiturgicalTheme.crimsonBurgundy = StaticCrimsonBurgundy
                LiturgicalTheme.crimsonDeep = StaticCrimsonDeep
                LiturgicalTheme.crimsonLight = StaticCrimsonLight
                LiturgicalTheme.darkCathedralBackground = StaticDarkCathedralBackground
                LiturgicalTheme.darkCathedralSurface = StaticDarkCathedralSurface
                LiturgicalTheme.darkCathedralSurfaceVariant = StaticDarkCathedralSurfaceVariant
                LiturgicalTheme.darkCathedralCardBorder = StaticDarkCathedralCardBorder
                LiturgicalTheme.parchmentLight = StaticParchmentLight
                LiturgicalTheme.parchmentCard = StaticDarkCathedralSurfaceVariant
                LiturgicalTheme.textGoldHighlight = StaticTextGoldHighlight
                LiturgicalTheme.textMutedParchment = StaticTextMutedParchment
            } else {
                LiturgicalTheme.goldSacred = StaticWarmBronze
                LiturgicalTheme.goldBright = StaticGoldenBrown
                LiturgicalTheme.goldLight = Color(0xFF9E6B15)
                LiturgicalTheme.goldDark = StaticDeepBrownGold
                LiturgicalTheme.crimsonBurgundy = StaticCrimsonBurgundy
                LiturgicalTheme.crimsonDeep = StaticCrimsonDeep
                LiturgicalTheme.crimsonLight = Color(0xFF9E1B32)
                LiturgicalTheme.darkCathedralBackground = Color(0xFFFAF5EA)
                LiturgicalTheme.darkCathedralSurface = Color(0xFFF4EEE0)
                LiturgicalTheme.darkCathedralSurfaceVariant = Color(0xFFEAE2CE)
                LiturgicalTheme.darkCathedralCardBorder = Color(0xFFD3C6AE)
                LiturgicalTheme.parchmentLight = Color(0xFF241C15)
                LiturgicalTheme.parchmentCard = Color(0xFFEAE2CE)
                LiturgicalTheme.textGoldHighlight = StaticGoldenBrown
                LiturgicalTheme.textMutedParchment = Color(0xFF4A3D30)
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

