package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.GrayText
import com.nisr.sauservices.ui.theme.LightGreen
import com.nisr.sauservices.ui.theme.SuccessGreen

@Composable
fun OfferBanner(){
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LightGreen)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            Icons.Outlined.CardGiftcard, 
            null, 
            tint = SuccessGreen, 
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(20.dp))

        Column{
            Text(
                "Flat 20% OFF on First Booking", 
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, 
                    fontSize = 15.sp,
                    color = Black
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Use Code: SAU20", 
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp, 
                    color = GrayText
                )
            )
        }
    }
}
