package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.BookingModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BookingRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun createBooking(booking: BookingModel): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].insert(booking)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getMyBookings(userId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"]
            .selectAsFlow(BookingModel::id) {
                filter {
                    eq("user_id", userId)
                }
            }.flowOn(Dispatchers.IO)
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].update({
                set("status", status)
            }) {
                filter { eq("id", bookingId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
