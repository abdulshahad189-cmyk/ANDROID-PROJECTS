package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

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
        ServiceItem("Cleaning", R.drawable.cleaning, "home_cleaning"),
        ServiceItem("Salon", R.drawable.salon, "mens_categories")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(list) { index, item ->
            QuickServiceCard(item, index, navController)
        }
    }
}

@Composable
fun QuickServiceCard(item: ServiceItem, index: Int, navController: NavController) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120)
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 25L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + slideInVertically(initialOffsetY = { 20 }, animationSpec = tween(220))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (item.categoryId == "mens_categories") {
                            navController.navigate(Screen.MensCategories.route)
                        } else {
                            navController.navigate(Screen.ResidentialSubcategories.createRoute(item.categoryId))
                        }
                    }
                )
        ) {
            // Small neat square box for image
            Surface(
                modifier = Modifier.size(76.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner light gray background for the image
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8F9FA)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
