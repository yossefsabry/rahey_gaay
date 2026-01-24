package com.raheygaay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.raheygaay.app.ui.screens.dashboard.DashboardScreen
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.raheygaay.app.ui.components.StreakPopup
import com.raheygaay.app.ui.streak.StreakViewModel

@Composable
fun AppRoot(appState: AppState) {
    val navController = appState.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = appState.isLoggedIn
    val streakViewModel: StreakViewModel = hiltViewModel()
    val streakState = streakViewModel.streakState.collectAsState()
    val popupVisible = streakViewModel.popupVisible.collectAsState()
    val showBottomBar = bottomBarRoutes.any { it.route == currentRoute }
    LaunchedEffect(appState.isArabic) {
        val localeTag = if (appState.isArabic) "ar" else "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
    }
    LaunchedEffect(Unit) {
        streakViewModel.onAppOpened()
    }
    fun requestAuth(targetRoute: String) {
        appState.pendingRoute = targetRoute
        navController.navigate(AppRoute.Login.route) {
            launchSingleTop = true
        }
    }

    fun handleAuthSuccess(state: AuthState) {
        appState.authState = state
        val targetRoute = appState.pendingRoute ?: AppRoute.Home.route
        appState.pendingRoute = null
        navController.navigate(targetRoute) {
            popUpTo(AppRoute.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun handleLogout() {
        appState.authState = AuthState.LoggedOut
        appState.pendingRoute = null
        navController.navigate(AppRoute.Home.route) {
            popUpTo(AppRoute.Home.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    AppBottomBar(
                        navController = navController,
                        routes = bottomBarRoutes,
                        isLoggedIn = isLoggedIn,
                        onAuthRequired = { route -> requestAuth(route.route) }
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
                    onContactTraveler = {
                        if (isLoggedIn) {
                            navController.navigate(AppRoute.Support.route)
                        } else {
                            requestAuth(AppRoute.Support.route)
                        }
                    },
                    onOpenProfile = {
                        if (isLoggedIn) {
                            navController.navigate(AppRoute.Profile.route)
                        } else {
                            requestAuth(AppRoute.Profile.route)
                        }
                    },
                    onOpenDashboard = {
                        if (isLoggedIn) {
                            navController.navigate(AppRoute.Dashboard.route)
                        } else {
                            requestAuth(AppRoute.Dashboard.route)
                        }
                    },
                    onToggleDark = { appState.isDark = !appState.isDark },
                    onLogout = { handleLogout() },
                    onOpenAuth = { navController.navigate(AppRoute.Login.route) },
                    isLoggedIn = isLoggedIn,
                    isGuest = appState.authState == AuthState.Guest,
                    isDark = appState.isDark
                )
            }
            composable(AppRoute.Map.route) {
                MapScreen(onContact = {
                    if (isLoggedIn) {
                        navController.navigate(AppRoute.Support.route)
                    } else {
                        requestAuth(AppRoute.Support.route)
                    }
                })
            }
            composable(AppRoute.Support.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Support.route) })
                } else {
                    SupportScreen(
                        onBack = { navController.navigateUp() },
                        onOpenChat = { navController.navigate(AppRoute.OtherProfile.route) }
                    )
                }
            }
            composable(AppRoute.Profile.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Profile.route) })
                } else {
                    ProfileScreen(isGuest = appState.authState == AuthState.Guest)
                }
            }
            composable(AppRoute.More.route) {
                MoreScreen(
                    isDark = appState.isDark,
                    isArabic = appState.isArabic,
                    isLoggedIn = isLoggedIn,
                    onToggleDark = { appState.isDark = !appState.isDark },
                    onToggleLanguage = { appState.isArabic = !appState.isArabic },
                    onOpenInfo = { page -> navController.navigate(AppRoute.Info.createRoute(page.id)) },
                    onOpenAuth = { navController.navigate(AppRoute.Login.route) },
                    onLogout = { handleLogout() },
                    onOpenDashboard = { navController.navigate(AppRoute.Dashboard.route) }
                )
            }
            composable(AppRoute.Login.route) {
                AuthScreen(
                    onLogin = { handleAuthSuccess(AuthState.LoggedIn) },
                    onRegister = { handleAuthSuccess(AuthState.LoggedIn) },
                    onGuestLogin = { handleAuthSuccess(AuthState.Guest) },
                    onTerms = { navController.navigate(AppRoute.Info.createRoute("terms")) },
                    onPrivacy = { navController.navigate(AppRoute.Info.createRoute("privacy")) }
                )
            }
            composable(AppRoute.Register.route) {
                AuthScreen(
                    onLogin = { handleAuthSuccess(AuthState.LoggedIn) },
                    onRegister = { handleAuthSuccess(AuthState.LoggedIn) },
                    onGuestLogin = { handleAuthSuccess(AuthState.Guest) },
                    onTerms = { navController.navigate(AppRoute.Info.createRoute("terms")) },
                    onPrivacy = { navController.navigate(AppRoute.Info.createRoute("privacy")) },
                    startOnLogin = false
                )
            }
            composable(AppRoute.OtherProfile.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.OtherProfile.route) })
                } else {
                    OtherProfileScreen(
                        onBack = { navController.navigateUp() },
                        onContact = { navController.navigate(AppRoute.Support.route) }
                    )
                }
            }
            composable(AppRoute.Dashboard.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Dashboard.route) })
                } else {
                    DashboardScreen()
                }
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
        if (popupVisible.value) {
            StreakPopup(state = streakState.value, onDismiss = { streakViewModel.dismissPopup() })
        }
    }
}

@Composable
private fun AuthRedirect(onRedirect: () -> Unit) {
    LaunchedEffect(Unit) {
        onRedirect()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
