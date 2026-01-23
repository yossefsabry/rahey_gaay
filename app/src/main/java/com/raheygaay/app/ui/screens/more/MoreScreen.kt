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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun MoreScreen(
    isDark: Boolean,
    isArabic: Boolean,
    onToggleDark: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenInfo: (InfoPage) -> Unit,
    onOpenAuth: () -> Unit
) {
    val pages = infoPages()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
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
        item {
            Text(
                text = stringResource(R.string.more_company_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(pages) { page ->
            InfoRow(page = page, onClick = { onOpenInfo(page) })
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
private fun InfoRow(page: InfoPage, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Icon(imageVector = Icons.AutoMirrored.Outlined.NavigateNext, contentDescription = null)
        }
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
