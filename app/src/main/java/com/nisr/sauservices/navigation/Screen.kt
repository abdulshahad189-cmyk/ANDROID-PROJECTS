package com.nisr.sauservices.navigation

sealed class Screen(
    val route: String
) {

    data object Splash :
        Screen("splash")

    data object Onboarding :
        Screen("onboarding")

    data object Login :
        Screen("login")

    data object SignUp :
        Screen("signup")

    data object Otp :
        Screen("otp")

    data object ForgotPassword :
        Screen("forgot_password")

    data object ResetPassword :
        Screen("reset_password")

    data object Home :
        Screen("home")
}