package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.Delivery
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DashboardRepository {
    private val postgrest = SupabaseClient.client.postgrest

    // --- SERVICE WORKER LOGIC ---

    fun listenToAssignedBookings(workerId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"]
            .selectAsFlow(BookingModel::id) {
                filter {
                    eq("provider_id", workerId)
                }
            }.flowOn(Dispatchers.IO)
    }

    // --- SHOPKEEPER LOGIC ---

    fun listenToShopOrders(shopId: String): Flow<List<OrderModel>> {
        // Assuming orders are linked to shopkeepers via products or a direct shop_id
        // For simplicity, filtering by a metadata or status for now as per previous logic
        return postgrest["orders"]
            .selectAsFlow(OrderModel::id) {
                // In a real app, you'd filter by shop_id if you have a junction table
                // or if orders are directly assigned.
            }.flowOn(Dispatchers.IO)
    }

    // --- DELIVERY LOGIC ---

    fun listenToAvailableDeliveries(): Flow<List<Delivery>> {
        return postgrest["deliveries"]
            .selectAsFlow(Delivery::id) {
                filter {
                    eq("status", "Assigned")
                }
            }.flowOn(Dispatchers.IO)
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
        // Linking logic in Supabase usually involves updating the order with a shop_id
        withContext(Dispatchers.IO) {
            postgrest["orders"].update({
                set("shop_id", shopId)
            }) {
                filter { eq("id", orderId) }
            }
        }
    }
}
