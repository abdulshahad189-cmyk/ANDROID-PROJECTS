package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.PhoneAndroid
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    role: String = "customer",
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState
    var phoneNumber by remember { mutableStateOf("") }
    
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "fade"
    )
    
    val contentOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "slide"
    )

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                authViewModel.signInWithGoogle(idToken, role)
            }
        } catch (e: ApiException) {
            android.util.Log.e("GOOGLE_AUTH", "Sign-in failed with code: ${e.statusCode}", e)
            Toast.makeText(context, "Google authentication failed (Code: ${e.statusCode})", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole(role)
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetState()
            }
            is AuthState.OtpSent -> {
                navController.currentBackStackEntry?.savedStateHandle?.set("phone", phoneNumber)
                navController.navigate(Screen.PhoneLogin.route)
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SAUVeryLightBlue, Color.White, SAULightBlue.copy(alpha = 0.5f))
                )
            )
    ) {
        // Decorative Background Element
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 150.dp, y = (-100).dp)
                .background(SAUPrimaryLight.copy(alpha = 0.05f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .graphicsLayer { 
                    alpha = contentAlpha
                    translationY = contentOffset.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Premium Branding Header
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, SAUBorder.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SAU SERVICES",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = SAUPrimary,
                    letterSpacing = 2.sp
                )
            )
            
            Text(
                text = "Premium Solutions at Your Fingertips",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SAUTextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Elegant Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, SAUBorder.copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Let's Get Started",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SAUPrimary,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Enter your mobile number to continue",
                        style = MaterialTheme.typography.bodySmall.copy(color = SAUTextSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Modern Phone Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) phoneNumber = it 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Phone Number", color = SAULightText) },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Text("🇮🇳", fontSize = 18.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("+91", fontWeight = FontWeight.Bold, color = SAUPrimary)
                                Spacer(Modifier.width(10.dp))
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(SAUBorder))
                            }
                        },
                        trailingIcon = {
                            if (phoneNumber.length == 10) {
                                Icon(Icons.Rounded.CheckCircle, null, tint = SAUSuccess, modifier = Modifier.size(22.dp))
                            } else {
                                Icon(Icons.Rounded.PhoneAndroid, null, tint = SAUPrimary.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SAUPrimary,
                            unfocusedBorderColor = SAUBorder,
                            cursorColor = SAUPrimary,
                            focusedContainerColor = SAUVeryLightBlue.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SAUPrimary)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gradient Action Button
                    Button(
                        onClick = { if (phoneNumber.length == 10) authViewModel.sendOtp(phoneNumber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(
                                elevation = if (phoneNumber.length == 10) 8.dp else 0.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = SAUPrimary
                            ),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        enabled = phoneNumber.length == 10 && authState !is AuthState.Loading
                    ) {
                        val gradient = if (phoneNumber.length == 10) {
                            Brush.horizontalGradient(colors = listOf(SAUPrimary, SAUPrimaryLight))
                        } else {
                            Brush.horizontalGradient(colors = listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)))
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(gradient),
                            contentAlignment = Alignment.Center
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("PROCEED", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Centered Sign Up Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New to SAU?",
                            color = SAUTextSecondary,
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = { navController.navigate(Screen.SignUp.createRoute(role)) }
                        ) {
                            Text(
                                text = "SIGN UP HERE",
                                color = SAUPrimaryLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylish Divider
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp)) {
                        Box(Modifier.weight(1f).height(1.dp).background(SAUBorder))
                        Text(
                            "  OR  ",
                            style = MaterialTheme.typography.labelSmall.copy(color = SAULightText, fontWeight = FontWeight.Bold)
                        )
                        Box(Modifier.weight(1f).height(1.dp).background(SAUBorder))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Premium Social Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .requestIdToken(context.getString(R.string.web_client_id))
                                    .build()
                                val signInClient = GoogleSignIn.getClient(context, gso)
                                googleLauncher.launch(signInClient.signInIntent)
                            },
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SAUBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SAUPrimary)
                        ) {
                            Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Google", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.EmailLogin.route) },
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SAUBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SAUPrimary)
                        ) {
                            Icon(Icons.Rounded.Email, null, modifier = Modifier.size(20.dp), tint = SAUPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Email", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_technicians),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .scale(0.9f),
                    contentScale = ContentScale.Fit,
                    alpha = 0.8f
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = buildAnnotatedString {
                        append("By continuing, you agree to our ")
                        withStyle(SpanStyle(color = SAUPrimaryLight, fontWeight = FontWeight.Bold)) { append("Terms") }
                        append(" and ")
                        withStyle(SpanStyle(color = SAUPrimaryLight, fontWeight = FontWeight.Bold)) { append("Privacy Policy") }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        textAlign = TextAlign.Center, 
                        color = SAUTextSecondary.copy(alpha = 0.8f)
                    ),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
