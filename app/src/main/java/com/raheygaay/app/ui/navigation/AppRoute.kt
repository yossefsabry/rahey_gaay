package com.raheygaay.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.raheygaay.app.R

sealed class AppRoute(
    val route: String,
    val labelRes: Int? = null,
    val icon: ImageVector? = null
) {
    object Loading : AppRoute("loading")
    object Home : AppRoute("home", R.string.nav_home, Icons.Outlined.Home)
    object Map : AppRoute("map", R.string.nav_map, Icons.Outlined.Explore)
    object Support : AppRoute("support", R.string.nav_support, Icons.Outlined.ChatBubbleOutline)
    object Profile : AppRoute("profile", R.string.nav_profile, Icons.Outlined.PersonOutline)
    object More : AppRoute("more", R.string.nav_more, Icons.Outlined.Menu)
    object Dashboard : AppRoute("dashboard")
    object Settings : AppRoute("settings")
    object Search : AppRoute("search")
    object Sahby : AppRoute("sahby")
    object Chat : AppRoute("chat/{chatId}") {
        fun createRoute(chatId: String): String = "chat/$chatId"
    }
    object Login : AppRoute("login")
    object Register : AppRoute("register")
    object OtherProfile : AppRoute("other_profile/{travelerId}") {
        fun createRoute(travelerId: String): String = "other_profile/$travelerId"
    }
    object Info : AppRoute("info/{pageId}") {
        fun createRoute(pageId: String): String {
            return "info/$pageId"
        }
    }
}

val bottomBarRoutes = listOf(
    AppRoute.Home,
    AppRoute.Map,
    AppRoute.Support,
    AppRoute.Profile,
    AppRoute.More
)
