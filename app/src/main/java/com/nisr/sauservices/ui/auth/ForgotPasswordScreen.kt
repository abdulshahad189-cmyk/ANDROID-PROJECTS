package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private val SAUPink = Color(0xFFFF1E5E)
private val PremiumDark = Color(0xFF121212)
private val CardWhite = Color.White
private val TextGrey = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val authState by authViewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.ResetPassword.createRoute(email)) {
                popUpTo(Screen.ForgotPassword.route) { inclusive = true }
            }
        }
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumDark)
    ) {
        // Subtle Pink Glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .background(SAUPink.copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with Navigation & Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { 
                        if (step > 1) {
                            step--
                            authViewModel.resetState()
                        } else {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }

                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ForgotPasswordStep(1, step >= 1, step > 1)
                    Box(modifier = Modifier.width(20.dp).height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                    ForgotPasswordStep(2, step >= 2, step > 2)
                    Box(modifier = Modifier.width(20.dp).height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                    ForgotPasswordStep(3, step >= 3, step > 3)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Crossfade(targetState = step, label = "stepTransition") { currentStep ->
                    when (currentStep) {
                        1 -> StepEmailEntry(
                            email = email,
                            onEmailChange = { email = it },
                            isLoading = authState is AuthState.Loading,
                            onContinue = { 
                                authViewModel.sendPasswordReset(email)
                                step = 2 
                            }
                        )
                        2 -> StepOtpVerification(
                            email = email,
                            otpCode = otpCode,
                            onOtpChange = { otpCode = it },
                            isLoading = authState is AuthState.Loading,
                            onContinue = { 
                                authViewModel.verifyPasswordResetOtp(email, otpCode)
                            },
                            onResend = { authViewModel.sendPasswordReset(email) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp)
            ) {
                Icon(Icons.Rounded.VerifiedUser, null, modifier = Modifier.size(14.dp), tint = SAUPink)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Secure password recovery", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun ForgotPasswordStep(number: Int, isActive: Boolean, isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isActive) SAUPink else Color.Transparent)
            .border(1.dp, if (isActive) SAUPink else Color.White.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = number.toString(),
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StepEmailEntry(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onContinue: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(SAUPink.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Mail, null, tint = SAUPink, modifier = Modifier.size(36.dp))
            }

            // Floating Sparkles
            FloatingIcon(Icons.Rounded.AutoAwesome, Modifier.align(Alignment.TopStart).offset(0.dp, 0.dp), 200)
            FloatingIcon(Icons.Rounded.Shield, Modifier.align(Alignment.BottomEnd).offset(0.dp, 0.dp), 500)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Recovery", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text(
            "Enter your email to reset password",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CardWhite
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("Email Address", color = TextGrey) },
                    leadingIcon = { Icon(Icons.Rounded.AlternateEmail, null, tint = SAUPink, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SAUPink,
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        cursorColor = SAUPink
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    enabled = email.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SAUPink)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("SEND CODE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StepOtpVerification(
    email: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isLoading: Boolean,
    onContinue: () -> Unit,
    onResend: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(SAUPink.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.VpnKey, null, tint = SAUPink, modifier = Modifier.size(36.dp))
            }
            FloatingIcon(Icons.Rounded.Lock, Modifier.align(Alignment.TopEnd).offset(0.dp, 0.dp), 300)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Verify", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text(
            "Code sent to $email",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CardWhite
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = onOtpChange,
                    placeholder = { Text("6-Digit Code", color = TextGrey) },
                    leadingIcon = { Icon(Icons.Rounded.VpnKey, null, tint = SAUPink, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SAUPink,
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        cursorColor = SAUPink
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    enabled = otpCode.length >= 6 && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SAUPink)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("VERIFY", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }

                TextButton(
                    onClick = onResend,
                    enabled = !isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                ) {
                    Text("Didn't receive code? Resend", color = SAUPink, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun FloatingIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yTranslation"
    )

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = SAUPink.copy(alpha = 0.3f),
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                this.translationY = translationY
            }
    )
}
