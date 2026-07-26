package com.nisr.sauservices.location

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.nisr.sauservices.R
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        createNotificationChannel()
        startForeground(1, createNotification())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocationToSupabase(location)
                }
            }
        }
        
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            stopSelf()
        }
    }

    private fun updateLocationToSupabase(location: Location) {
        val userId = auth.currentUserOrNull()?.id ?: return
        
        serviceScope.launch {
            try {
                // Update global delivery partner location
                postgrest["delivery_locations"].upsert(
                    mapOf(
                        "user_id" to userId,
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "updated_at" to System.currentTimeMillis()
                    )
                )

                // Update orders assigned to this delivery partner
                postgrest["orders"].update({
                    set("delivery_boy_lat", location.latitude)
                    set("delivery_boy_lng", location.longitude)
                }) {
                    filter {
                        eq("delivery_partner_id", userId)
                        neq("status", "delivered")
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "location_channel",
                "Location Tracking Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Live Delivery Tracking")
            .setContentText("Your location is being shared for real-time tracking.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
