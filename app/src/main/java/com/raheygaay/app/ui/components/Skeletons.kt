package com.raheygaay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.raheygaay.app.ui.navigation.AppRoute

@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = defaultSkeletonColor()
) {
    Box(modifier = modifier.background(color, shape))
}

@Composable
fun SkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    color: Color = defaultSkeletonColor()
) {
    SkeletonBlock(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = color
    )
}

@Composable
fun SkeletonTextLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    color: Color = defaultSkeletonColor()
) {
    SkeletonBlock(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height),
        shape = shape,
        color = color
    )
}

@Composable
private fun defaultSkeletonColor(): Color {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    return if (isLight) {
        scheme.outline.copy(alpha = 0.9f)
    } else {
        scheme.surfaceVariant.copy(alpha = 0.7f)
    }
}

@Composable
fun NavSkeletonOverlay(
    visible: Boolean,
    routeKey: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(140)),
        exit = fadeOut(animationSpec = tween(140))
    ) {
        val overlayColor = MaterialTheme.colorScheme.background
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(overlayColor)
        ) {
            when (routeKey) {
                AppRoute.Home.route -> HomeNavSkeleton()
                AppRoute.Map.route -> MapNavSkeleton()
                AppRoute.Support.route -> SupportNavSkeleton()
                AppRoute.Profile.route -> ProfileNavSkeleton()
                AppRoute.Dashboard.route -> DashboardNavSkeleton()
                AppRoute.OtherProfile.route -> OtherProfileNavSkeleton()
                AppRoute.More.route -> MoreNavSkeleton()
                AppRoute.Settings.route -> SettingsNavSkeleton()
                AppRoute.Search.route -> SearchNavSkeleton()
                AppRoute.Sahby.route -> SahbyNavSkeleton()
                AppRoute.Login.route, AppRoute.Register.route -> AuthNavSkeleton()
                AppRoute.Chat.route -> ChatNavSkeleton()
                AppRoute.Info.route -> InfoNavSkeleton()
                else -> GenericNavSkeleton()
            }
        }
    }
}

@Composable
private fun HomeNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            SkeletonCircle(size = 36.dp)
            Spacer(modifier = Modifier.width(10.dp))
            SkeletonBlock(modifier = Modifier.width(140.dp).height(16.dp))
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(72.dp), shape = RoundedCornerShape(20.dp))
        SkeletonBlock(modifier = Modifier.width(160.dp).height(44.dp), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun MapNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(22.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(220.dp), shape = RoundedCornerShape(24.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(28.dp))
    }
}

@Composable
private fun ProfileNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            SkeletonCircle(size = 36.dp)
            SkeletonBlock(modifier = Modifier.width(100.dp).height(16.dp))
            SkeletonCircle(size = 36.dp)
        }
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            SkeletonCircle(size = 96.dp)
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonTextLine(widthFraction = 0.5f, height = 16.dp)
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(72.dp), shape = RoundedCornerShape(20.dp))
    }
}

@Composable
private fun SupportNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            SkeletonCircle(size = 36.dp)
            SkeletonBlock(modifier = Modifier.width(120.dp).height(16.dp))
            SkeletonCircle(size = 36.dp)
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(14.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(84.dp), shape = RoundedCornerShape(22.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(84.dp), shape = RoundedCornerShape(22.dp))
    }
}

@Composable
private fun DashboardNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonTextLine(widthFraction = 0.5f, height = 18.dp)
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(70.dp), shape = RoundedCornerShape(18.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(170.dp), shape = RoundedCornerShape(22.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(18.dp))
    }
}

@Composable
private fun OtherProfileNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            SkeletonCircle(size = 36.dp)
            SkeletonBlock(modifier = Modifier.width(120.dp).height(16.dp))
            SkeletonCircle(size = 36.dp)
        }
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            SkeletonCircle(size = 96.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonTextLine(widthFraction = 0.5f, height = 16.dp)
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(84.dp), shape = RoundedCornerShape(20.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(70.dp), shape = RoundedCornerShape(18.dp))
    }
}

@Composable
private fun MoreNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp))
        repeat(4) {
            SkeletonBlock(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp))
        }
    }
}

@Composable
private fun SettingsNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.35f).height(16.dp))
        repeat(3) {
            SkeletonBlock(modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(16.dp))
        }
    }
}

@Composable
private fun SearchNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            SkeletonBlock(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(10.dp))
            SkeletonBlock(modifier = Modifier.width(160.dp).height(18.dp))
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(72.dp), shape = RoundedCornerShape(18.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(72.dp), shape = RoundedCornerShape(18.dp))
    }
}

@Composable
private fun SahbyNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            SkeletonBlock(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(10.dp))
            SkeletonBlock(modifier = Modifier.width(140.dp).height(18.dp))
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(18.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.7f).height(48.dp), shape = RoundedCornerShape(16.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.6f).height(48.dp), shape = RoundedCornerShape(16.dp))
    }
}

@Composable
private fun AuthNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(12.dp))
        repeat(2) {
            SkeletonBlock(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(14.dp))
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun ChatNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            SkeletonCircle(size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
            SkeletonBlock(modifier = Modifier.width(140.dp).height(16.dp))
        }
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.7f).height(48.dp), shape = RoundedCornerShape(16.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.75f).height(48.dp), shape = RoundedCornerShape(16.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(22.dp))
    }
}

@Composable
private fun InfoNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonTextLine(widthFraction = 0.6f, height = 20.dp)
        repeat(6) {
            SkeletonTextLine(widthFraction = 0.9f, height = 12.dp)
        }
    }
}

@Composable
private fun GenericNavSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.55f).height(18.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth().height(84.dp), shape = RoundedCornerShape(20.dp))
        SkeletonBlock(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
    }
}
