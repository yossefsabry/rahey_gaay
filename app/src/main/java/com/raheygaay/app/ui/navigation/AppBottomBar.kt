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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    routes: List<AppRoute>,
    isLoggedIn: Boolean,
    onAuthRequired: (AppRoute) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
                    if (!isLoggedIn && route.route !in setOf(AppRoute.Home.route, AppRoute.Map.route, AppRoute.More.route)) {
                        onAuthRequired(route)
                    } else {
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
