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
    routes: List<AppRoute>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
        routes.forEach { route ->
            val selected = currentRoute == route.route
            val label = route.labelRes?.let { stringResource(it) } ?: route.route
            NavigationBarItem(
                selected = selected,
                onClick = {
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
