package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nisr.sauservices.ui.theme.LightGray
import com.nisr.sauservices.ui.theme.PrimaryBlue
import com.nisr.sauservices.ui.theme.White

data class CategoryItem(
    val name: String, 
    val route: String = "",
    val description: String = "",
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
        CategoryItem("Essential\nSupplies", Screen.EssentialSupplies.route, "Daily essentials", R.drawable.essential_supplies),
        CategoryItem("Bookings", Screen.BookingsModule.route, "All your bookings", R.drawable.bookings),
        CategoryItem("Residential\nServices", Screen.ResidentialCategories.route, "Home services", R.drawable.residential_services),
        CategoryItem("Property &\nLifestyle", "", "Premium lifestyle", R.drawable.property_lifestyle),
        CategoryItem("Home\nEssentials", "", "Home maintenance", R.drawable.home_essentials),
        CategoryItem("Food &\nBeverages", "", "Food & drinks", R.drawable.food_beverages),
        CategoryItem("Education\nServices", "", "Learn & grow", R.drawable.education_services),
        CategoryItem("Business &\nProfessional", "", "Business services", R.drawable.business_professional),
        CategoryItem("Home &\nLifestyle", "", "Lifestyle needs", R.drawable.home_lifestyle),
        CategoryItem("Tech\nServices", "", "Tech solutions", R.drawable.tech_services),
        CategoryItem("Men's\nGrooming", "", "Grooming needs", R.drawable.mens_grooming),
        CategoryItem("Women's\nBeauty", "", "Beauty services", R.drawable.womens_beauty),
        CategoryItem("Healthcare &\nPharmacy", "", "Health care", R.drawable.healthcare_pharmacy)
    )

    val homeCategories = listOf(
        allCategories[0],
        allCategories[1],
        allCategories[2],
        allCategories[3],
        allCategories[4],
        CategoryItem("More\nServices", Screen.Categories.route, "Explore more", null)
    )

    val displayList = if (showAll) allCategories else homeCategories

    Column(modifier = Modifier.padding(top = 8.dp)) {
        if (!showAll) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Categories",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black
                    )
                )
                TextButton(
                    onClick = { navController.navigate(Screen.Categories.route) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("See all", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
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
                            if (item.route.isNotEmpty()) {
                                navController.navigate(item.route)
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
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(86.dp), // Adjusted size to match reference
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
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(6.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Apps,
                                contentDescription = null,
                                tint = Color(0xFFE91E63), // Pinkish like "More Services" icon
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = item.name,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Black,
                lineHeight = 14.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
