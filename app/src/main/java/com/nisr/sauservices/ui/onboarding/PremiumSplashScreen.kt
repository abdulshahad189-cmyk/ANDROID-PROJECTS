package com.nisr.sauservices.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.R
import kotlinx.coroutines.delay

private val SAUPink = Color(0xFFFF1E5E)
private val DeepBlack = Color(0xFF000000)

@Composable
fun PremiumSplashScreen(onAnimationFinished: () -> Unit) {
    val logoScale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        delay(1500)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo in Pink Box as per Image
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale.value)
                    .alpha(alpha.value)
                    .clip(RoundedCornerShape(32.dp))
                    .background(SAUPink),
                contentAlignment = Alignment.Center
            ) {
                // Assuming R.drawable.sau_logo is the white text logo
                Image(
                    painter = painterResource(id = R.drawable.sau_logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "All Services, One App",
                color = SAUPink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(alpha.value)
            )
        }

        Text(
            text = "Loading...",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .alpha(alpha.value)
        )
    }
}
