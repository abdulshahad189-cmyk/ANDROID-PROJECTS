package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.OrderModel
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RealtimeDatabaseRepository {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    fun getCurrentUserId(): String? = auth.currentUserOrNull()?.id

    suspend fun placeOrderDirectly(order: OrderModel): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        val finalOrder = order.copy(userId = userId)
        
        val inserted = withContext(Dispatchers.IO) {
            postgrest["orders"].insert(finalOrder) {
                select()
            }.decodeSingle<OrderModel>()
        }
        
        Result.success(inserted.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class)
    fun observeUserActivity(): Flow<List<OrderModel>> {
        val userId = getCurrentUserId() ?: "anonymous"
        return postgrest["orders"]
            .selectAsFlow(
                primaryKey = OrderModel::id,
                filter = FilterOperation("user_id", FilterOperator.EQ, userId)
            )
            .flowOn(Dispatchers.IO)
    }
}
