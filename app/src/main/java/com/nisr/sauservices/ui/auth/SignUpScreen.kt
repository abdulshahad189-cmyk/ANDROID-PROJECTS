package com.nisr.sauservices.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

private val SAUBlue = Color(0xFF2563EB)
private val Background = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderLight = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, role: String = "customer", authViewModel: AuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                authViewModel.signInWithGoogle(idToken, role)
            }
        } catch (e: ApiException) {
            Log.e("GOOGLE_AUTH", "Sign-up failed with code: ${e.statusCode}", e)
            Toast.makeText(context, "Google authentication failed (Code: ${e.statusCode})", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            sessionManager.saveLoginState(true)
            sessionManager.saveUserRole("customer")
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Join SAU", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background glow effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SAUBlue.copy(alpha = 0.08f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // App Logo - Improved Visibility
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.sau_logo),
                            contentDescription = "SAU Logo",
                            modifier = Modifier.size(54.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "SAU SERVICES",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Create your account for better experience",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Registration Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Register Profile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextDark),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        AnimatedVisibility(
                            visible = authState is AuthState.Error,
                            enter = expandVertically() + fadeIn()
                        ) {
                            Text(
                                text = (authState as? AuthState.Error)?.message ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        SignUpField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Full Name",
                            icon = Icons.Rounded.Person,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SignUpField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = "Phone Number",
                            icon = Icons.Rounded.Smartphone,
                            keyboardType = KeyboardType.Phone,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SignUpField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Email Address",
                            icon = Icons.Rounded.AlternateEmail,
                            keyboardType = KeyboardType.Email,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password") },
                            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp), tint = SAUBlue) },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(image, "Toggle visibility", modifier = Modifier.size(20.dp))
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = authState !is AuthState.Loading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SAUBlue,
                                unfocusedBorderColor = BorderLight,
                                cursorColor = SAUBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Create Account Button
                        Button(
                            onClick = { 
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    val userData = mapOf<String, Any>(
                                        "fullName" to fullName,
                                        "phoneNumber" to phoneNumber,
                                        "email" to email,
                                        "role" to "customer"
                                    )
                                    authViewModel.signUp(email, password, userData)
                                } else {
                                    Toast.makeText(context, "Email and Password are required", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = authState !is AuthState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SAUBlue)
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("SIGN UP / REGISTER", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // OR CONTINUE WITH
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                    Text(
                        " OR CONTINUE WITH ",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", color = TextDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            // This button should go back to the Phone Input screen (SignInScreen)
                            navController.navigate(Screen.Login.createRoute(role)) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Smartphone, contentDescription = null, modifier = Modifier.size(20.dp), tint = SAUBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phone OTP", color = TextDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", color = TextSecondary, fontSize = 15.sp)
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("SIGN IN", color = SAUBlue, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun SignUpField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = SAUBlue) },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SAUBlue,
            unfocusedBorderColor = BorderLight,
            cursorColor = SAUBlue
        )
    )
}
