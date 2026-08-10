package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.components.SauColors
import com.nisr.sauservices.ui.components.SauButton
import com.nisr.sauservices.ui.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    navController: NavController,
    viewModel: LocationViewModel
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.centerLocation, 15f)
    }

    var searchQuery by remember { mutableStateOf("") }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                viewModel.getCurrentLocation(context)
            }
        }
    )

    // Initial permission check and location fetch
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.getCurrentLocation(context)
        }
    }

    // Sync VM center location to Camera
    LaunchedEffect(uiState.centerLocation) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.centerLocation, 15f))
    }

    // Sync Camera movement back to VM (Debounced inside VM)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.updateCenterLocation(cameraPositionState.position.target, context)
        }
    }

    // Handle Errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Delivery Location", fontWeight = FontWeight.Bold, color = SauColors.TextDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SauColors.TextDark)
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
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
            )

            // Center Pin (Stationary)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin",
                        tint = SauColors.Primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp)) // Visual offset to align pin tip with center
                }
            }

            // Search Bar (Floating)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for area, street name...", color = SauColors.TextGrey) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = SauColors.Primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.searchLocation(searchQuery, context)
                            }) {
                                Icon(Icons.Default.MyLocation, null, tint = SauColors.Primary)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.searchLocation(searchQuery, context)
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = SauColors.TextDark
                    ),
                    singleLine = true
                )
            }

            // My Location FAB
            FloatingActionButton(
                onClick = { 
                    if (hasLocationPermission) viewModel.getCurrentLocation(context) 
                    else launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 260.dp, end = 16.dp),
                containerColor = Color.White,
                contentColor = SauColors.Primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            // Bottom Confirmation Sheet
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 24.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "CONFIRM DELIVERY LOCATION", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = SauColors.TextGrey,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SauColors.Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn, 
                                contentDescription = null, 
                                tint = SauColors.Primary, 
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (uiState.isFetchingAddress) "Locating..." else uiState.landmark.ifEmpty { "Pinned Location" }, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontSize = 18.sp,
                                color = SauColors.TextDark
                            )
                            Text(
                                text = if (uiState.isFetchingAddress) "Updating address..." else uiState.address, 
                                color = SauColors.TextGrey, 
                                fontSize = 14.sp, 
                                maxLines = 2,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    SauButton(
                        text = "Confirm Location",
                        isLoading = uiState.isFetchingAddress,
                        onClick = { 
                            viewModel.confirmLocation { 
                                navController.popBackStack() 
                            } 
                        }
                    )
                }
            }
        }
    }
}
