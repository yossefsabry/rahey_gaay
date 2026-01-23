package com.raheygaay.app.ui.screens.profile

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OtherProfileScreen(
    onBack: () -> Unit,
    onContact: () -> Unit,
    viewModel: OtherProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val profile = uiState.value.profile
    if (profile == null) {
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
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OtherProfileHeader(onBack = onBack)
            }
            item {
                OtherProfileCard(profile)
            }
            item {
                NextTripCard(profile)
            }
            item {
                CredentialGrid(profile.credentials)
            }
            item {
                ReviewsSection(profile.reviews)
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        BottomActionBar(onContact = onContact)
    }
}
