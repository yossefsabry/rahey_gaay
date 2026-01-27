package com.raheygaay.app.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import com.raheygaay.app.R
import com.raheygaay.app.ui.theme.BrandAmber
import com.raheygaay.app.ui.theme.BrandMint
import com.raheygaay.app.ui.theme.BrandOrange
import com.raheygaay.app.ui.theme.BrandTeal

@Composable
fun MoreScreen(
    isDark: Boolean,
    isArabic: Boolean,
    isLoggedIn: Boolean,
    onToggleDark: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenInfo: (InfoPage) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val pages = infoPages()

    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.more_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.more_dark_mode),
                icon = Icons.Outlined.DarkMode,
                checked = isDark,
                onToggle = onToggleDark
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.more_arabic_language),
                icon = Icons.Outlined.Language,
                checked = isArabic,
                onToggle = onToggleLanguage
            )
        }
        if (isLoggedIn) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDashboard() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.menu_dashboard),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(imageVector = Icons.AutoMirrored.Outlined.NavigateNext, contentDescription = null)
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLogout() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.more_logout),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(imageVector = Icons.AutoMirrored.Outlined.NavigateNext, contentDescription = null)
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAuth() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.more_login_register),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(imageVector = Icons.AutoMirrored.Outlined.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
        item {
            CompanySection(pages = pages, onOpenInfo = onOpenInfo)
        }
        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            Switch(checked = checked, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun CompanySection(pages: List<InfoPage>, onOpenInfo: (InfoPage) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.more_company_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column {
                pages.forEachIndexed { index, page ->
                    CompanyRow(page = page, onClick = { onOpenInfo(page) })
                    if (index < pages.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 64.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyRow(page: InfoPage, onClick: () -> Unit) {
    val accent = infoAccent(page.id)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accent.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = infoIcon(page.id),
                    contentDescription = null,
                    tint = accent
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(page.titleRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(page.subtitleRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun infoIcon(id: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (id) {
        "company" -> Icons.Outlined.Business
        "about" -> Icons.Outlined.Info
        "careers" -> Icons.Outlined.WorkOutline
        "press" -> Icons.Outlined.Public
        "blog" -> Icons.AutoMirrored.Outlined.Article
        "support" -> Icons.Outlined.SupportAgent
        "help-center" -> Icons.AutoMirrored.Outlined.HelpOutline
        "safety" -> Icons.Outlined.HealthAndSafety
        "community" -> Icons.Outlined.Groups
        "contact" -> Icons.Outlined.MailOutline
        "terms" -> Icons.Outlined.Policy
        "privacy" -> Icons.Outlined.Lock
        "map" -> Icons.Outlined.Map
        "profile" -> Icons.Outlined.PersonOutline
        else -> Icons.Outlined.Info
    }
}

@Composable
private fun infoAccent(id: String): androidx.compose.ui.graphics.Color {
    return when (id) {
        "company" -> MaterialTheme.colorScheme.primary
        "about" -> BrandTeal
        "careers" -> BrandMint
        "press" -> BrandAmber
        "blog" -> BrandOrange
        "support" -> MaterialTheme.colorScheme.secondary
        "help-center" -> BrandTeal
        "safety" -> BrandMint
        "community" -> MaterialTheme.colorScheme.primary
        "contact" -> BrandAmber
        "terms" -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        "privacy" -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        "map" -> BrandTeal
        "profile" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }
}

data class InfoPage(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int
)

fun infoPages(): List<InfoPage> {
    return listOf(
        InfoPage("company", R.string.info_company_title, R.string.info_company_subtitle),
        InfoPage("about", R.string.info_about_title, R.string.info_about_subtitle),
        InfoPage("careers", R.string.info_careers_title, R.string.info_careers_subtitle),
        InfoPage("press", R.string.info_press_title, R.string.info_press_subtitle),
        InfoPage("blog", R.string.info_blog_title, R.string.info_blog_subtitle),
        InfoPage("support", R.string.info_support_title, R.string.info_support_subtitle),
        InfoPage("help-center", R.string.info_help_center_title, R.string.info_help_center_subtitle),
        InfoPage("safety", R.string.info_safety_title, R.string.info_safety_subtitle),
        InfoPage("community", R.string.info_community_title, R.string.info_community_subtitle),
        InfoPage("contact", R.string.info_contact_title, R.string.info_contact_subtitle),
        InfoPage("terms", R.string.info_terms_title, R.string.info_terms_subtitle),
        InfoPage("privacy", R.string.info_privacy_title, R.string.info_privacy_subtitle),
        InfoPage("map", R.string.info_map_title, R.string.info_map_subtitle),
        InfoPage("profile", R.string.info_profile_title, R.string.info_profile_subtitle)
    )
}
