package com.raheygaay.app.ui.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raheygaay.app.BuildConfig
import com.raheygaay.app.R
import com.raheygaay.app.ui.components.ErrorState
import com.raheygaay.app.ui.components.SkeletonBlock
import com.raheygaay.app.ui.components.SkeletonCircle
import com.raheygaay.app.ui.components.SkeletonTextLine

@Composable
fun OtherProfileScreen(
    onBack: () -> Unit,
    onContact: () -> Unit,
    showSkeleton: Boolean = false,
    viewModel: OtherProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState.value
    val profile = state.profile
    val showSkeletonState = showSkeleton || (state.isLoading && profile == null)
    if (profile == null) {
        if (showSkeletonState) {
            OtherProfileSkeleton()
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
    val visibility = profile.visibility
    val canShowDetails = visibility.isProfileVisible
    val showContactInfo = visibility.showPhone || visibility.showEmail || visibility.showOnlineStatus || visibility.showAddress
    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OtherProfileHeader(onBack = onBack)
            }
            item {
                OtherProfileCard(
                    profile = profile,
                    showRatings = canShowDetails && visibility.showRatings,
                    showTrips = canShowDetails && visibility.showTrips
                )
            }
            if (!canShowDetails) {
                item {
                    PrivateProfileCard()
                }
            } else {
                if (showContactInfo) {
                    item {
                        ContactInfoCard(profile = profile)
                    }
                }
                if (visibility.showTrips) {
                    item {
                        NextTripCard(
                            profile = profile,
                            showOnlineStatus = visibility.showOnlineStatus,
                            showAddress = visibility.showAddress
                        )
                    }
                }
                if (profile.credentials.isNotEmpty()) {
                    item {
                        CredentialGrid(profile.credentials)
                    }
                }
                if (visibility.showRatings && profile.reviews.isNotEmpty()) {
                    item {
                        ReviewsHeader()
                    }
                    items(
                        items = profile.reviews,
                        key = { "${it.name}_${it.timeRes}" },
                        contentType = { "review" }
                    ) { review ->
                        ReviewCard(review)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        BottomActionBar(
            onContact = onContact,
            contactName = stringResource(profile.nameRes),
            enabled = canShowDetails
        )
        if (showSkeleton) {
            OtherProfileSkeleton()
        }
    }
}

@Composable
private fun OtherProfileSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonCircle(size = 36.dp)
            SkeletonBlock(modifier = Modifier.width(120.dp).height(16.dp))
            SkeletonCircle(size = 36.dp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SkeletonCircle(size = 96.dp)
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonTextLine(widthFraction = 0.5f, height = 16.dp)
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonTextLine(widthFraction = 0.7f, height = 12.dp)
        }
        repeat(2) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(84.dp),
                shape = RoundedCornerShape(20.dp)
            )
        }
        repeat(2) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
