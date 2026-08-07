package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Share
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
        color = White,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo & Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        "SAU",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Black,
                            lineHeight = 17.sp
                        )
                    )
                    Text(
                        "Solutions",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = GrayText,
                            lineHeight = 9.sp
                        )
                    )
                }
            }

            // Center: Location Selector Pill
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { navController.navigate(Screen.MapPicker.route) },
                color = PrimaryLight.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
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
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 10.sp,
                            color = Black,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Right: Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { /* Bag action */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.LocalMall,
                        contentDescription = "Bag",
                        tint = Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = { navController.navigate(Screen.Cart.route) }, modifier = Modifier.size(32.dp)) {
                    BadgedBox(badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = PrimaryBlue) {
                                Text(cartCount.toString(), color = White, fontSize = 8.sp)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                IconButton(onClick = { /* Share action */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
