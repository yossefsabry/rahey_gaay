package com.raheygaay.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.raheygaay.app.ui.theme.BrandSoftLavender

@Composable
fun HomeScreen(
    onSearch: () -> Unit,
    onSeeAllTravelers: () -> Unit,
    onContactTraveler: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val content = uiState.value.content
    val appName = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
        stringResource(R.string.app_name)
    } else {
        BuildConfig.APP_NAME
    }
    if (content == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val accent = if (isDark) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        BrandSoftLavender.copy(alpha = 0.3f)
    }
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            accent,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                HomeHeader(appName = appName, onOpenProfile = onOpenProfile)
            }
            item {
                HeroSection(onSearch = onSearch)
            }
            item {
                FeatureSection(features = content.features)
            }
            item {
                DiscoverSection(appName = appName, onExplore = onSearch)
            }
            item {
                StatsSection(stats = content.stats)
            }
            item {
                LiveConnectionsSection(connections = content.liveConnections)
            }
            item {
                FeaturedTravelersSection(
                    travelers = content.travelers,
                    onSeeAll = onSeeAllTravelers,
                    onContact = onContactTraveler
                )
            }
            item {
                HowItWorksSection(appName = appName, steps = content.steps)
            }
            item {
                WhyChooseSection(appName = appName, reasons = content.reasons)
            }
            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}
