package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

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
    primary = StaticCrimsonBurgundy,
    onPrimary = Color.White,
    primaryContainer = StaticParchmentCard,
    onPrimaryContainer = StaticCrimsonDeep,
    secondary = StaticGoldDark,
    onSecondary = Color.White,
    secondaryContainer = StaticParchmentLight,
    onSecondaryContainer = StaticCrimsonBurgundy,
    tertiary = StaticGoldSacred,
    onTertiary = Color.Black,
    background = StaticParchmentLight,
    onBackground = Color(0xFF1C1917),
    surface = Color.White,
    onSurface = Color(0xFF1C1917),
    surfaceVariant = StaticParchmentCard,
    onSurfaceVariant = Color(0xFF44403C),
    outline = StaticGoldSacred,
    outlineVariant = StaticDarkCathedralCardBorder
)

private val PenitentialVioletColorScheme = darkColorScheme(
    primary = Color(0xFFAB47BC), // Penitential Violet Light
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4A148C), // Deep Violet
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldSacred,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF231C33), // Dark Violet Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFEC407A), // Rose Violet
    onTertiary = Color.White,
    background = Color(0xFF0F0B18), // Very dark violet-black
    onBackground = StaticParchmentLight,
    surface = Color(0xFF191326), // Deep Violet Cathedral Surface
    onSurface = StaticParchmentLight,
    surfaceVariant = Color(0xFF231C33),
    onSurfaceVariant = Color(0xFFB39DDB), // Light Lavender text
    outline = Color(0xFF8E24AA),
    outlineVariant = Color(0xFF3F2B5C) // Violet Card Border
)

private val MarianBlueColorScheme = darkColorScheme(
    primary = Color(0xFF42A5F5), // Light Marian Blue
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0D1B3E), // Deep Starry Blue
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldSacred,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A233A), // Dark Blue Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFBBDEFB),
    onTertiary = Color.Black,
    background = Color(0xFF080C16), // Space Blue/Night
    onBackground = StaticParchmentLight,
    surface = Color(0xFF111726), // Starry Cathedral Surface
    onSurface = StaticParchmentLight,
    surfaceVariant = Color(0xFF1A233A),
    onSurfaceVariant = Color(0xFF90CAF9), // Muted Blue text
    outline = Color(0xFF1976D2),
    outlineVariant = Color(0xFF28375A) // Blue Card Border
)

private val MartyrCrimsonColorScheme = darkColorScheme(
    primary = Color(0xFFE53935), // Ruby Red
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4A0012), // CrimsonDeep
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldSacred,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2B191D), // Dark Red Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFFFA000),
    onTertiary = Color.Black,
    background = Color(0xFF13090B), // Charcoal Red-Black
    onBackground = StaticParchmentLight,
    surface = Color(0xFF1F1114), // Crimson Cathedral Surface
    onSurface = StaticParchmentLight,
    surfaceVariant = Color(0xFF2B191D),
    onSurfaceVariant = Color(0xFFEF9A9A), // Muted Red text
    outline = StaticCrimsonBurgundy,
    outlineVariant = Color(0xFF442227) // Crimson Card Border
)

private val OrdinaryGreenColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50), // Liturgical Green Light
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B5E20), // Deep Forest Green
    onPrimaryContainer = StaticGoldBright,
    secondary = StaticGoldSacred,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1B2C1E), // Dark Green Accent
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFF81C784),
    onTertiary = Color.Black,
    background = Color(0xFF09120B), // Spruce Dark Green-Black
    onBackground = StaticParchmentLight,
    surface = Color(0xFF121F15), // Forest Cathedral Surface
    onSurface = StaticParchmentLight,
    surfaceVariant = Color(0xFF1B2C1E),
    onSurfaceVariant = Color(0xFFA5D6A7), // Muted Green text
    outline = Color(0xFF2E7D32),
    outlineVariant = Color(0xFF2A3F2E) // Forest Card Border
)

// Traditional Catholic "Rosa Mystica" Rose & Mint Pure Pastel Color Scheme (From Image Palette)
private val RosaMysticaColorScheme = lightColorScheme(
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
    outlineVariant = Color(0xFFE8CAD4)        // Soft Rose Accent Border
)

@Composable
fun MyApplicationTheme(
    themeId: String = "CATHEDRAL_DARK",
    darkTheme: Boolean = true, // Default if not specific themeId
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeId) {
        "CATHEDRAL_DARK" -> DarkCathedralColorScheme
        "CATHEDRAL_LIGHT" -> LightCathedralColorScheme
        "ROSA_MYSTICA", "SACRED_ROSE" -> RosaMysticaColorScheme
        "PENITENTIAL_VIOLET" -> PenitentialVioletColorScheme
        "MARIAN_BLUE" -> MarianBlueColorScheme
        "MARTYR_CRIMSON" -> MartyrCrimsonColorScheme
        "ORDINARY_GREEN" -> OrdinaryGreenColorScheme
        else -> if (darkTheme) DarkCathedralColorScheme else LightCathedralColorScheme
    }

    val isLight = colorScheme.background.luminance() > 0.5f
    val isRosaMystica = themeId == "ROSA_MYSTICA" || themeId == "SACRED_ROSE"

    if (isRosaMystica) {
        // Pure Pastel theme mapping for Rosa Mystica (No dark red, green, or purple accents)
        LiturgicalTheme.goldSacred = StaticPastelRoseAccent        // Soft Pastel Rose (#D98A9F)
        LiturgicalTheme.goldBright = StaticPastelMintAccent        // Soft Pastel Mint (#9ECBAF)
        LiturgicalTheme.goldLight = StaticLavenderMist             // Soft Pastel Lavender (#C6B7CC)
        LiturgicalTheme.goldDark = StaticPastelGoldAccent          // Soft Pastel Honey Gold (#D9B875)
        LiturgicalTheme.crimsonBurgundy = StaticPastelRoseAccent   // Soft Pastel Rose (#D98A9F)
        LiturgicalTheme.crimsonDeep = StaticSoftRose               // Soft Rose (#F7D9E0)
        LiturgicalTheme.crimsonLight = StaticPastelRoseSoft        // Soft Pastel Rose (#E8B4C8)
        LiturgicalTheme.darkCathedralBackground = colorScheme.background // Blush Cream (#FDEEF1)
        LiturgicalTheme.darkCathedralSurface = colorScheme.surface       // Soft Rose (#F7D9E0)
        LiturgicalTheme.darkCathedralSurfaceVariant = StaticMintFrost    // Mint Frost (#D6E9DC)
        LiturgicalTheme.darkCathedralCardBorder = colorScheme.outline     // Lavender Mist (#C6B7CC)
        LiturgicalTheme.parchmentLight = colorScheme.onBackground        // Soft Dark Text (#23161B)
        LiturgicalTheme.parchmentCard = colorScheme.secondaryContainer   // Mint Frost (#D6E9DC)
        LiturgicalTheme.textGoldHighlight = StaticPastelRoseText         // Soft Berry-Rose Text (#6B3D4B)
        LiturgicalTheme.textMutedParchment = colorScheme.onSurfaceVariant// Muted Rose Text (#5E424B)
    } else {
        // Update the LiturgicalTheme reactive properties dynamically for other themes
        LiturgicalTheme.goldSacred = if (isLight) StaticDeepRoseBurgundy else colorScheme.primary
        LiturgicalTheme.goldBright = if (isLight) StaticDeepGoldAccent else colorScheme.secondary
        LiturgicalTheme.goldLight = if (isLight) StaticDeepLavender else colorScheme.tertiary
        LiturgicalTheme.goldDark = if (isLight) StaticDeepGoldAccent else colorScheme.outline
        LiturgicalTheme.crimsonBurgundy = if (isLight) StaticDeepRoseBurgundy else colorScheme.primaryContainer
        LiturgicalTheme.crimsonDeep = if (isLight) StaticSoftRose else colorScheme.primaryContainer
        LiturgicalTheme.crimsonLight = if (isLight) StaticDeepRoseBurgundy else colorScheme.tertiary
        LiturgicalTheme.darkCathedralBackground = colorScheme.background
        LiturgicalTheme.darkCathedralSurface = colorScheme.surface
        LiturgicalTheme.darkCathedralSurfaceVariant = colorScheme.surfaceVariant
        LiturgicalTheme.darkCathedralCardBorder = colorScheme.outline
        LiturgicalTheme.parchmentLight = colorScheme.onBackground
        LiturgicalTheme.parchmentCard = colorScheme.secondaryContainer
        LiturgicalTheme.textGoldHighlight = if (isLight) StaticDeepRoseBurgundy else colorScheme.onSecondaryContainer
        LiturgicalTheme.textMutedParchment = colorScheme.onSurfaceVariant
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
