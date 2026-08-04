
package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

// Color Palette
private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)
private val SoftGreyBgEnd = Color(0xFFECEFF1)
private val TextGrey = Color(0xFF717171)
private val TextDark = Color(0xFF1A1C1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val authState by authViewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, SoftGreyBgEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { 
                            if (step > 1) {
                                step--
                                authViewModel.resetState()
                            } else {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Back", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { 
                            if (step > 1) {
                                step--
                                authViewModel.resetState()
                            } else {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ForgotPasswordStep(1, step >= 1, step > 1)
                    Box(modifier = Modifier.width(20.dp).height(1.dp).background(TextGrey.copy(alpha = 0.3f)))
                    ForgotPasswordStep(2, step >= 2, step > 2)
                    Box(modifier = Modifier.width(20.dp).height(1.dp).background(TextGrey.copy(alpha = 0.3f)))
                    ForgotPasswordStep(3, step >= 3, step > 3)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

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
                                step = 3
                            },
                            onResend = { authViewModel.sendPasswordReset(email) }
                        )
                        3 -> StepResetPassword(
                            isLoading = authState is AuthState.Loading,
                            onContinue = { newPassword ->
                                authViewModel.updatePassword(newPassword)
                                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(Icons.Rounded.VerifiedUser, null, modifier = Modifier.size(14.dp), tint = ElegantTeal.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Secure password recovery", fontSize = 12.sp, color = TextGrey)
                }
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
            .background(if (isActive) ElegantTeal else Color.Transparent)
            .border(1.dp, if (isActive) ElegantTeal else TextGrey.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = number.toString(),
                color = if (isActive) Color.White else TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                .size(80.dp)
                .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Mail, null, tint = ElegantTeal, modifier = Modifier.size(32.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Forgot Password", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(
            "Enter your registered email and we'll send you a verification code",
            fontSize = 15.sp,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Rounded.AlternateEmail, null, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantTeal,
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    enabled = email.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = ElegantTeal),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (email.isNotBlank() && !isLoading) 
                                    Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))
                                else
                                    Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Send Verification Code", fontWeight = FontWeight.Bold, color = Color.White)
                        }
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
            modifier = Modifier
                .size(80.dp)
                .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.VpnKey, null, tint = ElegantTeal, modifier = Modifier.size(32.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Enter Code", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(
            "We sent a 6-digit code to\n$email",
            fontSize = 15.sp,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) onOtpChange(it) },
                    placeholder = { Text("6-Digit Code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onContinue,
                    enabled = otpCode.length == 6 && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = ElegantTeal),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (otpCode.length == 6 && !isLoading)
                                    Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))
                                else
                                    Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Verify Code", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Didn't receive it? Resend Code",
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().clickable { onResend() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepResetPassword(
    isLoading: Boolean,
    onContinue: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.LockReset, null, tint = ElegantTeal, modifier = Modifier.size(32.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("New Password", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(
            "Create a strong password with at least 6 characters",
            fontSize = 15.sp,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("New Password") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, null, modifier = Modifier.size(20.dp)) },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantTeal,
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, null, modifier = Modifier.size(20.dp)) },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantTeal,
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { if (newPassword == confirmPassword) onContinue(newPassword) },
                    enabled = newPassword.length >= 6 && newPassword == confirmPassword && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = ElegantTeal),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (newPassword.length >= 6 && newPassword == confirmPassword && !isLoading)
                                    Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))
                                else
                                    Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Reset Password", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
