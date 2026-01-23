package com.raheygaay.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.raheygaay.app.ui.navigation.AppBottomBar
import com.raheygaay.app.ui.navigation.AppRoute
import com.raheygaay.app.ui.navigation.bottomBarRoutes
import com.raheygaay.app.ui.screens.auth.AuthScreen
import com.raheygaay.app.ui.screens.home.HomeScreen
import com.raheygaay.app.ui.screens.info.InfoScreen
import com.raheygaay.app.ui.screens.loading.LoadingScreen
import com.raheygaay.app.ui.screens.map.MapScreen
import com.raheygaay.app.ui.screens.more.MoreScreen
import com.raheygaay.app.ui.screens.more.infoPages
import com.raheygaay.app.ui.screens.profile.OtherProfileScreen
import com.raheygaay.app.ui.screens.profile.ProfileScreen
import com.raheygaay.app.ui.screens.support.SupportScreen
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

@Composable
fun AppRoot(appState: AppState) {
    val navController = appState.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = bottomBarRoutes.any { it.route == currentRoute }
    LaunchedEffect(appState.isArabic) {
        val localeTag = if (appState.isArabic) "ar" else "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
    }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    navController = navController,
                    routes = bottomBarRoutes
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Loading.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppRoute.Loading.route) {
                LoadingScreen(onFinished = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Loading.route) { inclusive = true }
                    }
                })
            }
            composable(AppRoute.Home.route) {
                HomeScreen(
                    onSearch = { navController.navigate(AppRoute.Map.route) },
                    onSeeAllTravelers = { navController.navigate(AppRoute.Map.route) },
                    onContactTraveler = { navController.navigate(AppRoute.Support.route) },
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) }
                )
            }
            composable(AppRoute.Map.route) {
                MapScreen(onContact = { navController.navigate(AppRoute.Support.route) })
            }
            composable(AppRoute.Support.route) {
                SupportScreen(
                    onBack = { navController.navigateUp() },
                    onOpenChat = { navController.navigate(AppRoute.OtherProfile.route) }
                )
            }
            composable(AppRoute.Profile.route) { ProfileScreen() }
            composable(AppRoute.More.route) {
                MoreScreen(
                    isDark = appState.isDark,
                    isArabic = appState.isArabic,
                    onToggleDark = { appState.isDark = !appState.isDark },
                    onToggleLanguage = { appState.isArabic = !appState.isArabic },
                    onOpenInfo = { page -> navController.navigate(AppRoute.Info.createRoute(page.id)) },
                    onOpenAuth = { navController.navigate(AppRoute.Login.route) }
                )
            }
            composable(AppRoute.Login.route) {
                AuthScreen(
                    onLogin = { navController.navigate(AppRoute.Home.route) },
                    onRegister = { navController.navigate(AppRoute.Home.route) },
                    onTerms = { navController.navigate(AppRoute.Info.createRoute("terms")) },
                    onPrivacy = { navController.navigate(AppRoute.Info.createRoute("privacy")) }
                )
            }
            composable(AppRoute.Register.route) {
                AuthScreen(
                    onLogin = { navController.navigate(AppRoute.Home.route) },
                    onRegister = { navController.navigate(AppRoute.Home.route) },
                    onTerms = { navController.navigate(AppRoute.Info.createRoute("terms")) },
                    onPrivacy = { navController.navigate(AppRoute.Info.createRoute("privacy")) },
                    startOnLogin = false
                )
            }
            composable(AppRoute.OtherProfile.route) {
                OtherProfileScreen(
                    onBack = { navController.navigateUp() },
                    onContact = { navController.navigate(AppRoute.Support.route) }
                )
            }
            composable(
                route = AppRoute.Info.route,
                arguments = listOf(navArgument("pageId") { type = NavType.StringType })
            ) { entry ->
                val pageId = entry.arguments?.getString("pageId") ?: "about"
                val page = infoPages().firstOrNull { it.id == pageId } ?: infoPages().first()
                InfoScreen(page = page, onBack = { navController.navigateUp() })
            }
        }
    }
}
