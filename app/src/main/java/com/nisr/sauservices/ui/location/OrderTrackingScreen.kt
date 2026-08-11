package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.ElectricBike
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.components.SauColors
import com.nisr.sauservices.ui.viewmodel.TrackingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String,
    viewModel: TrackingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.partnerLocation, 15f)
    }

    val partnerMarkerState = rememberMarkerState(position = uiState.partnerLocation)

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.values.any { it }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Initialize tracking logic
    LaunchedEffect(orderId) {
        viewModel.startTracking(orderId)
    }

    // Sync marker and camera when partner moves
    LaunchedEffect(uiState.partnerLocation) {
        partnerMarkerState.position = uiState.partnerLocation
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(uiState.partnerLocation))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Track Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SauColors.TextDark)
                        Text("#${orderId.takeLast(6).uppercase()}", fontSize = 12.sp, color = SauColors.TextGrey)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SauColors.TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.partnerLocation, 16f))
                        }
                    }) {
                        Icon(Icons.Rounded.MyLocation, null, tint = SauColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                // Moving Partner Marker
                Marker(
                    state = partnerMarkerState,
                    title = "Delivery Partner",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Destination Marker
                uiState.destinationLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Delivery Point",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }

            // Professional Tracking Status Card
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {
                    val isCompleted = uiState.isCompleted
                    
                    Text(
                        text = uiState.statusTitle,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = if (isCompleted) SauColors.PrimaryDark else SauColors.TextDark
                    )
                    Text(
                        text = uiState.statusSubtitle,
                        fontSize = 15.sp,
                        color = SauColors.TextGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Themed Progress Bar
                    LinearProgressIndicator(
                        progress = { uiState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = SauColors.Primary,
                        trackColor = SauColors.Primary.copy(alpha = 0.1f),
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.ElectricBike,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = SauColors.Primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Live Tracking Active",
                                fontSize = 14.sp,
                                color = SauColors.Primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Order Delivered Successfully",
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
