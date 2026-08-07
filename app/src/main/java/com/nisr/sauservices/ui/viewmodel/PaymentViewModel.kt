package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.repository.CashPaymentRepository
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val repository = CashPaymentRepository()

    var isLoading by mutableStateOf(false)
        private set

    var paymentError by mutableStateOf<String?>(null)
        private set

    var cashPaymentId by mutableStateOf<String?>(null)
        private set

    var isOtpVerified by mutableStateOf(false)
        private set

    fun createCashPayment(bookingId: String, customerId: String, partnerId: String, amount: Double, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = repository.createCashPayment(bookingId, customerId, partnerId, amount)
            result.onSuccess { id ->
                cashPaymentId = id
                onSuccess(id)
            }.onFailure {
                paymentError = it.message ?: "Failed to create cash payment"
            }
            isLoading = false
        }
    }

    fun markCashCollected(paymentId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = repository.partnerCollectedCash(paymentId)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                paymentError = it.message ?: "Failed to mark cash as collected"
            }
            isLoading = false
        }
    }

    fun verifyOtp(paymentId: String, otp: String, bookingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = repository.verifyCustomerOtp(paymentId, otp, bookingId)
            result.onSuccess { verified ->
                if (verified) {
                    isOtpVerified = true
                    onSuccess()
                } else {
                    paymentError = "Invalid OTP"
                }
            }.onFailure {
                paymentError = it.message ?: "Failed to verify OTP"
            }
            isLoading = false
        }
    }
    
    fun clearError() {
        paymentError = null
    }
}
