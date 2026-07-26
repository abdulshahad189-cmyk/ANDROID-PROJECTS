package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.ServiceModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getAllServices(): Result<List<ServiceModel>> = try {
        val services = withContext(Dispatchers.IO) {
            postgrest["services"].select().decodeList<ServiceModel>()
        }
        Result.success(services)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getServicesByCategory(categoryId: String): Result<List<ServiceModel>> = try {
        val services = withContext(Dispatchers.IO) {
            postgrest["services"].select {
                filter {
                    eq("category_id", categoryId)
                }
            }.decodeList<ServiceModel>()
        }
        Result.success(services)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addService(service: ServiceModel): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["services"].insert(service)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
