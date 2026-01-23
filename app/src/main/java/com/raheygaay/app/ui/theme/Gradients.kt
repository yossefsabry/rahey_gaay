package com.raheygaay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun primaryGradientColors(): List<Color> {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val start = if (isDark) BrandPurpleDark else BrandPurple
    val end = if (isDark) BrandIndigoDark else BrandIndigo
    return listOf(start, end)
}

@Composable
fun mintGradientColors(): List<Color> {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val start = if (isDark) BrandMint.copy(alpha = 0.9f) else BrandMint
    val end = if (isDark) BrandTeal.copy(alpha = 0.9f) else BrandTeal
    return listOf(start, end)
}

@Composable
fun warmGradientColors(): List<Color> {
    val start = BrandAmber
    val end = BrandOrange
    return listOf(start, end)
}

@Composable
fun primaryGradientBrush(): Brush {
    return Brush.linearGradient(primaryGradientColors())
}
