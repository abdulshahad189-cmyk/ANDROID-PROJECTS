package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

@Composable
fun HeroBanner(navController: NavController) {
    val bannerBackground = Color(0xFFE3F2FD) // Light Blue background as requested

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = bannerBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ALL SERVICES,\nONE APP",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Trusted professionals\nat your doorstep",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = { navController.navigate(Screen.ResidentialCategories.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)), // Orchid Pink (#E91E63)
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        "Book a Service", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Placeholder image area on the right
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                // Future image location
            }
        }
    }
}
