package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.OrchidPrimary
import com.nisr.sauservices.ui.theme.White
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarUI(navController: NavController, sessionManager: SessionManager) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var userAddress by remember { mutableStateOf("63B...") }
    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    sessionManager.logout()
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true } }
                    showLogoutDialog = false
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color(0xFFFFF7FA), // Match overall background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.8f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(OrchidPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "SAU",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // Center: Location Selector Pill
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(50))
                    .clickable { navController.navigate(Screen.MapPicker.route) },
                color = Color(0xFFFCE4EC).copy(alpha = 0.4f) // Very soft pink
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn, 
                        contentDescription = null,
                        tint = OrchidPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = userAddress,
                        fontSize = 12.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Right: Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { /* Bag action */ }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Outlined.LocalMall,
                        contentDescription = "Shopping Bag",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = { navController.navigate(Screen.Cart.route) }, modifier = Modifier.size(36.dp)) {
                    BadgedBox(badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = OrchidPrimary) {
                                Text(cartCount.toString(), color = White, fontSize = 9.sp)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(onClick = { showLogoutDialog = true }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
