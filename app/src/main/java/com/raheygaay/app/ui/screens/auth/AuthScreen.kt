package com.raheygaay.app.ui.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.raheygaay.app.BuildConfig
import com.raheygaay.app.R
import com.raheygaay.app.ui.components.AppTextField
import com.raheygaay.app.ui.components.BrandLogo
import com.raheygaay.app.ui.components.PrimaryButton
import com.raheygaay.app.ui.components.SecondaryButton

@Composable
fun AuthScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGuestLogin: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    startOnLogin: Boolean = true
) {
    val isLogin = remember { mutableStateOf(startOnLogin) }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val appName = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
            stringResource(R.string.app_name)
        } else {
            BuildConfig.APP_NAME
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            BrandLogo(modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = appName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.auth_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    AuthTab(
                        title = stringResource(R.string.auth_tab_login),
                        selected = isLogin.value,
                        onClick = { isLogin.value = true },
                        modifier = Modifier.weight(1f)
                    )
                    AuthTab(
                        title = stringResource(R.string.auth_tab_signup),
                        selected = !isLogin.value,
                        onClick = { isLogin.value = false },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isLogin.value) {
                    AuthField(
                        label = stringResource(R.string.auth_label_email),
                        placeholder = stringResource(R.string.auth_placeholder_email),
                        value = email.value,
                        onValueChange = { email.value = it },
                        icon = Icons.Outlined.Mail,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthField(
                        label = stringResource(R.string.auth_label_password),
                        placeholder = stringResource(R.string.auth_placeholder_password),
                        value = password.value,
                        onValueChange = { password.value = it },
                        icon = Icons.Outlined.Lock,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = stringResource(R.string.auth_button_sign_in),
                        onClick = {
                            if (email.value.isBlank() || password.value.isBlank()) {
                                onGuestLogin()
                            } else {
                                onLogin()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AuthField(
                        label = stringResource(R.string.auth_label_full_name),
                        placeholder = stringResource(R.string.auth_placeholder_name),
                        value = name.value,
                        onValueChange = { name.value = it },
                        icon = Icons.Outlined.Person
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthField(
                        label = stringResource(R.string.auth_label_email),
                        placeholder = stringResource(R.string.auth_placeholder_email),
                        value = email.value,
                        onValueChange = { email.value = it },
                        icon = Icons.Outlined.Mail,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthField(
                        label = stringResource(R.string.auth_label_create_password),
                        placeholder = stringResource(R.string.auth_placeholder_password),
                        value = password.value,
                        onValueChange = { password.value = it },
                        icon = Icons.Outlined.Lock,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = stringResource(R.string.auth_button_create_account),
                        onClick = {
                            if (name.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
                                onGuestLogin()
                            } else {
                                onRegister()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.auth_or_continue),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton(
                        text = stringResource(R.string.auth_google),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.auth_facebook),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(
                    text = stringResource(R.string.auth_guest_button),
                    onClick = onGuestLogin,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.auth_terms_line),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Row {
            Text(
                text = stringResource(R.string.auth_terms_title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onTerms() }
            )
            Text(
                text = stringResource(R.string.auth_privacy_title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onPrivacy() }
            )
        }
    }
}

@Composable
private fun AuthTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = color)
    }
}

@Composable
private fun AuthField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leadingIcon = icon,
            isPassword = isPassword,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
