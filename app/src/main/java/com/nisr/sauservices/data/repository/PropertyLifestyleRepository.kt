package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.data.model.PLSService
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PropertyLifestyleRepository {
    private val postgrest = SupabaseClient.client.postgrest

    // --- SERVICES ---

    @OptIn(SupabaseExperimental::class)
    fun getServices(subcategory: String? = null): Flow<List<PLSService>> {
        val filter = if (subcategory != null) FilterOperation("subcategory", FilterOperator.EQ, subcategory) else null
        return postgrest["pls_services"]
            .selectAsFlow(PLSService::id, filter = filter).flowOn(Dispatchers.IO)
    }

    suspend fun addService(service: PLSService): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["pls_services"].insert(service)
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateService(service: PLSService): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["pls_services"].update(service) {
                filter { eq("id", service.id) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- BOOKINGS ---

    suspend fun placeBooking(booking: PLSBooking): Result<String> = try {
        val inserted = withContext(Dispatchers.IO) {
            postgrest["pls_bookings"].insert(booking) {
                select()
            }.decodeSingle<PLSBooking>()
        }
        Result.success(inserted.id)
    } catch (e: Exception) { Result.failure(e) }

    @OptIn(SupabaseExperimental::class)
    fun getBookings(userId: String? = null): Flow<List<PLSBooking>> {
        val filter = if (userId != null) FilterOperation("user_id", FilterOperator.EQ, userId) else null
        return postgrest["pls_bookings"]
            .selectAsFlow(PLSBooking::id, filter = filter).flowOn(Dispatchers.IO)
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["pls_bookings"].update({
                set("status", status)
            }) {
                filter { eq("id", bookingId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
