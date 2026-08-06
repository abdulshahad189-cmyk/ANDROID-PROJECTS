package com.nisr.sauservices.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PrimaryBlue
import com.nisr.sauservices.ui.theme.InactiveIcon

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Home", Screen.Home.route, Icons.Outlined.Home),
            NavigationItem("Categories", Screen.Categories.route, Icons.Outlined.GridView),
            NavigationItem("Orders", Screen.MyOrders.route, Icons.AutoMirrored.Outlined.Assignment),
            NavigationItem("Track", Screen.OrderTracking.route, Icons.Outlined.MyLocation),
            NavigationItem("Profile", Screen.Profile.route, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route || (item.route == Screen.OrderTracking.route && currentRoute?.startsWith("order_tracking") == true)
            
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.9f,
                animationSpec = tween(180)
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.route == Screen.OrderTracking.route) {
                        navController.navigate(Screen.OrderTracking.createRoute("active_order"))
                    } else if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(item.icon, null, modifier = Modifier.size(24.dp).scale(iconScale))
                },
                label = { 
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(180)),
                        exit = fadeOut(tween(180))
                    ) {
                        Text(item.title, fontSize = 10.sp)
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = InactiveIcon,
                    unselectedTextColor = InactiveIcon,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
