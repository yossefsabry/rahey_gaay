package com.raheygaay.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AppState(
    val navController: NavHostController,
    initialDark: Boolean,
    initialArabic: Boolean
) {
    var isDark by mutableStateOf(initialDark)
    var isArabic by mutableStateOf(initialArabic)
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
): AppState {
    val context = LocalContext.current
    val isArabic = context.resources.configuration.locales[0].language == "ar"
    val isDark = isSystemInDarkTheme()

    return remember(navController, isArabic, isDark) {
        AppState(navController, initialDark = isDark, initialArabic = isArabic)
    }
}
