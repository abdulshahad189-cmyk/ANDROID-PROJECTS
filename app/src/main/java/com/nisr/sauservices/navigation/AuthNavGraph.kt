package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nisr.sauservices.ui.auth.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable(Routes.ONBOARDING) {
        OnboardingScreen(navController)
    }
    
    composable(Routes.LOGIN) {
        SignInScreen(navController) 
    }
    
    composable(Routes.SIGNUP) {
        SignUpScreen(navController)
    }
    
    composable(Routes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(navController)
    }
}
