package com.raheygaay.app.ui.screens.map

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import com.raheygaay.app.R
import com.raheygaay.app.data.model.MapTraveler
import com.raheygaay.app.ui.components.AppTextField
import com.raheygaay.app.ui.components.GlassCard
import com.raheygaay.app.ui.components.NetworkImage
import com.raheygaay.app.ui.components.PrimaryButton
import com.raheygaay.app.ui.theme.BrandMint
import com.raheygaay.app.ui.theme.primaryGradientBrush
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapScreen(
    onContact: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val content = uiState.value.content
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                run {
                    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
                    val colors = if (isDark) {
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A), MaterialTheme.colorScheme.background)
                    } else {
                        listOf(Color(0xFFE0F2FE), Color(0xFFF0F9FF), MaterialTheme.colorScheme.background)
                    }
                    Brush.radialGradient(colors = colors)
                }
            )
    ) {
        MapMarkers()
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            Spacer(modifier = Modifier.height(12.dp))
            SearchPanel()
            Spacer(modifier = Modifier.weight(1f))
            FloatingActions()
            Spacer(modifier = Modifier.height(12.dp))
            TravelerBottomSheet(traveler = content.traveler, onContact = onContact)
        }
    }
}

@Composable
private fun SearchPanel() {
    val query = remember { mutableStateOf("") }
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 8.dp,
        contentPadding = 12.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextField(
                value = query.value,
                onValueChange = { query.value = it },
                placeholder = stringResource(R.string.common_search_placeholder),
                leadingIcon = Icons.Outlined.Search,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(primaryGradientBrush()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun FloatingActions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionIcon(icon = Icons.Outlined.MyLocation)
        FloatingActionIcon(icon = Icons.Outlined.Layers)
    }
}

@Composable
private fun FloatingActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .shadow(6.dp, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun MapMarkers() {
    Box(modifier = Modifier.fillMaxSize()) {
        MapMarker(
            name = stringResource(R.string.name_ahmed_short),
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAN1--rX42ZmAXJx4xQ2D6RGAOtIoQ6M00uB-S5zzGNx5ukfMABjExx1jo1OWVH-tN5q8X7YqFv-uqvUfCd1kzalzi8pNEPxv0uxd16-00MMbbvAx4588vghbA1hLoaM9us0gIGj6e7sTVr3a8LWHpIGc-B77CdmiqVBj6x4VpfGnDdbU_Yh17T2EbptM6tAxNhBTPpn-0zc5p7dOxBHpKaLuW6Hd1wdsx8d_AUzp2jokJ1cA5os1HPCw6sTQ8xxXfeBYvWJHpe7YA",
            modifier = Modifier.align(Alignment.Center)
        )
        MapMarker(
            name = stringResource(R.string.name_fatma_hassan),
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDowgytZp4ZOt9kILiGO6Txwj3SO-360c7EREfn2LfFh_0z1ZxLsonbKHN7Jm-nFlme0_hk0VIPgTb9u84AtRxRzphcjBcOEEIfZ33KzV_k81uEsZOfpfdFGRIFN5Bz3ICJsvmZ9nKZ2uYUdu1940aMmeEcpuurMmm_hwdhtlaZHtGRWvFOpo_tJgEDXjltjXa34oSKk0tr5x-kF0JTB7boX9XSfNBnq0a2scdwmRE9RPlCs6gizQKiYSTjtaCjZ7WzAcNwwgg-_d0",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 140.dp, end = 90.dp)
        )
        MapMarker(
            name = stringResource(R.string.name_omar_mostafa),
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAc02jS639Sp0JH9EXXN8TjkCXy7ahFArxG6Y87KjhGuxRGwn5JtA_RAtSTwdS-IZ6jzzD-KcndkfGD4oX-jNqfCjF22qh8Q7FE6909ZVYTnRKmgy0hHJ3-T__cT-CkYfdcjjJKhYJdN0XxZYWOMUOTBSQ7gZ6TjkSgBcvTkGTb0aLlhywCSi7w0mK__jn5MeJRhN-uwRP4N31_flp_Fs6SnC9HH1ry969mgy4TNuHohdc69As5JpzNKMGUcc5gCN6BytWJjKkQ_Dk",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 240.dp, start = 80.dp)
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFF3B82F6))
                .align(Alignment.CenterEnd)
                .padding(end = 90.dp)
        )
    }
}

@Composable
private fun MapMarker(
    name: String,
    imageUrl: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(3.dp)
        ) {
            NetworkImage(
                url = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TravelerBottomSheet(
    traveler: MapTraveler,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    .align(Alignment.CenterHorizontally)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NetworkImage(
                        url = traveler.avatarUrl,
                        contentDescription = stringResource(traveler.nameRes),
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = stringResource(traveler.nameRes), style = MaterialTheme.typography.titleLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.common_age, "28"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = traveler.rating, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.common_trusted),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.common_active_now),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SheetInfoCard(
                    label = stringResource(R.string.common_origin),
                    value = stringResource(R.string.map_origin_value),
                    modifier = Modifier.weight(1f)
                )
                SheetInfoCard(
                    label = stringResource(R.string.common_destination),
                    value = stringResource(R.string.map_destination_value),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandMint.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = null,
                    tint = BrandMint
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.common_traveling_tomorrow),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandMint,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.common_departure_time),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandMint.copy(alpha = 0.8f)
                    )
                }
            }
            Text(
                text = stringResource(traveler.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.common_contact_name, stringResource(traveler.nameRes)),
                    onClick = onContact,
                    leadingIcon = Icons.AutoMirrored.Outlined.Chat,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
