package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.GrayText
import com.nisr.sauservices.ui.theme.PrimaryAccent

data class HowItWorksItem(val name: String, val icon: ImageVector)

@Composable
fun HowItWorks() {

    val list = listOf(
        HowItWorksItem("Choose Service", Icons.Outlined.EditCalendar),
        HowItWorksItem("Select Date & Time", Icons.Outlined.CalendarMonth),
        HowItWorksItem("Get Professional at Home", Icons.Outlined.CheckCircle)
    )

    Column(modifier = Modifier.padding(top = 28.dp, bottom = 28.dp)) {
        Text(
            "How It Works",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            list.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PrimaryAccent.copy(alpha = 0.08f)), // Very soft primary accent background
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon, 
                            null, 
                            tint = PrimaryAccent, 
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        item.name, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        color = GrayText
                    )
                }
            }
        }
    }
}
