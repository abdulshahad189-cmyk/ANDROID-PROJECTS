package com.nisr.sauservices.service.location

import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DeliveryLocationUpdater(private val id: String, private val isWorker: Boolean = false) {
    private val postgrest = SupabaseClient.client.postgrest
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateLocation(lat: Double, lng: Double) {
        val table = if (isWorker) "worker_locations" else "delivery_locations"
        val locationMap = mapOf(
            "user_id" to id,
            "latitude" to lat,
            "longitude" to lng,
            "updated_at" to System.currentTimeMillis()
        )
        
        scope.launch {
            try {
                postgrest[table].upsert(locationMap)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
