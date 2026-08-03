package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.Delivery
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DashboardRepository {
    private val postgrest = SupabaseClient.client.postgrest

    // --- SERVICE WORKER LOGIC ---

    @OptIn(SupabaseExperimental::class)
    fun listenToAssignedBookings(workerId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"]
            .selectAsFlow(BookingModel::id, filter = FilterOperation("provider_id", FilterOperator.EQ, workerId))
    }

    // --- SHOPKEEPER LOGIC ---

    @OptIn(SupabaseExperimental::class)
    fun listenToShopOrders(shopId: String): Flow<List<OrderModel>> {
        return postgrest["orders"]
            .selectAsFlow(OrderModel::id, filter = FilterOperation("shop_id", FilterOperator.EQ, shopId))
    }

    // --- DELIVERY LOGIC ---

    @OptIn(SupabaseExperimental::class)
    fun listenToAvailableDeliveries(): Flow<List<Delivery>> {
        return postgrest["deliveries"]
            .selectAsFlow(Delivery::id, filter = FilterOperation("status", FilterOperator.EQ, "Assigned"))
    }

    // --- AUTOMATION LINKING ---

    suspend fun linkBookingToWorker(workerId: String, bookingId: String) {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].update({
                set("provider_id", workerId)
            }) {
                filter { eq("id", bookingId) }
            }
        }
    }

    suspend fun linkOrderToShop(shopId: String, orderId: String) {
        withContext(Dispatchers.IO) {
            postgrest["orders"].update({
                set("shop_id", shopId)
            }) {
                filter { eq("id", orderId) }
            }
        }
    }
}
