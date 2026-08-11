package com.nisr.sauservices.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.location.LocationPermissionScreen
import com.nisr.sauservices.ui.location.LocationPickerScreen
import com.nisr.sauservices.ui.location.OrderTrackingScreen
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import com.nisr.sauservices.ui.viewmodel.TrackingViewModel

/**
 * Modular Navigation Graph for Map and Location features.
 */
fun NavGraphBuilder.locationNavGraph(
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    trackingViewModel: TrackingViewModel
) {
    composable(Screen.LocationPermission.route) {
        LocationPermissionScreen(navController = navController)
    }

    composable(Screen.MapPicker.route) {
        LocationPickerScreen(
            navController = navController,
            viewModel = locationViewModel
        )
    }
    
    composable(
        route = Screen.OrderTracking.route,
        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        OrderTrackingScreen(
            navController = navController,
            orderId = orderId,
            viewModel = trackingViewModel
        )
    }
}
