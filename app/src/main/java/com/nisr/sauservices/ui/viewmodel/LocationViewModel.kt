package com.nisr.sauservices.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LocationViewModel : ViewModel() {

    var uiState by mutableStateOf(LocationUiState())
        private set

    private var geocodeJob: Job? = null
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    data class LocationUiState(
        val centerLocation: LatLng = LatLng(20.5937, 78.9629), // Default India
        val address: String = "Fetching address...",
        val landmark: String = "",
        val isFetchingAddress: Boolean = false,
        val isLocationConfirmed: Boolean = false
    )

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                updateCenterLocation(latLng, context)
            }
        }
    }

    fun updateCenterLocation(latLng: LatLng, context: Context) {
        uiState = uiState.copy(centerLocation = latLng, isFetchingAddress = true)
        
        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            reverseGeocode(latLng, context)
        }
    }

    private fun reverseGeocode(latLng: LatLng, context: Context) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: ""
                val landmark = address.featureName ?: ""
                
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(
                        address = fullAddress,
                        landmark = landmark,
                        isFetchingAddress = false
                    )
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false)
            }
        }
    }

    fun searchLocation(query: String, context: Context) {
        if (query.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    viewModelScope.launch(Dispatchers.Main) {
                        updateCenterLocation(latLng, context)
                    }
                }
            } catch (e: Exception) {
                // Handle search error
            }
        }
    }

    fun confirmLocation(onSuccess: () -> Unit) {
        val userId = auth.currentUserOrNull()?.id ?: return
        
        // Prevent saving invalid addresses
        if (uiState.address == "Fetching address..." || uiState.isFetchingAddress) return

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Update user's location in the 'users' table or a dedicated 'locations' table
                    postgrest["users"].update({
                        set("address", uiState.address)
                        set("latitude", uiState.centerLocation.latitude)
                        set("longitude", uiState.centerLocation.longitude)
                    }) {
                        filter { eq("id", userId) }
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
