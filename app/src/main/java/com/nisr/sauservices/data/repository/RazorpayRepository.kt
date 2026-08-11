package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.RazorpayPaymentModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RazorpayRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun savePaymentResult(
        bookingId: String,
        customerId: String,
        partnerId: String,
        amount: Double,
        razorpayPaymentId: String,
        razorpayOrderId: String,
        razorpaySignature: String
    ): Result<Unit> = try {
        val payment = RazorpayPaymentModel(
            id = UUID.randomUUID().toString(),
            bookingId = bookingId,
            customerId = customerId,
            partnerId = partnerId,
            amount = amount,
            razorpayPaymentId = razorpayPaymentId,
            razorpayOrderId = razorpayOrderId,
            razorpaySignature = razorpaySignature,
            status = "paid"
        )

        withContext(Dispatchers.IO) {
            postgrest["razorpay_payments"].insert(payment)
            
            // Update booking status
            postgrest["bookings"].update(
                update = {
                    set("payment_method", "Digital")
                    set("payment_status", "paid")
                    set("status", "completed")
                }
            ) {
                filter {
                    eq("id", bookingId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
