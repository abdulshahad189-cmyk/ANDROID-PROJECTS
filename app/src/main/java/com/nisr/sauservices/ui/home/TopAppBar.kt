package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarUI(navController: NavController, sessionManager: SessionManager) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var userAddress by remember { mutableStateOf("63B, Sector 63") }
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
        color = AppBackground,
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "SAU",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black,
                        lineHeight = 18.sp
                    )
                    Text(
                        "SOLUTIONS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Center: Location Selector Pill
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(50))
                    .clickable { navController.navigate(Screen.MapPicker.route) },
                color = LightBluePill
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn, 
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = userAddress,
                        fontSize = 12.sp,
                        color = Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = Black,
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
                        tint = Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = { navController.navigate(Screen.Cart.route) }, modifier = Modifier.size(36.dp)) {
                    BadgedBox(badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = PrimaryBlue) {
                                Text(cartCount.toString(), color = White, fontSize = 9.sp)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(onClick = { showLogoutDialog = true }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
