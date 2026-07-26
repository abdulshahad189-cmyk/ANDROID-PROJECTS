package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.OrderModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class OrderRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun placeOrder(order: OrderModel): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["orders"].insert(order)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getMyOrders(userId: String): Flow<List<OrderModel>> {
        return postgrest["orders"]
            .selectAsFlow(OrderModel::id) {
                filter {
                    eq("user_id", userId)
                }
            }.flowOn(Dispatchers.IO)
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["orders"].update({
                set("status", status)
            }) {
                filter { eq("id", orderId) }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
