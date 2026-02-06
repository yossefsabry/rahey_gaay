package com.raheygaay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.raheygaay.app.ui.navigation.AppBottomBar
import com.raheygaay.app.ui.navigation.AppRoute
import com.raheygaay.app.ui.navigation.bottomBarRoutes
import com.raheygaay.app.ui.screens.auth.AuthScreen
import com.raheygaay.app.ui.screens.dashboard.DashboardScreen
import com.raheygaay.app.ui.screens.chat.ChatScreen
import com.raheygaay.app.ui.screens.home.HomeScreen
import com.raheygaay.app.ui.screens.info.InfoScreen
import com.raheygaay.app.ui.screens.loading.LoadingScreen
import com.raheygaay.app.ui.screens.map.MapScreen
import com.raheygaay.app.ui.screens.more.MoreScreen
import com.raheygaay.app.ui.screens.more.infoPages
import com.raheygaay.app.ui.screens.profile.OtherProfileScreen
import com.raheygaay.app.ui.screens.profile.ProfileScreen
import com.raheygaay.app.ui.screens.search.SearchScreen
import com.raheygaay.app.ui.screens.sahby.SahbyScreen
import com.raheygaay.app.ui.screens.settings.SettingsScreen
import com.raheygaay.app.ui.screens.support.SupportScreen
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import android.os.SystemClock
import com.raheygaay.app.ui.components.NavSkeletonOverlay
import com.raheygaay.app.ui.components.LocalNavigationProgress
import com.raheygaay.app.ui.components.StreakPopup
import com.raheygaay.app.ui.streak.StreakViewModel
import com.raheygaay.app.ui.preferences.UserPreferencesViewModel
import com.raheygaay.app.ui.performance.PerformanceViewModel
import com.raheygaay.app.ui.performance.PerformanceTracker
import kotlinx.coroutines.delay

private const val NAV_SKELETON_MIN_MS_DEFAULT = 300L
private const val NAV_SKELETON_MIN_MS_AUTH = 120L

@Composable
fun AppRoot(appState: AppState, performanceTracker: PerformanceTracker? = null) {
    val navController = appState.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = appState.isLoggedIn
    val streakViewModel: StreakViewModel = hiltViewModel()
    val streakState = streakViewModel.streakState.collectAsStateWithLifecycle()
    val popupVisible = streakViewModel.popupVisible.collectAsStateWithLifecycle()
    val preferencesViewModel: UserPreferencesViewModel = hiltViewModel()
    val preferences = preferencesViewModel.preferences.collectAsStateWithLifecycle()
    val performanceViewModel: PerformanceViewModel = hiltViewModel()
    val streakOwnerKey = when (appState.authState) {
        AuthState.Guest -> "guest"
        AuthState.LoggedIn -> "logged_in"
        AuthState.LoggedOut -> null
    }
    val showBottomBar = bottomBarRoutes.any { it.route == currentRoute }
    var navSkeletonVisible by remember { mutableStateOf(false) }
    var navSkeletonTarget by remember { mutableStateOf<String?>(null) }
    var navSkeletonStart by remember { mutableStateOf(0L) }
    var pendingNavigation by remember { mutableStateOf<PendingNavigation?>(null) }
    LaunchedEffect(currentRoute) {
        val route = currentRoute ?: return@LaunchedEffect
        val routeKey = resolveRouteKey(route)
        performanceTracker?.onScreenStart(routeKey)
        withFrameNanos { }
        performanceTracker?.onScreenFirstFrame(routeKey)
    }
    LaunchedEffect(appState.isArabic) {
        val localeTag = if (appState.isArabic) "ar" else "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
    }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        performanceViewModel.recordAppOpen()
    }
    LaunchedEffect(streakOwnerKey) {
        streakViewModel.setOwnerKey(streakOwnerKey)
        if (streakOwnerKey != null) {
            withFrameNanos { }
            streakViewModel.onAppOpened()
        }
    }
    LaunchedEffect(preferences.value) {
        preferences.value.isDark?.let { appState.isDark = it }
        preferences.value.isArabic?.let { appState.isArabic = it }
        preferences.value.authState?.let { stored ->
            appState.authState = runCatching { AuthState.valueOf(stored) }.getOrDefault(appState.authState)
        }
    }
    LaunchedEffect(currentRoute, navSkeletonTarget, navSkeletonVisible) {
        val target = navSkeletonTarget
        if (!navSkeletonVisible || target == null) return@LaunchedEffect
        if (currentRoute == target) {
            val elapsed = SystemClock.elapsedRealtime() - navSkeletonStart
            val minDuration = navSkeletonMinMs(target)
            val remaining = (minDuration - elapsed).coerceAtLeast(0L)
            if (remaining > 0L) {
                delay(remaining)
            }
            navSkeletonVisible = false
        }
    }
    LaunchedEffect(pendingNavigation) {
        val pending = pendingNavigation ?: return@LaunchedEffect
        withFrameNanos { }
        when (pending) {
            is PendingNavigation.Navigate -> {
                navController.navigate(pending.route, pending.builder)
            }
            is PendingNavigation.BottomBar -> {
                val route = pending.route
                val navigated = navController.popBackStack(route.route, inclusive = false)
                if (!navigated) {
                    navController.navigate(route.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(AppRoute.Home.route) {
                            saveState = true
                        }
                    }
                }
            }
            is PendingNavigation.Up -> {
                navController.navigateUp()
            }
        }
        pendingNavigation = null
    }
    fun startNavSkeleton(targetKey: String) {
        navSkeletonTarget = targetKey
        navSkeletonStart = SystemClock.elapsedRealtime()
        navSkeletonVisible = true
    }

    fun navigateWithSkeleton(
        route: String,
        targetKey: String = resolveRouteKey(route),
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        if (currentRoute == route) return
        startNavSkeleton(targetKey)
        pendingNavigation = PendingNavigation.Navigate(route, targetKey, builder)
    }

    fun navigateBottomBar(route: AppRoute) {
        if (currentRoute == route.route) return
        startNavSkeleton(route.route)
        pendingNavigation = PendingNavigation.BottomBar(route)
    }

    fun navigateUpWithSkeleton() {
        val targetRoute = navController.previousBackStackEntry?.destination?.route
        val targetKey = targetRoute?.let { resolveRouteKey(it) }
        if (targetKey != null) {
            startNavSkeleton(targetKey)
        }
        pendingNavigation = PendingNavigation.Up(targetKey)
    }
    fun requestAuth(targetRoute: String) {
        appState.pendingRoute = targetRoute
        navigateWithSkeleton(AppRoute.Login.route, AppRoute.Login.route) {
            launchSingleTop = true
        }
    }

    fun handleAuthSuccess(state: AuthState) {
        appState.authState = state
        preferencesViewModel.setAuthState(state.name)
        val targetRoute = appState.pendingRoute ?: AppRoute.Home.route
        appState.pendingRoute = null
        navigateWithSkeleton(targetRoute) {
            popUpTo(AppRoute.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun handleLogout() {
        appState.authState = AuthState.LoggedOut
        appState.pendingRoute = null
        preferencesViewModel.setAuthState(AuthState.LoggedOut.name)
        navigateWithSkeleton(AppRoute.Home.route) {
            popUpTo(AppRoute.Home.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toggleDark() {
        val newValue = !appState.isDark
        appState.isDark = newValue
        preferencesViewModel.setDark(newValue)
    }

    fun toggleLanguage() {
        val newValue = !appState.isArabic
        appState.isArabic = newValue
        preferencesViewModel.setArabic(newValue)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    AppBottomBar(
                        navController = navController,
                        routes = bottomBarRoutes,
                        isLoggedIn = isLoggedIn,
                        onAuthRequired = { route -> requestAuth(route.route) },
                        onNavigate = { route -> navigateBottomBar(route) }
                    )
                }
            }
        ) { padding ->
            CompositionLocalProvider(LocalNavigationProgress provides navSkeletonVisible) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = AppRoute.Loading.route,
                        modifier = Modifier.padding(padding)
                    ) {
                composable(AppRoute.Loading.route) {
                    LoadingScreen(onFinished = {
                        navigateWithSkeleton(AppRoute.Home.route, AppRoute.Home.route) {
                            popUpTo(AppRoute.Loading.route) { inclusive = true }
                        }
                    })
                }
                composable(AppRoute.Home.route) {
                    HomeScreen(
                        onSearch = { navigateWithSkeleton(AppRoute.Map.route) },
                        onSeeAllTravelers = { navigateWithSkeleton(AppRoute.Map.route) },
                        onContactTraveler = { traveler ->
                            val targetRoute = AppRoute.OtherProfile.createRoute(traveler.id)
                            if (isLoggedIn) {
                                navigateWithSkeleton(targetRoute, AppRoute.OtherProfile.route)
                            } else {
                                requestAuth(targetRoute)
                            }
                        },
                        onOpenProfile = {
                            if (isLoggedIn) {
                                navigateWithSkeleton(AppRoute.Profile.route)
                            } else {
                                requestAuth(AppRoute.Profile.route)
                            }
                        },
                        onOpenSearch = {
                            if (isLoggedIn) {
                                navigateWithSkeleton(AppRoute.Search.route)
                            } else {
                                requestAuth(AppRoute.Search.route)
                            }
                        },
                        onOpenSahby = {
                            if (isLoggedIn) {
                                navigateWithSkeleton(AppRoute.Sahby.route)
                            } else {
                                requestAuth(AppRoute.Sahby.route)
                            }
                        },
                        onOpenDashboard = {
                            if (isLoggedIn) {
                                navigateWithSkeleton(AppRoute.Dashboard.route)
                            } else {
                                requestAuth(AppRoute.Dashboard.route)
                            }
                        },
                        onToggleDark = { toggleDark() },
                        onLogout = { handleLogout() },
                        onOpenAuth = { navigateWithSkeleton(AppRoute.Login.route) },
                        isLoggedIn = isLoggedIn,
                        isGuest = appState.authState == AuthState.Guest,
                        isDark = appState.isDark
                    )
                }
                composable(AppRoute.Map.route) {
                    MapScreen(
                        onContact = {
                            if (isLoggedIn) {
                                navigateWithSkeleton(AppRoute.Support.route)
                            } else {
                                requestAuth(AppRoute.Support.route)
                            }
                        }
                    )
                }
            composable(AppRoute.Support.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Support.route) })
                } else {
                    SupportScreen(
                        onBack = { navigateUpWithSkeleton() },
                        onOpenChat = { chatId -> navigateWithSkeleton(AppRoute.Chat.createRoute(chatId), AppRoute.Chat.route) },
                        onOpenHelpCenter = { navigateWithSkeleton(AppRoute.Info.createRoute("help-center"), AppRoute.Info.route) },
                        onSearch = { navigateWithSkeleton(AppRoute.Info.createRoute("support"), AppRoute.Info.route) },
                        onNewChat = { chatId -> navigateWithSkeleton(AppRoute.Chat.createRoute(chatId), AppRoute.Chat.route) }
                    )
                }
            }
            composable(AppRoute.Profile.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Profile.route) })
                } else {
                    ProfileScreen(
                        isGuest = appState.authState == AuthState.Guest,
                        onBack = { navigateUpWithSkeleton() },
                        onOpenSettings = { navigateWithSkeleton(AppRoute.Settings.route) },
                        streakOwnerKey = streakOwnerKey
                    )
                }
            }
            composable(AppRoute.Search.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Search.route) })
                } else {
                    SearchScreen(
                        onBack = { navigateUpWithSkeleton() },
                        onOpenTraveler = { travelerId ->
                            navigateWithSkeleton(AppRoute.OtherProfile.createRoute(travelerId), AppRoute.OtherProfile.route)
                        },
                        onOpenTrip = { navigateWithSkeleton(AppRoute.Map.route) },
                        onOpenPlace = { navigateWithSkeleton(AppRoute.Map.route) }
                    )
                }
            }
            composable(AppRoute.Sahby.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Sahby.route) })
                } else {
                    SahbyScreen(onBack = { navigateUpWithSkeleton() })
                }
            }
            composable(AppRoute.Settings.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Settings.route) })
                } else {
                    SettingsScreen(
                        isDark = appState.isDark,
                        isArabic = appState.isArabic,
                        onBack = { navigateUpWithSkeleton() },
                        onToggleDark = { toggleDark() },
                        onToggleLanguage = { toggleLanguage() }
                    )
                }
            }
            composable(AppRoute.More.route) {
                MoreScreen(
                    isDark = appState.isDark,
                    isArabic = appState.isArabic,
                    isLoggedIn = isLoggedIn,
                    onToggleDark = { toggleDark() },
                    onToggleLanguage = { toggleLanguage() },
                    onOpenInfo = { page -> navigateWithSkeleton(AppRoute.Info.createRoute(page.id), AppRoute.Info.route) },
                    onOpenAuth = { navigateWithSkeleton(AppRoute.Login.route) },
                    onLogout = { handleLogout() },
                    onOpenDashboard = { navigateWithSkeleton(AppRoute.Dashboard.route) }
                )
            }
            composable(AppRoute.Login.route) {
                AuthScreen(
                    onLogin = { handleAuthSuccess(AuthState.LoggedIn) },
                    onRegister = { handleAuthSuccess(AuthState.LoggedIn) },
                    onGuestLogin = { handleAuthSuccess(AuthState.Guest) },
                    onTerms = { navigateWithSkeleton(AppRoute.Info.createRoute("terms"), AppRoute.Info.route) },
                    onPrivacy = { navigateWithSkeleton(AppRoute.Info.createRoute("privacy"), AppRoute.Info.route) }
                )
            }
            composable(AppRoute.Register.route) {
                AuthScreen(
                    onLogin = { handleAuthSuccess(AuthState.LoggedIn) },
                    onRegister = { handleAuthSuccess(AuthState.LoggedIn) },
                    onGuestLogin = { handleAuthSuccess(AuthState.Guest) },
                    onTerms = { navigateWithSkeleton(AppRoute.Info.createRoute("terms"), AppRoute.Info.route) },
                    onPrivacy = { navigateWithSkeleton(AppRoute.Info.createRoute("privacy"), AppRoute.Info.route) },
                    startOnLogin = false
                )
            }
            composable(
                route = AppRoute.OtherProfile.route,
                arguments = listOf(navArgument("travelerId") { type = NavType.StringType })
            ) { entry ->
                val travelerId = entry.arguments?.getString("travelerId").orEmpty().ifBlank { "ahmed_ali" }
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.OtherProfile.createRoute(travelerId)) })
                } else {
                    OtherProfileScreen(
                        onBack = { navigateUpWithSkeleton() },
                        onContact = { navigateWithSkeleton(AppRoute.Chat.createRoute(travelerId), AppRoute.Chat.route) }
                    )
                }
            }
            composable(
                route = AppRoute.Chat.route,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { _ ->
                ChatScreen(onBack = { navigateUpWithSkeleton() })
            }
            composable(AppRoute.Dashboard.route) {
                if (!isLoggedIn) {
                    AuthRedirect(onRedirect = { requestAuth(AppRoute.Dashboard.route) })
                } else {
                    DashboardScreen(onBack = { navigateUpWithSkeleton() })
                }
            }
            composable(
                route = AppRoute.Info.route,
                arguments = listOf(navArgument("pageId") { type = NavType.StringType })
            ) { entry ->
                val pageId = entry.arguments?.getString("pageId") ?: "about"
                val page = infoPages().firstOrNull { it.id == pageId } ?: infoPages().first()
                InfoScreen(page = page, onBack = { navigateUpWithSkeleton() })
            }
            }
                NavSkeletonOverlay(
                    visible = navSkeletonVisible,
                    routeKey = navSkeletonTarget,
                    modifier = Modifier.padding(padding)
                )
            }
        }
        }
        if (streakOwnerKey != null && popupVisible.value) {
            StreakPopup(state = streakState.value, onDismiss = { streakViewModel.dismissPopup() })
        }
    }
}

private fun resolveRouteKey(route: String): String {
    return when {
        route.startsWith("chat/") -> AppRoute.Chat.route
        route.startsWith("other_profile/") -> AppRoute.OtherProfile.route
        route.startsWith("info/") -> AppRoute.Info.route
        else -> route
    }
}

private fun navSkeletonMinMs(target: String): Long {
    return when (target) {
        AppRoute.Login.route,
        AppRoute.Register.route -> NAV_SKELETON_MIN_MS_AUTH
        else -> NAV_SKELETON_MIN_MS_DEFAULT
    }
}

private sealed class PendingNavigation {
    data class Navigate(
        val route: String,
        val targetKey: String,
        val builder: NavOptionsBuilder.() -> Unit
    ) : PendingNavigation()

    data class BottomBar(val route: AppRoute) : PendingNavigation()

    data class Up(val targetKey: String?) : PendingNavigation()
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
