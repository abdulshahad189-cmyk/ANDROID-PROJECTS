package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.CashPaymentModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class CashPaymentRepository {
    private val postgrest = SupabaseClient.client.postgrest

    // SAU commission percentage
    private val commissionPercentage = 10.0

    private fun generateOtp(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    suspend fun createCashPayment(
        bookingId: String,
        customerId: String,
        partnerId: String,
        amount: Double
    ): Result<String> = try {
        val paymentId = UUID.randomUUID().toString()
        val otp = generateOtp()
        val commission = amount * commissionPercentage / 100
        val partnerAmount = amount - commission

        val payment = CashPaymentModel(
            id = paymentId,
            bookingId = bookingId,
            customerId = customerId,
            partnerId = partnerId,
            amount = amount,
            commission = commission,
            partnerAmount = partnerAmount,
            status = "pending",
            otp = otp
        )

        withContext(Dispatchers.IO) {
            postgrest["cash_payments"].insert(payment)
            
            // Update booking status
            postgrest["bookings"].update(
                update = {
                    set("payment_method", "Cash")
                    set("payment_status", "pending")
                    set("cash_payment_id", paymentId)
                }
            ) {
                filter {
                    eq("id", bookingId)
                }
            }
        }
        Result.success(paymentId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun partnerCollectedCash(paymentId: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["cash_payments"].update(
                update = {
                    set("status", "cash_collected")
                    set("collected_at", "now()")
                }
            ) {
                filter {
                    eq("id", paymentId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun verifyCustomerOtp(paymentId: String, enteredOtp: String, bookingId: String): Result<Boolean> = try {
        withContext(Dispatchers.IO) {
            val payment = postgrest["cash_payments"].select {
                filter {
                    eq("id", paymentId)
                }
            }.decodeSingle<CashPaymentModel>()

            if (payment.otp == enteredOtp) {
                postgrest["cash_payments"].update(
                    update = {
                        set("status", "paid")
                        set("confirmed_at", "now()")
                    }
                ) {
                    filter {
                        eq("id", paymentId)
                    }
                }

                // Mark booking as paid and completed
                postgrest["bookings"].update(
                    update = {
                        set("payment_status", "paid")
                        set("status", "completed")
                    }
                ) {
                    filter {
                        eq("id", bookingId)
                    }
                }
                Result.success(true)
            } else {
                Result.success(false)
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
