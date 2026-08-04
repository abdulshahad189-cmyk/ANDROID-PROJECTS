package com.nisr.sauservices.service.location

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class LiveTrackingManager(
    private val id: String, 
    private val isWorker: Boolean,
    private val map: GoogleMap,
    private var marker: Marker?
) {
    private val postgrest = SupabaseClient.client.postgrest
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @OptIn(SupabaseExperimental::class)
    fun startListening() {
        val table = if (isWorker) "worker_locations" else "delivery_locations"
        
        scope.launch {
            postgrest[table].selectAsFlow(
                LocationSnapshot::user_id, 
                filter = FilterOperation("user_id", FilterOperator.EQ, id)
            )
                .collectLatest { list ->
                    val snapshot = list.firstOrNull() ?: return@collectLatest
                    val newPos = LatLng(snapshot.latitude, snapshot.longitude)

                    marker?.let {
                        MarkerAnimator.animateMarker(it, newPos)
                    }
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 16f))
                }
        }
    }
}

@Serializable
private data class LocationSnapshot(
    val user_id: String,
    val latitude: Double,
    val longitude: Double
)
