package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Raw Static Color Constants for Palette Definition
val StaticGoldSacred = Color(0xFFD4AF37)
val StaticGoldBright = Color(0xFFFFD700)
val StaticGoldLight = Color(0xFFF9E79F)
val StaticGoldDark = Color(0xFF997A15)

val StaticCrimsonBurgundy = Color(0xFF800020)
val StaticCrimsonDeep = Color(0xFF4A0012)
val StaticCrimsonLight = Color(0xFFB03A2E)

val StaticDarkCathedralBackground = Color(0xFF0E1116)
val StaticDarkCathedralSurface = Color(0xFF181E26)
val StaticDarkCathedralSurfaceVariant = Color(0xFF222A36)
val StaticDarkCathedralCardBorder = Color(0xFF384353)

val StaticParchmentLight = Color(0xFFFBF8EE)
val StaticParchmentCard = Color(0xFFF3EFE0)
val StaticTextGoldHighlight = Color(0xFFFFE082)
val StaticTextMutedParchment = Color(0xFF9EA8B6)

// Image Palette Colors: Sacred Rose & Mint Frost (Exact Hex Values)
val StaticBlushCream = Color(0xFFFDEEF1)   // #FDEEF1
val StaticSoftRose = Color(0xFFF7D9E0)     // #F7D9E0
val StaticMintFrost = Color(0xFFD6E9DC)    // #D6E9DC
val StaticVanillaWhip = Color(0xFFFFF9F4)   // #FFF9F4
val StaticLavenderMist = Color(0xFFC6B7CC)  // #C6B7CC

// Pastel Accent & Legibility Colors for Rosa Mystica (No dark red, green, or purple)
val StaticPastelRoseAccent = Color(0xFFD98A9F)     // Soft Pastel Rose Accent
val StaticPastelRoseSoft = Color(0xFFE8B4C8)       // Soft Pastel Rose Secondary
val StaticPastelRoseText = Color(0xFF6B3D4B)       // Muted Soft Berry-Rose Text
val StaticPastelMintAccent = Color(0xFF9ECBAF)     // Soft Pastel Mint Accent
val StaticPastelMintText = Color(0xFF385E48)       // Muted Soft Sage-Mint Text
val StaticPastelLavenderAccent = Color(0xFFB5A1BD) // Soft Pastel Lavender Accent
val StaticPastelLavenderText = Color(0xFF4A3554)   // Muted Soft Lavender Text
val StaticPastelGoldAccent = Color(0xFFD9B875)     // Soft Pastel Honey Gold Accent

// High-Contrast Companion Colors for Light/Pastel Themes
val StaticDeepRoseBurgundy = Color(0xFF6E2034)
val StaticDeepDarkRose = Color(0xFF42101F)
val StaticDeepMintGreen = Color(0xFF1E4D33)
val StaticDeepLavender = Color(0xFF4A2F5A)
val StaticDeepGoldAccent = Color(0xFF7A580D)
val StaticDarkTextCharcoal = Color(0xFF23161B)
val StaticMutedTextRose = Color(0xFF5E424B)

// Liturgical Season Colors
val LiturgicalViolet = Color(0xFF8E24AA)
val LiturgicalWhiteGold = Color(0xFFFFD700)
val LiturgicalRed = Color(0xFFC62828)
val LiturgicalGreen = Color(0xFF2E7D32)
val LiturgicalRose = Color(0xFFEC407A)
val LiturgicalBlack = Color(0xFF212121)

// Central Reactive Theme State Holder
object LiturgicalTheme {
    var goldSacred by mutableStateOf(StaticGoldSacred)
    var goldBright by mutableStateOf(StaticGoldBright)
    var goldLight by mutableStateOf(StaticGoldLight)
    var goldDark by mutableStateOf(StaticGoldDark)
    var crimsonBurgundy by mutableStateOf(StaticCrimsonBurgundy)
    var crimsonDeep by mutableStateOf(StaticCrimsonDeep)
    var crimsonLight by mutableStateOf(StaticCrimsonLight)
    var darkCathedralBackground by mutableStateOf(StaticDarkCathedralBackground)
    var darkCathedralSurface by mutableStateOf(StaticDarkCathedralSurface)
    var darkCathedralSurfaceVariant by mutableStateOf(StaticDarkCathedralSurfaceVariant)
    var darkCathedralCardBorder by mutableStateOf(StaticDarkCathedralCardBorder)
    var parchmentLight by mutableStateOf(StaticParchmentLight)
    var parchmentCard by mutableStateOf(StaticParchmentCard)
    var textGoldHighlight by mutableStateOf(StaticTextGoldHighlight)
    var textMutedParchment by mutableStateOf(StaticTextMutedParchment)
}

// Dynamic Theme-Aware Getters for Automatic Context Colors
val GoldSacred: Color
    get() = LiturgicalTheme.goldSacred

val GoldBright: Color
    get() = LiturgicalTheme.goldBright

val GoldLight: Color
    get() = LiturgicalTheme.goldLight

val GoldDark: Color
    get() = LiturgicalTheme.goldDark

val CrimsonBurgundy: Color
    get() = LiturgicalTheme.crimsonBurgundy

val CrimsonDeep: Color
    get() = LiturgicalTheme.crimsonDeep

val CrimsonLight: Color
    get() = LiturgicalTheme.crimsonLight

val DarkCathedralBackground: Color
    get() = LiturgicalTheme.darkCathedralBackground

val DarkCathedralSurface: Color
    get() = LiturgicalTheme.darkCathedralSurface

val DarkCathedralSurfaceVariant: Color
    get() = LiturgicalTheme.darkCathedralSurfaceVariant

val DarkCathedralCardBorder: Color
    get() = LiturgicalTheme.darkCathedralCardBorder

val ParchmentLight: Color
    get() = LiturgicalTheme.parchmentLight

val ParchmentCard: Color
    get() = LiturgicalTheme.parchmentCard

val TextGoldHighlight: Color
    get() = LiturgicalTheme.textGoldHighlight

val TextMutedParchment: Color
    get() = LiturgicalTheme.textMutedParchment
