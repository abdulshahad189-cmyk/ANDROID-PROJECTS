package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.payment.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.Screen

fun NavGraphBuilder.bookingNavGraph(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    bookingsViewModel: BookingsViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel,
    educationCartViewModel: EducationCartViewModel
) {
    // Unified Bookings List
    composable(Routes.BOOKINGS) {
        BookingsScreen(navController, bookingsViewModel)
    }

    // Unified Cart for all Services
    composable(Routes.CART) {
        UnifiedCartScreen(
            navController = navController,
            residentialViewModel = residentialViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = homeCartViewModel,
            educationViewModel = educationCartViewModel
        )
    }

    // Shared Checkout Flow for Services
    composable(Routes.SERVICE_BOOKING_DETAILS) {
        ResidentialBookingDetailsScreen(navController, residentialViewModel)
    }

    composable(Routes.SERVICE_PAYMENT) {
        ResidentialPaymentScreen(navController, residentialViewModel)
    }

    composable(Routes.SERVICE_ORDER_SUMMARY) {
        ResidentialOrderSummaryScreen(
            navController = navController,
            viewModel = residentialViewModel,
            bookingsViewModel = bookingsViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = homeCartViewModel,
            educationViewModel = educationCartViewModel
        )
    }

    composable(Routes.SERVICE_BOOKING_SUCCESS) {
        BookingSuccessScreen(navController)
    }

    // --- PAYMENT FLOW ---
    composable(
        route = Screen.PaymentMethod.route,
        arguments = listOf(
            navArgument("bookingId") { type = NavType.StringType },
            navArgument("customerId") { type = NavType.StringType },
            navArgument("partnerId") { type = NavType.StringType },
            navArgument("amount") { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val bId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val cId = backStackEntry.arguments?.getString("customerId") ?: ""
        val pId = backStackEntry.arguments?.getString("partnerId") ?: ""
        val amt = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
        PaymentMethodScreen(navController, bId, cId, pId, amt)
    }

    composable(
        route = Screen.CashSuccess.route,
        arguments = listOf(
            navArgument("paymentId") { type = NavType.StringType },
            navArgument("amount") { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val pId = backStackEntry.arguments?.getString("paymentId") ?: ""
        val amt = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
        CashBookingSuccessScreen(navController, pId, amt)
    }

    composable(
        route = Screen.CashCollection.route,
        arguments = listOf(
            navArgument("paymentId") { type = NavType.StringType },
            navArgument("bookingId") { type = NavType.StringType },
            navArgument("amount") { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val pId = backStackEntry.arguments?.getString("paymentId") ?: ""
        val bId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val amt = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
        CashCollectionScreen(navController, pId, bId, amt)
    }

    composable(
        route = Screen.CustomerOtp.route,
        arguments = listOf(
            navArgument("paymentId") { type = NavType.StringType },
            navArgument("bookingId") { type = NavType.StringType },
            navArgument("amount") { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val pId = backStackEntry.arguments?.getString("paymentId") ?: ""
        val bId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val amt = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
        CustomerOtpScreen(navController, pId, bId, amt)
    }

    composable(
        route = Screen.PaidSuccess.route,
        arguments = listOf(navArgument("amount") { type = NavType.FloatType })
    ) { backStackEntry ->
        val amt = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
        DigitalPaymentSuccessScreen(navController, amt)
    }
}
