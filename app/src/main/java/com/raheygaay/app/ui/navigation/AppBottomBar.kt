package com.raheygaay.app.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    routes: List<AppRoute>,
    isLoggedIn: Boolean,
    onAuthRequired: (AppRoute) -> Unit,
    onNavigate: (AppRoute) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptic = LocalHapticFeedback.current
    val visibleRoutes = if (isLoggedIn) {
        routes
    } else {
        val allowed = setOf(AppRoute.Home.route, AppRoute.Map.route, AppRoute.More.route)
        routes.filter { allowed.contains(it.route) }
    }

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
        visibleRoutes.forEach { route ->
            val selected = currentRoute == route.route
            val label = route.labelRes?.let { stringResource(it) } ?: route.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (!isLoggedIn && route.route !in setOf(AppRoute.Home.route, AppRoute.Map.route, AppRoute.More.route)) {
                        onAuthRequired(route)
                    } else {
                        onNavigate(route)
                    }
                },
                icon = {
                    route.icon?.let { icon ->
                        androidx.compose.material3.Icon(
                            imageVector = icon,
                            contentDescription = label
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }
    }
}
