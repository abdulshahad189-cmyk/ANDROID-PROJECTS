package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Professional UI State for Order Tracking
 */
data class TrackingUiState(
    val orderId: String = "",
    val statusTitle: String = "Processing...",
    val statusSubtitle: String = "Fetching order details...",
    val progress: Float = 0.2f,
    val partnerLocation: LatLng = LatLng(20.5937, 78.9629),
    val destinationLocation: LatLng? = null,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true
)

class TrackingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    fun startTracking(orderId: String) {
        _uiState.update { it.copy(orderId = orderId, isLoading = true) }
        
        viewModelScope.launch {
            // Simulate initial data fetch
            delay(1000)
            _uiState.update { 
                it.copy(
                    statusTitle = "Order Confirmed",
                    statusSubtitle = "Partner is being assigned...",
                    progress = 0.3f,
                    isLoading = false
                )
            }

            // Simulate partner assignment
            delay(2000)
            _uiState.update {
                it.copy(
                    statusTitle = "Heading to Shop",
                    statusSubtitle = "Partner is picking up your items",
                    progress = 0.4f,
                    partnerLocation = LatLng(20.5940, 78.9635)
                )
            }

            // Professional Note: In production, use Supabase Realtime listener here
            // supabase.realtime["orders"].select(filters = { eq("id", orderId) }).subscribe { ... }
            
            simulateLiveMovement()
        }
    }

    private fun simulateLiveMovement() {
        viewModelScope.launch {
            while (!_uiState.value.isCompleted) {
                delay(3000)
                _uiState.update { state ->
                    val newLat = state.partnerLocation.latitude + (Math.random() - 0.5) * 0.001
                    val newLng = state.partnerLocation.longitude + (Math.random() - 0.5) * 0.001
                    state.copy(partnerLocation = LatLng(newLat, newLng))
                }
            }
        }
    }

    fun updateDestination(latLng: LatLng, addressName: String) {
        _uiState.update { 
            it.copy(
                destinationLocation = latLng,
                statusTitle = "Out for Delivery",
                statusSubtitle = "Partner is moving towards $addressName",
                progress = 0.7f
            )
        }
    }
}
