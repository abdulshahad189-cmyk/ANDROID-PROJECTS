package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

data class ServiceItem(val name: String, val icon: ImageVector, val categoryId: String, val bgColor: Color)

@Composable
fun QuickServicesRow(navController: NavController) {

    val list = listOf(
        ServiceItem("Electrician", Icons.Rounded.ElectricBolt, "electrician", Color(0xFFE3F2FD)),
        ServiceItem("Plumber", Icons.Rounded.Construction, "plumber", Color(0xFFFFF3E0)),
        ServiceItem("AC Repair", Icons.Rounded.AcUnit, "ac_repair", Color(0xFFE8F5E9)),
        ServiceItem("Cleaning", Icons.Rounded.CleaningServices, "home_cleaning", Color(0xFFFCE4EC)),
        ServiceItem("Salon", Icons.Rounded.ContentCut, "mens_categories", Color(0xFFF3E5F5))
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(list) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(75.dp)
                    .clickable {
                        if (item.categoryId == "mens_categories") {
                            navController.navigate(Screen.MensCategories.route)
                        } else {
                            navController.navigate(Screen.ResidentialSubcategories.createRoute(item.categoryId))
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(item.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.name,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
