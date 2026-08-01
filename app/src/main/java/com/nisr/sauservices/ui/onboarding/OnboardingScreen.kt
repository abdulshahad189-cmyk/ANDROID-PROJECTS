package com.nisr.sauservices.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val items = OnboardingItem.items
    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Decorative Elements
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = -300f
                        translationX = 200f
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .clip(CircleShape)
                        .background(ElegantTeal.copy(alpha = 0.05f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { 
                            navController.navigate(Screen.Login.createRoute("customer")) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    ) {
                        Text(
                            text = "Skip",
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Pager with Professional Transitions
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    val item = items[page]
                    
                    // Page transformation animation
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                // Parallax effect
                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                val scale = lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleX = scale
                                scaleY = scale
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(32.dp))
                                .background(ElegantTeal.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Professional Illustration Placeholder
                            Icon(
                                painter = painterResource(id = item.image),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.7f),
                                tint = ElegantTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Text(
                            text = item.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1E),
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = item.description,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 24.sp
                        )
                    }
                }

                // Footer with Animations
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnboardingIndicator(
                        count = items.size,
                        currentPage = pagerState.currentPage,
                        targetPage = pagerState.targetPage,
                        offset = pagerState.currentPageOffsetFraction
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.96f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "buttonScale"
                    )

                    Button(
                        onClick = {
                            if (pagerState.currentPage < items.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                navController.navigate(Screen.Login.createRoute("customer")) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .scale(scale)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = ElegantTeal.copy(alpha = 0.4f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        interactionSource = interactionSource
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ElegantTeal, ElegantTealDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AnimatedContent(
                                    targetState = pagerState.currentPage == items.size - 1,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) + slideInVertically { it } togetherWith
                                        fadeOut(animationSpec = tween(300)) + slideOutVertically { -it }
                                    },
                                    label = "buttonText"
                                ) { isLastPage ->
                                    Text(
                                        text = if (isLastPage) "Get Started" else "Continue",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowForward,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingIndicator(
    count: Int,
    currentPage: Int,
    targetPage: Int,
    offset: Float
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "indicatorWidth"
            )
            
            val color by animateColorAsState(
                targetValue = if (isSelected) ElegantTeal else Color.LightGray.copy(alpha = 0.5f),
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
