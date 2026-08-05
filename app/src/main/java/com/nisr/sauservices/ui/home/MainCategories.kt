package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        CategoryItem("Mechanic\nServices", "", R.drawable.mechanic_services),
        CategoryItem("Mobility\nServices", "", R.drawable.mobility_services),
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
        CategoryItem("Healthcare &\nPharmacy", "", R.drawable.healthcare_pharmacy)
    )

    val homeCategories = listOf(
        allCategories[0],
        allCategories[1],
        allCategories[2],
        allCategories[3],
        allCategories[4],
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
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )
        }

        val rows = displayList.chunked(3)
        
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                // Fill empty slots in the last row if needed
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f)

    Card(
        modifier = modifier
            .height(140.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (item.imageRes != null) Arrangement.SpaceBetween else Arrangement.Center
        ) {
            if (item.imageRes != null) {
                // Standardized Image using helper composable
                CategoryImage(
                    imageRes = item.imageRes,
                    title = item.name
                )
            }
            
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                color = Color.Black,
                maxLines = 2,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun CategoryImage(@DrawableRes imageRes: Int, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp) // Occupies ~65% of 140dp height
            .padding(8.dp) // White padding around the image
            .clip(RoundedCornerShape(14.dp)), // Rounded corners for image
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Premium Blinkit style crop
        )
    }
}
