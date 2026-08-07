package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.GrayText
import com.nisr.sauservices.ui.theme.PrimaryBlue
import com.nisr.sauservices.ui.theme.White

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    Surface(
        color = White,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = White,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(72.dp)
                .navigationBarsPadding()
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
                        Icon(
                            item.icon, 
                            null, 
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { 
                        Text(
                            item.title, 
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        unselectedIconColor = GrayText,
                        unselectedTextColor = GrayText,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
