package com.nisr.sauservices

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.nisr.sauservices.data.api.SupabaseConfig
import com.nisr.sauservices.navigation.AppNavHost
import com.nisr.sauservices.ui.theme.AppTheme
import io.github.jan.supabase.auth.auth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Supabase Connection Test
        val client = SupabaseConfig.client

        setContent {

            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                try {
                    val user = client.auth.currentSessionOrNull()?.user

                    Log.d(
                        "SUPABASE_TEST",
                        "Current User: ${user?.email ?: "Not logged in"}",
                    )
                } catch (e: Exception) {

                    Log.e(
                        "SUPABASE_TEST",
                        "Init Error: ${e.message}"
                    )
                }
            }

            AppTheme {
                // Let AppNavHost handle the Splash screen as its start destination
                AppNavHost(navController)
            }
        }
    }
}