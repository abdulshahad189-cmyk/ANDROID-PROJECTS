package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

data class CategoryItem(
    val name: String, 
    val icon: ImageVector, 
    val route: String = "", 
    val iconColor: Color = Color(0xFFE91E63)
)

@Composable
fun CategoriesGrid(
    navController: NavController, 
    showAll: Boolean = false,
    onHomeEssentialsClick: () -> Unit = {},
    onEducationClick: () -> Unit = {},
    onBusinessClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {},
    onTechClick: () -> Unit = {},
    onMechanicClick: () -> Unit = {},
    onMobilityClick: () -> Unit = {}
) {
    val categories = listOf(
        CategoryItem("Essential\nSupplies", Icons.Rounded.ShoppingBasket, Screen.EssentialSupplies.route, Color(0xFFFFA000)),
        CategoryItem("Bookings", Icons.Rounded.EventAvailable, Screen.BookingsModule.route, Color(0xFF4CAF50)),
        CategoryItem("Mechanic\nServices", Icons.Rounded.Handyman, "", Color(0xFF2196F3)),
        CategoryItem("Mobility\nServices", Icons.Rounded.DirectionsCar, "", Color(0xFF673AB7)),
        CategoryItem("Residential\nServices", Icons.Rounded.MapsHomeWork, Screen.ResidentialCategories.route, Color(0xFF795548)),
        CategoryItem("Home &\nLifestyle", Icons.Rounded.Chair, "", Color(0xFFFF5722)),
        CategoryItem("More\nServices", Icons.Rounded.Apps, Screen.Categories.route, Color(0xFF9E9E9E))
    )

    Column(modifier = Modifier.padding(top = 16.dp)) {
        if (!showAll) {
            Text(
                "Categories",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
            )
        }

        // First Row: 4 items (Essential, Bookings, Mechanic, Mobility)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.take(4).forEach { item ->
                CategoryCard(
                    item = item, 
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (item.name.replace("\n", " ")) {
                            "Essential Supplies" -> navController.navigate(Screen.EssentialSupplies.route)
                            "Bookings" -> navController.navigate(Screen.BookingsModule.route)
                            "Mechanic Services" -> onMechanicClick()
                            "Mobility Services" -> onMobilityClick()
                        }
                    }
                )
            }
        }
        
        Spacer(Modifier.height(12.dp))

        // Second Row: 3 items (Residential, Home & Lifestyle, More Services)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.drop(4).forEach { item ->
                CategoryCard(
                    item = item, 
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (item.name.replace("\n", " ")) {
                            "Residential Services" -> navController.navigate(Screen.ResidentialCategories.route)
                            "Home & Lifestyle" -> onLifestyleClick()
                            "More Services" -> navController.navigate(Screen.Categories.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(item.iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = item.iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}
