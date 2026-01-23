package com.raheygaay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientCard(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    intensity: Float = 0.16f,
    shape: Shape = MaterialTheme.shapes.large,
    shadowElevation: Dp = 6.dp,
    contentPadding: Dp = 16.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    val overlayColors = colors.map { it.copy(alpha = intensity) }
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(overlayColors), shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    shadowElevation: Dp = 6.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val baseColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.92f else 0.85f)
    val overlay = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isDark) 0.08f else 0.15f),
            Color.Transparent
        )
    )

    Surface(
        modifier = modifier,
        shape = shape,
        color = baseColor,
        shadowElevation = shadowElevation,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(overlay, shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        if (actionText != null && onAction != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAction() }
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
