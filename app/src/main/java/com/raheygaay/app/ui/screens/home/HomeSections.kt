package com.raheygaay.app.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.raheygaay.app.R
import com.raheygaay.app.data.model.HomeFeature
import com.raheygaay.app.data.model.HomeFeatureType
import com.raheygaay.app.data.model.HomeStat
import com.raheygaay.app.data.model.HomeStatType
import com.raheygaay.app.data.model.HowItWorksStep
import com.raheygaay.app.data.model.HowItWorksStepType
import com.raheygaay.app.data.model.LiveConnection
import com.raheygaay.app.data.model.LiveConnectionTone
import com.raheygaay.app.data.model.Traveler
import com.raheygaay.app.data.model.WhyChooseReason
import com.raheygaay.app.data.model.WhyChooseReasonType
import com.raheygaay.app.ui.components.BrandLogo
import com.raheygaay.app.ui.components.NetworkImage
import com.raheygaay.app.ui.components.PrimaryButton
import com.raheygaay.app.ui.theme.BrandMint

@Composable
internal fun HomeHeader(
    appName: String,
    isLoggedIn: Boolean,
    isGuest: Boolean,
    onOpenProfile: () -> Unit,
    onOpenDashboard: () -> Unit,
    onToggleDark: () -> Unit,
    onLogout: () -> Unit,
    onOpenAuth: () -> Unit,
    isDark: Boolean
) {
    val menuExpanded = remember { mutableStateOf(false) }
    val handleDarkToggle = {
        menuExpanded.value = false
        onToggleDark()
    }
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    BrandLogo(modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                if (isGuest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.guest_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (isLoggedIn) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .clickable { menuExpanded.value = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.menu_profile)) },
                            leadingIcon = { Icon(imageVector = Icons.Outlined.PersonOutline, contentDescription = null) },
                            onClick = {
                                menuExpanded.value = false
                                onOpenProfile()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.menu_dashboard)) },
                            leadingIcon = { Icon(imageVector = Icons.Outlined.Dashboard, contentDescription = null) },
                            onClick = {
                                menuExpanded.value = false
                                onOpenDashboard()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.menu_dark_mode)) },
                            leadingIcon = { Icon(imageVector = Icons.Outlined.DarkMode, contentDescription = null) },
                            trailingIcon = {
                                Switch(checked = isDark, onCheckedChange = { handleDarkToggle() })
                            },
                            onClick = { handleDarkToggle() }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.menu_logout)) },
                            leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                            onClick = {
                                menuExpanded.value = false
                                onLogout()
                            }
                        )
                    }
                }
            } else {
                Button(
                    onClick = onOpenAuth,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_sign_in_up),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun HeroSection(onSearch: () -> Unit) {
    val query = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_tagline_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.home_tagline_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        SearchBar(
            value = query.value,
            onValueChange = { query.value = it },
            onSearch = onSearch
        )
    }
}

@Composable
internal fun FeatureSection(features: List<HomeFeature>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 4.dp,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = featureIcon(feature.type),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(featureTitleRes(feature.type)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(featureSubtitleRes(feature.type)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DiscoverSection(appName: String, onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.home_discover_title, appName),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.home_discover_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        PrimaryButton(
            text = stringResource(R.string.home_discover_cta),
            onClick = onExplore,
            modifier = Modifier.height(48.dp)
        )
    }
}

@Composable
internal fun StatsSection(stats: List<HomeStat>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            val highlight = stat.type == HomeStatType.DELIVERIES_TODAY
            StatCard(
                title = stringResource(statTitleRes(stat.type)),
                value = stat.value,
                icon = statIcon(stat.type),
                highlight = highlight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val contentColor = if (highlight) Color.White else MaterialTheme.colorScheme.onSurface
    val surfaceColor = if (highlight) BrandMint else MaterialTheme.colorScheme.surface
    val borderColor = if (highlight) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val shadow = if (highlight) 10.dp else 4.dp

    Surface(
        modifier = modifier,
        color = surfaceColor,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = shadow,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.dp, color = borderColor, shape = MaterialTheme.shapes.extraLarge)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.18f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomStart)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
internal fun LiveConnectionsSection(connections: List<LiveConnection>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_live_connections),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandMint)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandMint.copy(alpha = 0.5f))
                    )
                }
            }
            connections.forEach { connection ->
                ProgressBar(
                    color = connectionColor(connection.tone),
                    progress = connection.progress
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_live_connections_footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = stringResource(R.string.home_live_connections_live),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(color: Color, progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color)
        )
    }
}

@Composable
internal fun FeaturedTravelersSection(
    travelers: List<Traveler>,
    onSeeAll: () -> Unit,
    onContact: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(vertical = 20.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.home_featured_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_featured_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            travelers.forEach { traveler ->
                TravelerCard(traveler = traveler, onContact = onContact)
            }
            Text(
                text = stringResource(R.string.home_featured_view_all),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onSeeAll() }
            )
        }
    }
}

@Composable
private fun TravelerCard(
    traveler: Traveler,
    onContact: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NetworkImage(
                    url = traveler.avatarUrl,
                    contentDescription = stringResource(traveler.nameRes),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(traveler.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.common_age, traveler.age),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = traveler.rating,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.home_rating_count, traveler.ratingCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                if (traveler.isVerified) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_verified),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.large,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.NearMe,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(traveler.routeRes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = stringResource(traveler.noteRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            PrimaryButton(
                text = stringResource(R.string.common_contact_name, stringResource(traveler.nameRes)),
                onClick = onContact,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun HowItWorksSection(appName: String, steps: List<HowItWorksStep>) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val panelColor = if (isDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val glow = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            Color.Transparent
        )
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = panelColor,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.background(glow)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_how_it_works_title, appName),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.home_how_it_works_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    steps.forEachIndexed { index, step ->
                        StepCard(step = step, isLast = index == steps.lastIndex)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(step: HowItWorksStep, isLast: Boolean) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val cardColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Surface(
        color = cardColor,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stepIcon(step.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset((-6).dp, 4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.stepNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (!isLast) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(26.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(step.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(step.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
internal fun WhyChooseSection(appName: String, reasons: List<WhyChooseReason>) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val baseSurface = if (isDark) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
    val glow = Brush.radialGradient(
        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), Color.Transparent),
        radius = 520f
    )
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Surface(
            color = baseSurface,
            shape = MaterialTheme.shapes.extraLarge,
            shadowElevation = 6.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.background(glow)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_why_choose_title, appName),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reasons.forEach { reason ->
                            ReasonCard(reason = reason)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReasonCard(reason: WhyChooseReason) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f
    val cardColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Surface(
        color = cardColor,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = reasonIcon(reason.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(reason.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(reason.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
internal fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 6.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.common_search_placeholder),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSearch,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.height(36.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
            ) {
                Text(text = stringResource(R.string.common_search), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@StringRes
private fun featureTitleRes(type: HomeFeatureType): Int = when (type) {
    HomeFeatureType.VERIFIED -> R.string.home_feature_verified_title
    HomeFeatureType.RATED -> R.string.home_feature_rated_title
    HomeFeatureType.FAST -> R.string.home_feature_fast_title
}

@StringRes
private fun featureSubtitleRes(type: HomeFeatureType): Int = when (type) {
    HomeFeatureType.VERIFIED -> R.string.home_feature_verified_subtitle
    HomeFeatureType.RATED -> R.string.home_feature_rated_subtitle
    HomeFeatureType.FAST -> R.string.home_feature_fast_subtitle
}

private fun featureIcon(type: HomeFeatureType) = when (type) {
    HomeFeatureType.VERIFIED -> Icons.Outlined.VerifiedUser
    HomeFeatureType.RATED -> Icons.Outlined.Star
    HomeFeatureType.FAST -> Icons.Outlined.Speed
}

@StringRes
private fun statTitleRes(type: HomeStatType): Int = when (type) {
    HomeStatType.ACTIVE_USERS -> R.string.home_stats_active_users
    HomeStatType.DELIVERIES_TODAY -> R.string.home_stats_deliveries_today
}

private fun statIcon(type: HomeStatType) = when (type) {
    HomeStatType.ACTIVE_USERS -> Icons.Outlined.Groups
    HomeStatType.DELIVERIES_TODAY -> Icons.Outlined.LocalShipping
}

@Composable
private fun connectionColor(tone: LiveConnectionTone): Color = when (tone) {
    LiveConnectionTone.MINT -> BrandMint
    LiveConnectionTone.AMBER -> Color(0xFFF59E0B)
    LiveConnectionTone.PRIMARY -> MaterialTheme.colorScheme.primary
}

private fun stepIcon(type: HowItWorksStepType) = when (type) {
    HowItWorksStepType.SEARCH -> Icons.Outlined.Search
    HowItWorksStepType.CONNECT -> Icons.Outlined.ChatBubbleOutline
    HowItWorksStepType.DELIVER -> Icons.Outlined.LocalShipping
}

private fun reasonIcon(type: WhyChooseReasonType) = when (type) {
    WhyChooseReasonType.TRUSTED -> Icons.Outlined.Groups
    WhyChooseReasonType.SAVINGS -> Icons.Outlined.Payments
    WhyChooseReasonType.SUPPORT -> Icons.Outlined.SupportAgent
}
