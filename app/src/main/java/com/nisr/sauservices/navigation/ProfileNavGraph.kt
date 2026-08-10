package com.nisr.sauservices.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.profile.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

/**
 * Navigation Graph for Profile and Settings related screens.
 */
fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    composable(Screen.Profile.route) {
        ProfileScreen(navController, profileViewModel)
    }

    composable(Screen.EditProfile.route) {
        EditProfileScreen(navController, profileViewModel)
    }

    composable(Screen.Notifications.route) {
        NotificationsScreen(navController, profileViewModel)
    }

    composable(Screen.ShippingAddress.route) {
        ShippingAddressScreen(navController, profileViewModel)
    }

    composable(Screen.ChangePassword.route) {
        ChangePasswordScreen(navController)
    }

    composable(Screen.AddAccounts.route) {
        AddAccountsScreen(navController)
    }

    composable(Screen.ContactUs.route) {
        ContactUsScreen(navController, profileViewModel)
    }

    composable(Screen.FAQ.route) {
        FAQScreen(navController)
    }
}
