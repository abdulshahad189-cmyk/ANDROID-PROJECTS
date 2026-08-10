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
        val isLocationConfirmed: Boolean = false,
        val error: String? = null
    )

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                updateCenterLocation(latLng, context)
            }
        }.addOnFailureListener {
            uiState = uiState.copy(error = "Failed to get current location")
        }
    }

    fun updateCenterLocation(latLng: LatLng, context: Context) {
        uiState = uiState.copy(centerLocation = latLng, isFetchingAddress = true, error = null)
        
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
                val fullAddress = address.getAddressLine(0)
                val landmark = address.featureName ?: ""
                
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(
                        address = fullAddress,
                        landmark = landmark,
                        isFetchingAddress = false
                    )
                }
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(address = "Address not found", isFetchingAddress = false)
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false, error = e.message)
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
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        uiState = uiState.copy(error = "Location not found")
                    }
                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(error = "Search failed: ${e.message}")
                }
            }
        }
    }

    fun confirmLocation(onSuccess: () -> Unit) {
        val userId = auth.currentUserOrNull()?.id ?: return
        
        // Prevent saving invalid addresses
        if (uiState.address == "Fetching address..." || uiState.isFetchingAddress) return

        viewModelScope.launch {
            try {
                val locationData = mapOf(
                    "address" to uiState.address,
                    "latitude" to uiState.centerLocation.latitude,
                    "longitude" to uiState.centerLocation.longitude,
                    "last_updated" to System.currentTimeMillis()
                )

                postgrest["users"].update({
                    set("selected_location", locationData)
                }) {
                    filter { eq("id", userId) }
                }
                
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Failed to save location: ${e.message}")
            }
        }
    }
}
