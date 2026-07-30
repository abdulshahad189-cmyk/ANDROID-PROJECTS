package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepository {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest
    private val realtime = SupabaseClient.client.realtime

    private fun getUserId(): String {
        return auth.currentUserOrNull()?.id ?: "anonymous"
    }

    suspend fun addToCart(item: CartModel): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["cart_items"].insert(item.copy(itemId = ""))
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateQuantity(itemId: String, newQuantity: Int): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            if (newQuantity <= 0) {
                postgrest["cart_items"].delete {
                    filter { eq("id", itemId) }
                }
            } else {
                postgrest["cart_items"].update({
                    set("quantity", newQuantity)
                }) {
                    filter { eq("id", itemId) }
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class)
    fun getCartItems(): Flow<List<CartModel>> {
        return postgrest["cart_items"]
            .selectAsFlow(CartModel::itemId).map { list ->
                list.filter { it.itemId.isNotEmpty() }
            }.flowOn(Dispatchers.IO)
    }

    suspend fun removeItem(itemId: String) = withContext(Dispatchers.IO) {
        postgrest["cart_items"].delete {
            filter { eq("id", itemId) }
        }
    }

    suspend fun clearCart(): Result<Unit> = try {
        val userId = getUserId()
        withContext(Dispatchers.IO) {
            postgrest["cart_items"].delete {
                filter { eq("user_id", userId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun placeOrder(order: OrderModel): Result<String> {
        return try {
            val userId = getUserId()
            val finalOrder = order.copy(userId = userId, orderStatus = "placed")
            
            val insertedOrder = withContext(Dispatchers.IO) {
                postgrest["orders"].insert(finalOrder) {
                    select()
                }.decodeSingle<OrderModel>()
            }

            order.items.filter { it.unit == "Booking" }.forEach { cartItem ->
                val bookingData = BookingModel(
                    userId = userId,
                    serviceId = cartItem.productId,
                    serviceName = cartItem.itemName,
                    scheduleDate = cartItem.date ?: "",
                    scheduleTime = cartItem.time ?: "",
                    status = "pending",
                    address = order.address
                )
                withContext(Dispatchers.IO) {
                    postgrest["bookings"].insert(bookingData)
                }
            }

            Result.success(insertedOrder.orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String, staffId: String? = null): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["orders"].update({
                set("status", newStatus)
                if (staffId != null) set("delivery_partner_id", staffId)
            }) {
                filter { eq("id", orderId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: String, workerId: String? = null): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].update({
                set("status", newStatus)
                if (workerId != null) set("provider_id", workerId)
            }) {
                filter { eq("id", bookingId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class)
    fun getGlobalOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"]
            .selectAsFlow(OrderModel::orderId)
            .flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun getGlobalBookings(): Flow<List<BookingModel>> {
        return postgrest["bookings"]
            .selectAsFlow(BookingModel::bookingId)
            .flowOn(Dispatchers.IO)
    }
}
