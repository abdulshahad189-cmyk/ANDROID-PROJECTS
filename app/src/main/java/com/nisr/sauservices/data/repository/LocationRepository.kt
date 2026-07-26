package com.nisr.sauservices.data.repository

import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class LocationRepository {
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun updateUserLocation(userId: String, latLng: LatLng, role: String) {
        withContext(Dispatchers.IO) {
            postgrest["locations"].upsert(
                mapOf(
                    "id" to userId,
                    "latitude" to latLng.latitude,
                    "longitude" to latLng.longitude,
                    "role" to role,
                    "last_updated" to System.currentTimeMillis()
                )
            )
        }
    }

    fun observeUserLocation(userId: String): Flow<LatLng?> {
        return postgrest["locations"]
            .selectAsFlow(LocationData::id) {
                filter { eq("id", userId) }
            }
            .map { list ->
                list.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
            }
            .flowOn(Dispatchers.IO)
    }

    fun observeOrderTracking(orderId: String): Flow<TrackingData> {
        return postgrest["orders"]
            .selectAsFlow(OrderTrackingData::id) {
                filter { eq("id", orderId) }
            }
            .map { list ->
                val data = list.firstOrNull()
                TrackingData(
                    deliveryBoy = data?.delivery_boy_lat?.let { lat -> 
                        data.delivery_boy_lng?.let { lng -> LatLng(lat, lng) } 
                    },
                    customer = data?.customer_lat?.let { lat -> 
                        data.customer_lng?.let { lng -> LatLng(lat, lng) } 
                    },
                    shop = data?.shop_lat?.let { lat -> 
                        data.shop_lng?.let { lng -> LatLng(lat, lng) } 
                    }
                )
            }
            .flowOn(Dispatchers.IO)
    }
}

@Serializable
private data class LocationData(
    val id: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
private data class OrderTrackingData(
    val id: String,
    val delivery_boy_lat: Double? = null,
    val delivery_boy_lng: Double? = null,
    val customer_lat: Double? = null,
    val customer_lng: Double? = null,
    val shop_lat: Double? = null,
    val shop_lng: Double? = null
)

data class TrackingData(
    val deliveryBoy: LatLng? = null,
    val customer: LatLng? = null,
    val shop: LatLng? = null
)
