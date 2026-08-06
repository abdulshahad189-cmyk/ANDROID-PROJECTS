package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.Black
import com.nisr.sauservices.ui.theme.PrimaryAccent

@Composable
fun ValuePropositionsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryAccent.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ValueItem(Icons.Rounded.Verified, "Verified Professionals")
        ValueItem(Icons.Rounded.OfflineBolt, "Quick Booking")
        ValueItem(Icons.Rounded.Lock, "Secure Payments")
    }
}

@Composable
fun ValueItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
    }
}
