package com.nisr.sauservices.data.repository

import android.util.Log
import com.nisr.sauservices.data.api.SupabaseConfig
import com.nisr.sauservices.data.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SupabaseRepository {
    private val client = SupabaseConfig.client

    fun getCurrentUserId(): String? = client.auth.currentSessionOrNull()?.user?.id

    // --- GENERIC LISTENERS (Using Realtime) ---

    fun listenToOrders(): Flow<List<OrderModel>> {
        val channel = client.channel("public:orders")
        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "orders"
        }.map { 
            // In a real app, you might want to fetch the full list or update local state
            getAllOrders() 
        }
    }

    suspend fun getAllOrders(): List<OrderModel> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["orders"].select().decodeList<OrderModel>()
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error fetching orders", e)
            emptyList()
        }
    }

    fun listenToBookings(): Flow<List<BookingModel>> {
        val channel = client.channel("public:bookings")
        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "bookings"
        }.map { getAllBookings() }
    }

    suspend fun getAllBookings(): List<BookingModel> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["bookings"].select().decodeList<BookingModel>()
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error fetching bookings", e)
            emptyList()
        }
    }

    // --- ACTIONS ---

    suspend fun bookService(booking: BookingModel): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = client.postgrest["bookings"].insert(booking) {
                select()
            }.decodeSingle<BookingModel>()
            Result.success(response.bookingId)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun placeOrder(order: OrderModel): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = client.postgrest["orders"].insert(order) {
                select()
            }.decodeSingle<OrderModel>()
            Result.success(response.orderId)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["orders"].update({
                set("orderStatus", status)
            }) {
                filter { eq("id", orderId) }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserProfile(userId: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val user = client.postgrest["users"].select {
                filter { eq("id", userId) }
            }.decodeSingle<FirebaseUser>()
            Result.success(user)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerUser(user: FirebaseUser): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["users"].upsert(user)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
