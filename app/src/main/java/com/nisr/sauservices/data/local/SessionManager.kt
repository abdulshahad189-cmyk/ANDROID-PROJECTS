package com.nisr.sauservices.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sau_services_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LOGIN_STATE = "login_state"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_LAT = "latitude"
        const val KEY_LNG = "longitude"
        const val KEY_ADDRESS = "address"
    }

    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_LOGIN_STATE, isLoggedIn) }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGIN_STATE, false)

    fun saveUserRole(role: String) {
        prefs.edit { putString(KEY_USER_ROLE, role) }
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun saveLocation(lat: Double, lng: Double, address: String) {
        prefs.edit {
            putFloat(KEY_LAT, lat.toFloat())
            putFloat(KEY_LNG, lng.toFloat())
            putString(KEY_ADDRESS, address)
        }
    }

    fun getAddress(): String = prefs.getString(KEY_ADDRESS, "Fetching location...") ?: "Fetching location..."

    fun logout() {
        prefs.edit { clear() }
    }
}
