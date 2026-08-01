package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)
private val SoftGreyBgEnd = Color(0xFFECEFF1)
private val TextGrey = Color(0xFF717171)
private val TextDark = Color(0xFF1A1C1E)

@Composable
fun PhoneLoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var step by remember { mutableIntStateOf(1) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            sessionManager.saveLoginState(true)
            sessionManager.saveUserRole("customer")
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.createRoute("customer")) { inclusive = true }
            }
            authViewModel.resetState()
        } else if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            authViewModel.resetState()
        }
    }

    val animateState = remember { MutableTransitionState(false) }.apply { targetState = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, SoftGreyBgEnd)
                )
            )
    ) {
        // Decorative background
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-120).dp, y = (-120).dp)
                .clip(CircleShape)
                .background(ElegantTeal.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                IconButton(
                    onClick = { 
                        if (step > 1) step = 1 else navController.popBackStack() 
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Crossfade(
                targetState = step, 
                animationSpec = tween(500),
                label = "phoneAuthStep"
            ) { currentStep ->
                when (currentStep) {
                    1 -> PhoneEntryStep(
                        phoneNumber = phoneNumber,
                        isLoading = authState is AuthState.Loading,
                        animateState = animateState,
                        onPhoneChange = { phoneNumber = it },
                        onSendOtp = { 
                            authViewModel.sendOtp("+91$phoneNumber")
                            step = 2 
                        }
                    )
                    2 -> OtpVerificationStep(
                        phoneNumber = phoneNumber,
                        otpCode = otpCode,
                        isLoading = authState is AuthState.Loading,
                        animateState = animateState,
                        onOtpChange = { otpCode = it },
                        onVerify = {
                            authViewModel.verifyOtp("+91$phoneNumber", otpCode)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneEntryStep(
    phoneNumber: String,
    isLoading: Boolean,
    animateState: MutableTransitionState<Boolean>,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Pulse Animation for Icon
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val iconScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(iconScale)
                .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Smartphone, null, tint = ElegantTeal, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Phone Login", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(
            "Enter your mobile number to receive a 6-digit verification code",
            fontSize = 15.sp,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visibleState = animateState,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneChange,
                        placeholder = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Rounded.Phone, null, modifier = Modifier.size(20.dp)) },
                        prefix = { Text("+91 ", color = TextDark, fontWeight = FontWeight.Medium) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantTeal,
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "btnScale")

                    Button(
                        onClick = onSendOtp,
                        enabled = phoneNumber.length >= 10 && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(scale)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = ElegantTeal),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        interactionSource = interactionSource
                    ) {
                        val brush = if (phoneNumber.length >= 10 && !isLoading) 
                            Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))
                        else 
                            Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                            
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(targetState = isLoading, label = "btnContent") { loading ->
                                if (loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Get OTP", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationStep(
    phoneNumber: String,
    otpCode: String,
    isLoading: Boolean,
    animateState: MutableTransitionState<Boolean>,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.VpnKey, null, tint = ElegantTeal, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Verify Phone", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(
            "Enter the 6-digit code sent to +91 $phoneNumber",
            fontSize = 15.sp,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visibleState = animateState,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6) onOtpChange(it) },
                        placeholder = { Text("Enter OTP Code", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantTeal,
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "btnScale")

                    Button(
                        onClick = onVerify,
                        enabled = otpCode.length == 6 && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(scale)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = ElegantTeal),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        interactionSource = interactionSource
                    ) {
                        val brush = if (otpCode.length == 6 && !isLoading) 
                            Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))
                        else 
                            Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                            
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(targetState = isLoading, label = "btnContent") { loading ->
                                if (loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Verify & Sign In", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Didn't receive code? Resend",
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Resend */ }
                    )
                }
            }
        }
    }
}
