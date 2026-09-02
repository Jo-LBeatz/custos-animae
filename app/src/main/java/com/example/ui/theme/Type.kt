package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp

// Traditional Gothic & Sacred Catholic Font Family
val GothicFontFamily = FontFamily.Serif

// Global typography tailored for traditional Catholic prayer books & missals
val Typography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.5.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    displayMedium =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.4.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    displaySmall =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.3.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    headlineLarge =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    headlineMedium =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    headlineSmall =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    titleLarge =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    titleMedium =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    titleSmall =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    bodyLarge =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.3.sp,
        lineBreak = LineBreak.Paragraph,
        hyphens = Hyphens.None
      ),
    bodyMedium =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Paragraph,
        hyphens = Hyphens.None
      ),
    bodySmall =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
        lineBreak = LineBreak.Paragraph,
        hyphens = Hyphens.None
      ),
    labelLarge =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    labelMedium =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      ),
    labelSmall =
      TextStyle(
        fontFamily = GothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Heading,
        hyphens = Hyphens.None
      )
  )


