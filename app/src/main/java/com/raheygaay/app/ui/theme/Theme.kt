package com.raheygaay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColors = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    secondary = BrandAmber,
    background = BrandLight,
    surface = BrandCardLight,
    surfaceVariant = BrandSurfaceVariantLight,
    onSurface = BrandTextDark,
    onSurfaceVariant = BrandTextDark,
    outline = Color(0xFFE2E8F0),
    surfaceTint = Color.Transparent
)

private val DarkColors = darkColorScheme(
    primary = BrandPurpleDark,
    onPrimary = Color.White,
    secondary = BrandMint,
    background = BrandNavy,
    surface = BrandCardDark,
    surfaceVariant = BrandSurfaceDark,
    onSurface = BrandTextLight,
    onSurfaceVariant = BrandTextLight,
    outline = BrandOutlineDark,
    surfaceTint = Color.Transparent
)

@Composable
fun RaheyGaayTheme(
    isDark: Boolean,
    isArabic: Boolean,
    content: @Composable () -> Unit
) {
    val typography = if (isArabic) buildTypography(CairoFamily) else buildTypography(InterFamily)
    val colors = if (isDark) DarkColors else LightColors
    val direction = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalLayoutDirection provides direction
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            shapes = RaheyGaayShapes,
            content = content
        )
    }
}
