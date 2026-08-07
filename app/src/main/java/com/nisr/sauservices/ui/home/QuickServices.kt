package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.White

data class ServiceItem(
    val name: String, 
    @DrawableRes val imageRes: Int, 
    val categoryId: String
)

@Composable
fun QuickServicesRow(navController: NavController) {

    val list = listOf(
        ServiceItem("Electrician", R.drawable.electrician, "electrician"),
        ServiceItem("Plumber", R.drawable.plumber, "plumber"),
        ServiceItem("AC Repair", R.drawable.ac_repair, "ac_repair"),
        ServiceItem("Cleaning", R.drawable.cleaning, "home_cleaning")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        list.forEach { item ->
            QuickServiceCard(item, navController)
        }
    }
}

@Composable
fun QuickServiceCard(item: ServiceItem, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable {
                navController.navigate(Screen.ResidentialSubcategories.createRoute(item.categoryId))
            }
    ) {
        Surface(
            modifier = Modifier.size(80.dp), // Matched with Category boxes
            shape = RoundedCornerShape(20.dp),
            color = White,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF9FAFB)
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = item.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Black,
                lineHeight = 14.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
