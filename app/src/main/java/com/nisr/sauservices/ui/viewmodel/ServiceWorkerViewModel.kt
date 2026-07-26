package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _bookings = MutableLiveData<List<Booking>>(emptyList())
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        if (workerId.isNotEmpty()) {
            observeBookings()
        }
    }

    private fun observeBookings() {
        viewModelScope.launch {
            repository.listenToWorkerBookings(workerId).collect { allBookings ->
                _pendingBookings.value = allBookings.filter { it.status == "pending" }
                val accepted = allBookings.filter { it.status == "accepted" && it.provider_id == workerId }
                _acceptedBookings.value = accepted
                _completedBookings.value = allBookings.filter { it.status == "completed" && it.provider_id == workerId }
                
                _bookings.value = allBookings.filter { it.provider_id == workerId || it.status == "pending" }.map { it.toLegacyBooking() }
            }
        }
    }

    fun acceptBooking(bookingId: String) {
        if (workerId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "accepted", workerId)
            _isLoading.value = false
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status, if (status == "accepted") workerId else null)
            _isLoading.value = false
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "completed")
            _isLoading.value = false
        }
    }

    private fun BookingModel.toLegacyBooking() = Booking(
        bookingId = id,
        customerName = "Customer",
        serviceType = service_name,
        address = address,
        timeSlot = scheduled_time,
        status = status
    )
}
