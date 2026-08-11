package com.nisr.sauservices.ui.payment

import com.razorpay.PaymentData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class PaymentEvent {
    data class Success(val paymentId: String?, val data: PaymentData?) : PaymentEvent()
    data class Error(val code: Int, val description: String?, val data: PaymentData?) : PaymentEvent()
}

object PaymentResultBus {
    private val _events = MutableSharedFlow<PaymentEvent>()
    val events = _events.asSharedFlow()

    suspend fun post(event: PaymentEvent) {
        _events.emit(event)
    }
}
