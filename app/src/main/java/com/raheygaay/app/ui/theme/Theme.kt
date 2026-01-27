package com.raheygaay.app.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

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
    val initialColors = if (isDark) DarkColors else LightColors
    var displayedIsDark by remember { mutableStateOf(isDark) }
    var overlayColors by remember { mutableStateOf(initialColors) }
    val waveProgress = remember { Animatable(1f) }
    var overlayVisible by remember { mutableStateOf(false) }
    val colors = if (displayedIsDark) DarkColors else LightColors
    val direction = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    LaunchedEffect(isDark) {
        if (displayedIsDark != isDark) {
            overlayColors = if (displayedIsDark) DarkColors else LightColors
            overlayVisible = true
            waveProgress.snapTo(0f)
            withFrameNanos { }
            displayedIsDark = isDark
            waveProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing)
            )
            overlayVisible = false
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides direction
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            shapes = RaheyGaayShapes
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
                if (overlayVisible) {
                    ThemeWaveOverlay(
                        progress = waveProgress.value,
                        colors = overlayColors,
                        waveHeight = 18.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeWaveOverlay(
    progress: Float,
    colors: androidx.compose.material3.ColorScheme,
    waveHeight: Dp
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val wavePath = remember { Path() }
    val fillPath = remember { Path() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        if (clampedProgress >= 1f) return@Canvas
        val waveHeightPx = waveHeight.toPx()
        val waveY = size.height * clampedProgress
        val segmentWidth = size.width / 4f
        wavePath.reset()
        wavePath.moveTo(0f, waveY)
        var currentX = 0f
        var direction = 1f
        repeat(4) {
            val midX = currentX + segmentWidth / 2f
            val endX = currentX + segmentWidth
            val controlY = waveY + (waveHeightPx * direction)
            wavePath.quadraticBezierTo(midX, controlY, endX, waveY)
            currentX = endX
            direction *= -1f
        }
        fillPath.reset()
        fillPath.addPath(wavePath)
        fillPath.lineTo(size.width, size.height)
        fillPath.lineTo(0f, size.height)
        fillPath.close()
        val brush = Brush.verticalGradient(
            colors = listOf(
                colors.primary.copy(alpha = 0.28f),
                colors.background.copy(alpha = 0.98f),
                colors.background
            ),
            startY = (waveY - waveHeightPx * 2f).coerceAtLeast(0f),
            endY = size.height
        )
        drawPath(path = fillPath, brush = brush)
        drawPath(
            path = wavePath,
            color = colors.primary.copy(alpha = 0.4f),
            style = Stroke(width = waveHeightPx * 0.18f)
        )
    }
}
