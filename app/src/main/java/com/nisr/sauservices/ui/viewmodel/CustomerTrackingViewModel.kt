package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerTrackingViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _trackedOrder = MutableStateFlow<OrderModel?>(null)
    val trackedOrder = _trackedOrder.asStateFlow()

    private val _deliveryLocation = MutableStateFlow<LatLng?>(null)
    val deliveryLocation = _deliveryLocation.asStateFlow()

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            repository.listenToCustomerOrder(orderId).collect { order ->
                _trackedOrder.value = order
                // If OrderModel has lat/lng fields in the new schema, update accordingly
            }
        }
    }
}
