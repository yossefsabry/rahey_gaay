package com.raheygaay.app.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raheygaay.app.BuildConfig
import com.raheygaay.app.R
import com.raheygaay.app.data.model.Traveler
import com.raheygaay.app.ui.components.ErrorState
import com.raheygaay.app.ui.components.SkeletonBlock
import com.raheygaay.app.ui.components.SkeletonCircle
import com.raheygaay.app.ui.components.SkeletonTextLine
import com.raheygaay.app.ui.theme.BrandSoftLavender

@Composable
fun HomeScreen(
    onSearch: () -> Unit,
    onSeeAllTravelers: () -> Unit,
    onContactTraveler: (Traveler) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSahby: () -> Unit,
    onOpenDashboard: () -> Unit,
    onToggleDark: () -> Unit,
    onLogout: () -> Unit,
    onOpenAuth: () -> Unit,
    isLoggedIn: Boolean,
    isGuest: Boolean,
    isDark: Boolean,
    showSkeleton: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState.value
    val content = state.content
    val appName = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
        stringResource(R.string.app_name)
    } else {
        BuildConfig.APP_NAME
    }
    val showSkeletonState = showSkeleton || (state.isLoading && content == null)
    if (content == null) {
        if (showSkeletonState) {
            HomeSkeleton()
        } else {
            ErrorState(
                title = stringResource(R.string.error_generic_title),
                message = stringResource(R.string.error_generic_body),
                buttonText = stringResource(R.string.error_retry),
                onRetry = { viewModel.retry() },
                details = if (BuildConfig.DEBUG) state.errorMessage else null
            )
        }
        return
    }
    val listState = rememberLazyListState()
    val background = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    val isSurfaceDark = background.luminance() < 0.4f
    val gradient = remember(isSurfaceDark, background, primary) {
        val accent = if (isSurfaceDark) {
            primary.copy(alpha = 0.08f)
        } else {
            BrandSoftLavender.copy(alpha = 0.3f)
        }
        Brush.verticalGradient(
            colors = listOf(
                background,
                background,
                accent,
                background
            )
        )
    }

    val sectionSpacing = 22.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                HomeHeader(
                    appName = appName,
                    isLoggedIn = isLoggedIn,
                    isGuest = isGuest,
                    onOpenProfile = onOpenProfile,
                    onOpenSearch = onOpenSearch,
                    onOpenSahby = onOpenSahby,
                    onOpenDashboard = onOpenDashboard,
                    onToggleDark = onToggleDark,
                    onLogout = onLogout,
                    onOpenAuth = onOpenAuth,
                    isDark = isDark
                )
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                HeroSection(onSearch = onSearch)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                FeatureSection(features = content.features)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                DiscoverSection(appName = appName, onExplore = onSearch)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                StatsSection(stats = content.stats)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                LiveConnectionsSection(connections = content.liveConnections)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                FeaturedTravelersHeader()
            }
            items(
                items = content.travelers,
                key = { it.id },
                contentType = { "featured_traveler" }
            ) { traveler ->
                FeaturedTravelerItem(
                    traveler = traveler,
                    onContact = onContactTraveler
                )
            }
            item {
                FeaturedTravelersFooter(onSeeAll = onSeeAllTravelers)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                HowItWorksSection(appName = appName, steps = content.steps)
            }
            item {
                Spacer(modifier = Modifier.height(sectionSpacing))
            }
            item {
                WhyChooseSection(appName = appName, reasons = content.reasons)
            }
            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
        if (showSkeleton) {
            HomeSkeleton()
        }
    }
}

@Composable
private fun HomeSkeleton() {
    val background = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    val isSurfaceDark = background.luminance() < 0.4f
    val gradient = remember(isSurfaceDark, background, primary) {
        val accent = if (isSurfaceDark) {
            primary.copy(alpha = 0.08f)
        } else {
            BrandSoftLavender.copy(alpha = 0.3f)
        }
        Brush.verticalGradient(
            colors = listOf(
                background,
                background,
                accent,
                background
            )
        )
    }
    val sectionSpacing = 22.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBlock(
                modifier = Modifier.size(36.dp),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.width(10.dp))
            SkeletonBlock(modifier = Modifier.width(140.dp).height(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            SkeletonCircle(size = 36.dp)
        }
        Spacer(modifier = Modifier.height(sectionSpacing))
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SkeletonTextLine(widthFraction = 0.7f, height = 22.dp)
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonTextLine(widthFraction = 0.45f, height = 22.dp)
            Spacer(modifier = Modifier.height(12.dp))
            SkeletonTextLine(widthFraction = 0.9f, height = 12.dp)
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonTextLine(widthFraction = 0.75f, height = 12.dp)
            Spacer(modifier = Modifier.height(16.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(sectionSpacing))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                SkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(sectionSpacing))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonTextLine(widthFraction = 0.65f, height = 18.dp)
            SkeletonTextLine(widthFraction = 0.9f, height = 12.dp)
            SkeletonBlock(
                modifier = Modifier
                    .width(160.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
