package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.*
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.eq
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

open class SupabaseRepository {
    private val auth = SupabaseClient.client.auth
    protected val postgrest = SupabaseClient.client.postgrest

    fun getCurrentUserId(): String? = auth.currentUserOrNull()?.id

    // --- GENERIC LISTENERS ---

    @OptIn(SupabaseExperimental::class)
    fun listenToOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToBookings(): Flow<List<BookingModel>> {
        return postgrest["bookings"].selectAsFlow(BookingModel::id).flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToUsers(): Flow<List<User>> {
        return postgrest["users"].selectAsFlow(User::id).flowOn(Dispatchers.IO)
    }

    // --- SPECIFIC LISTENERS ---

    @OptIn(SupabaseExperimental::class)
    fun observeMyBookings(role: String, userId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"].selectAsFlow(BookingModel::id).map { list ->
            if (role == "customer") list.filter { it.user_id == userId } 
            else list.filter { it.provider_id == userId }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToCustomerOrders(customerId: String): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.user_id == customerId }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToDeliveryBoyOrders(deliveryBoyId: String): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.delivery_partner_id == deliveryBoyId }
        }.flowOn(Dispatchers.IO)
    }
    
    fun observeAssignedOrders(deliveryBoyId: String): Flow<List<OrderModel>> = listenToDeliveryBoyOrders(deliveryBoyId)

    @OptIn(SupabaseExperimental::class)
    fun listenToAvailableOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.status == "accepted" }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun observeAvailableBookings(type: String): Flow<List<BookingModel>> {
        return postgrest["bookings"].selectAsFlow(BookingModel::id).map { list ->
            list.filter { it.status == "pending" }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToWorkerBookings(workerId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"].selectAsFlow(BookingModel::id).map { list ->
            list.filter { it.provider_id == workerId }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun getPendingOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.status == "placed" }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun getAcceptedOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.status == "accepted" }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun getAssignedOrdersForShop(shopId: String? = null): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.filter { it.status == "assigned" || it.status == "out_for_delivery" }
        }.flowOn(Dispatchers.IO)
    }

    fun listenToAllBookings(): Flow<List<BookingModel>> = listenToBookings()
    fun listenToAllOrders(): Flow<List<OrderModel>> = listenToOrders()
    fun listenToAllUsers(): Flow<List<User>> = listenToUsers()
    fun listenToShopkeeperOrders(): Flow<List<OrderModel>> = listenToOrders()

    @OptIn(SupabaseExperimental::class)
    fun listenToDeliveryLocations(): Flow<List<LiveLocation>> {
        return postgrest["delivery_locations"].selectAsFlow(LiveLocation::timestamp).flowOn(Dispatchers.IO)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToCustomerOrder(orderId: String): Flow<OrderModel?> {
        return postgrest["orders"].selectAsFlow(OrderModel::id).map { list ->
            list.firstOrNull { it.id == orderId }
        }.flowOn(Dispatchers.IO)
    }

    // --- ACTIONS ---

    suspend fun bookService(booking: BookingModel): Result<String> = try {
        val inserted = withContext(Dispatchers.IO) {
            postgrest["bookings"].insert(booking) { select() }.decodeSingle<BookingModel>()
        }
        Result.success(inserted.id)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun placeOrder(order: OrderModel): Result<String> = try {
        val inserted = withContext(Dispatchers.IO) {
            postgrest["orders"].insert(order) { select() }.decodeSingle<OrderModel>()
        }
        Result.success(inserted.id)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["orders"].update({ set("status", status) }) {
                filter { eq("id", orderId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].update({
                set("status", status)
                if (workerId != null) set("provider_id", workerId)
            }) {
                filter { eq("id", bookingId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun registerUser(user: User): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["users"].upsert(user)
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getUserProfile(userId: String): Result<User> = try {
        val user = withContext(Dispatchers.IO) {
            postgrest["users"].select {
                filter { eq("id", userId) }
            }.decodeSingle<User>()
        }
        Result.success(user)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteUser(userId: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["users"].delete {
                filter { eq("id", userId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getDeliveryBoys(): List<User> = try {
        withContext(Dispatchers.IO) {
            postgrest["users"].select {
                filter { eq("role", "delivery") }
            }.decodeList<User>()
        }
    } catch (e: Exception) { emptyList() }
}
