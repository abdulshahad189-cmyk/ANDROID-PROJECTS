package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiceWorkerViewModel : ViewModel() {
    private val repository = SupabaseRepository()
    private val workerId = repository.getCurrentUserId() ?: ""

    private val _pendingBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val pendingBookings = _pendingBookings.asStateFlow()

    private val _acceptedBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val acceptedBookings = _acceptedBookings.asStateFlow()

    private val _completedBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val completedBookings = _completedBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observeAllServiceRequests()
    }

    private fun observeAllServiceRequests() {
        viewModelScope.launch {
            // Combine data from /bookings and /orders
            combine(
                repository.listenToBookings(),
                repository.listenToOrders()
            ) { bookings, orders ->
                bookings + orders.map { it.toBookingModel() }
            }.collect { allRequests ->
                _pendingBookings.value = allRequests.filter { 
                    it.status.lowercase() == "pending" || it.status.lowercase() == "placed" 
                }
                
                if (workerId.isNotEmpty()) {
                    _acceptedBookings.value = allRequests.filter { 
                        it.status.lowercase() == "accepted" && it.provider_id == workerId 
                    }
                    _completedBookings.value = allRequests.filter { 
                        it.status.lowercase() == "completed" && it.provider_id == workerId 
                    }
                }
            }
        }
    }

    private fun OrderModel.toBookingModel() = BookingModel(
        id = id,
        user_id = user_id,
        service_name = "Product Order",
        status = status,
        address = address,
        provider_id = delivery_partner_id ?: ""
    )

    fun acceptBooking(bookingId: String) {
        if (workerId.isEmpty() || bookingId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "accepted", workerId)
            _isLoading.value = false
        }
    }

    fun completeBooking(bookingId: String) {
        if (bookingId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "completed", workerId)
            _isLoading.value = false
        }
    }
}
