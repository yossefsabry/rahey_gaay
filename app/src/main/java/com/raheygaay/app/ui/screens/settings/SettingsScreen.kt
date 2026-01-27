package com.raheygaay.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raheygaay.app.R
import com.raheygaay.app.ui.components.AppTextField
import com.raheygaay.app.ui.components.PrimaryButton
import com.raheygaay.app.ui.screens.profile.ProfileViewModel

@Composable
fun SettingsScreen(
    isDark: Boolean,
    isArabic: Boolean,
    onBack: () -> Unit,
    onToggleDark: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSave: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.value.profile
    val defaultName = profile?.let { stringResource(it.nameRes) }
        ?: stringResource(R.string.settings_default_name)
    val defaultEmail = stringResource(R.string.settings_default_email)
    val defaultPhone = stringResource(R.string.settings_default_phone)
    val defaultCity = stringResource(R.string.settings_default_city)
    val defaultBio = stringResource(R.string.settings_default_bio)

    val nameState = rememberSaveable { mutableStateOf("") }
    val emailState = rememberSaveable { mutableStateOf("") }
    val phoneState = rememberSaveable { mutableStateOf("") }
    val cityState = rememberSaveable { mutableStateOf("") }
    val bioState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }
    val profileVisible = rememberSaveable { mutableStateOf(true) }
    val notificationsEnabled = rememberSaveable { mutableStateOf(true) }
    val showPhone = rememberSaveable { mutableStateOf(true) }
    val showEmail = rememberSaveable { mutableStateOf(true) }
    val showOnlineStatus = rememberSaveable { mutableStateOf(true) }
    val showAddress = rememberSaveable { mutableStateOf(false) }
    val showRatings = rememberSaveable { mutableStateOf(true) }
    val showTrips = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(defaultName, defaultEmail, defaultPhone, defaultCity, defaultBio) {
        if (nameState.value.isBlank()) nameState.value = defaultName
        if (emailState.value.isBlank()) emailState.value = defaultEmail
        if (phoneState.value.isBlank()) phoneState.value = defaultPhone
        if (cityState.value.isBlank()) cityState.value = defaultCity
        if (bioState.value.isBlank()) bioState.value = defaultBio
    }

    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SettingsHeader(onBack = onBack)
        }
        item {
            SectionCard(title = stringResource(R.string.settings_section_personal)) {
                SettingsField(
                    label = stringResource(R.string.settings_label_name),
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_name),
                    icon = Icons.Outlined.Person
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsField(
                    label = stringResource(R.string.settings_label_email),
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_email),
                    icon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsField(
                    label = stringResource(R.string.settings_label_phone),
                    value = phoneState.value,
                    onValueChange = { phoneState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_phone),
                    icon = Icons.Outlined.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsField(
                    label = stringResource(R.string.settings_label_city),
                    value = cityState.value,
                    onValueChange = { cityState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_city),
                    icon = Icons.Outlined.LocationOn
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsField(
                    label = stringResource(R.string.settings_label_bio),
                    value = bioState.value,
                    onValueChange = { bioState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_bio),
                    icon = Icons.Outlined.Badge
                )
            }
        }
        item {
            SectionCard(title = stringResource(R.string.settings_section_security)) {
                SettingsField(
                    label = stringResource(R.string.settings_label_password),
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    placeholder = stringResource(R.string.settings_placeholder_password),
                    icon = Icons.Outlined.Visibility,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        }
        item {
            SectionCard(title = stringResource(R.string.settings_section_preferences)) {
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_dark_mode),
                    subtitle = stringResource(R.string.settings_pref_dark_mode_desc),
                    icon = Icons.Outlined.DarkMode,
                    checked = isDark,
                    onToggle = onToggleDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_language),
                    subtitle = stringResource(R.string.settings_pref_language_desc),
                    icon = Icons.Outlined.Language,
                    checked = isArabic,
                    onToggle = onToggleLanguage
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_notifications),
                    subtitle = stringResource(R.string.settings_pref_notifications_desc),
                    icon = Icons.Outlined.Notifications,
                    checked = notificationsEnabled.value,
                    onToggle = { notificationsEnabled.value = !notificationsEnabled.value }
                )
            }
        }
        item {
            SectionCard(title = stringResource(R.string.settings_section_visibility)) {
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_visibility),
                    subtitle = stringResource(R.string.settings_pref_visibility_desc),
                    icon = Icons.Outlined.Person,
                    checked = profileVisible.value,
                    onToggle = { profileVisible.value = !profileVisible.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_show_phone),
                    subtitle = stringResource(R.string.settings_pref_show_phone_desc),
                    icon = Icons.Outlined.Phone,
                    checked = showPhone.value,
                    onToggle = { showPhone.value = !showPhone.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_show_email),
                    subtitle = stringResource(R.string.settings_pref_show_email_desc),
                    icon = Icons.Outlined.Email,
                    checked = showEmail.value,
                    onToggle = { showEmail.value = !showEmail.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_online_status),
                    subtitle = stringResource(R.string.settings_pref_online_status_desc),
                    icon = Icons.Outlined.Wifi,
                    checked = showOnlineStatus.value,
                    onToggle = { showOnlineStatus.value = !showOnlineStatus.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_show_address),
                    subtitle = stringResource(R.string.settings_pref_show_address_desc),
                    icon = Icons.Outlined.LocationOn,
                    checked = showAddress.value,
                    onToggle = { showAddress.value = !showAddress.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_show_ratings),
                    subtitle = stringResource(R.string.settings_pref_show_ratings_desc),
                    icon = Icons.Outlined.Star,
                    checked = showRatings.value,
                    onToggle = { showRatings.value = !showRatings.value }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PreferenceToggle(
                    title = stringResource(R.string.settings_pref_show_trips),
                    subtitle = stringResource(R.string.settings_pref_show_trips_desc),
                    icon = Icons.Outlined.Explore,
                    checked = showTrips.value,
                    onToggle = { showTrips.value = !showTrips.value }
                )
            }
        }
        item {
            PrimaryButton(
                text = stringResource(R.string.settings_save_button),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onBack() }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.settings_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SettingsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leadingIcon = icon,
            isPassword = isPassword,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PreferenceToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
