package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.nisr.sauservices.ui.theme.OrchidPrimary

data class CategoryItem(
    val name: String, 
    val route: String = "",
    @DrawableRes val imageRes: Int? = null
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
    val allCategories = listOf(
        CategoryItem("Essential\nSupplies", Screen.EssentialSupplies.route, R.drawable.essential_supplies),
        CategoryItem("Bookings", Screen.BookingsModule.route, R.drawable.bookings),
        CategoryItem("Residential\nServices", Screen.ResidentialCategories.route, R.drawable.residential_services),
        CategoryItem("Property &\nLifestyle", "", R.drawable.property_lifestyle),
        CategoryItem("Home\nEssentials", "", R.drawable.home_essentials),
        CategoryItem("Food &\nBeverages", "", R.drawable.food_beverages),
        CategoryItem("Education\nServices", "", R.drawable.education_services),
        CategoryItem("Business &\nProfessional", "", R.drawable.business_professional),
        CategoryItem("Home &\nLifestyle", "", R.drawable.home_lifestyle),
        CategoryItem("Tech\nServices", "", R.drawable.tech_services),
        CategoryItem("Men's\nGrooming", "", R.drawable.mens_grooming),
        CategoryItem("Women's\nBeauty", "", R.drawable.womens_beauty),
        CategoryItem("Healthcare &\nPharmacy", "", R.drawable.healthcare_pharmacy),
        CategoryItem("Mechanic\nServices", "", R.drawable.mechanic_services),
        CategoryItem("Mobility\nServices", "", R.drawable.mobility_services)
    )

    val homeCategories = listOf(
        allCategories[0], // Essential Supplies
        allCategories[1], // Bookings
        allCategories[2], // Residential Services
        allCategories[3], // Property & Lifestyle
        allCategories[4], // Home Essentials
        CategoryItem("More\nServices", Screen.Categories.route, null)
    )

    val displayList = if (showAll) allCategories else homeCategories

    Column(modifier = Modifier.padding(top = 8.dp)) {
        if (!showAll) {
            Text(
                "Categories",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp, start = 4.dp)
            )
        }

        val rows = displayList.chunked(3)
        
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    CategoryCard(
                        item = item, 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (item.name.replace("\n", " ")) {
                                "Essential Supplies" -> navController.navigate(Screen.EssentialSupplies.route)
                                "Bookings" -> navController.navigate(Screen.BookingsModule.route)
                                "Mechanic Services" -> onMechanicClick()
                                "Mobility Services" -> onMobilityClick()
                                "Residential Services" -> navController.navigate(Screen.ResidentialCategories.route)
                                "More Services" -> navController.navigate(Screen.Categories.route)
                                "Property & Lifestyle" -> onLifestyleClick()
                                "Home Essentials" -> onHomeEssentialsClick()
                                "Food & Beverages" -> { /* Navigate to Food */ }
                                "Education Services" -> onEducationClick()
                                "Business & Professional" -> onBusinessClick()
                                "Home & Lifestyle" -> onLifestyleClick()
                                "Tech Services" -> onTechClick()
                                "Men's Grooming" -> { /* Navigate to Mens */ }
                                "Women's Beauty" -> { /* Navigate to Womens */ }
                                "Healthcare & Pharmacy" -> { /* Navigate to Healthcare */ }
                            }
                        }
                    )
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(24.dp)) // Increased spacing between rows
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    // Removed the outer Card (big box) and using Column as the container
    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Removed default ripple to keep it clean
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Small neat square box for image/icon
        Box(
            modifier = Modifier
                .size(72.dp) // Neater square box size
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White) // White box background
                .padding(4.dp), // Padding inside the white box
            contentAlignment = Alignment.Center
        ) {
            // Inner soft shadow/light background effect for the image area
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9F9F9), // Light background for image container
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Apps,
                            contentDescription = item.name,
                            tint = OrchidPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(Modifier.height(10.dp))
        
        Text(
            text = item.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
