package com.nisr.sauservices.ui.viewmodel

import android.app.Activity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.nisr.sauservices.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    val currentUser: FirebaseUser?
        get() = userRepository.getCurrentUser()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.uid)
                        dataResult.fold(
                            onSuccess = { AuthState.Success(user, it) },
                            onFailure = { AuthState.Error(it.message ?: "Failed to fetch user data") }
                        )
                    } else {
                        AuthState.Error("User not found")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun signInWithGoogle(credential: AuthCredential, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signInWithCredential(credential)
            _authState.value = result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        handleUserAfterSignIn(user, role)
                    } else {
                        AuthState.Error("Google Sign-In failed")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Google Sign-In failed") }
            )
        }
    }

    fun sendOtp(phoneNumber: String, activity: Activity, role: String) {
        _authState.value = AuthState.Loading
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneCredential(credential, role)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _authState.value = AuthState.Error(e.message ?: "Verification failed: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _authState.value = AuthState.OtpSent(verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(verificationId: String, otp: String, role: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneCredential(credential, role)
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signInWithCredential(credential)
            _authState.value = result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        handleUserAfterSignIn(user, role)
                    } else {
                        AuthState.Error("Phone Sign-In failed")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Phone Sign-In failed") }
            )
        }
    }

    private suspend fun handleUserAfterSignIn(user: FirebaseUser, role: String): AuthState {
        val dataResult = userRepository.getUserData(user.uid)
        val state = dataResult.fold(
            onSuccess = { existingData ->
                if (existingData == null) {
                    val newData = mutableMapOf(
                        "uid" to user.uid,
                        "userId" to user.uid,
                        "fullName" to (user.displayName ?: ""),
                        "name" to (user.displayName ?: ""),
                        "email" to (user.email ?: ""),
                        "role" to role,
                        "phoneNumber" to (user.phoneNumber ?: ""),
                        "phone" to (user.phoneNumber ?: ""),
                        "status" to "APPROVED"
                    )
                    userRepository.saveUserData(user.uid, newData)
                    AuthState.Success(user, newData)
                } else {
                    AuthState.Success(user, existingData)
                }
            },
            onFailure = { AuthState.Error(it.message ?: "Failed to fetch user data") }
        )
        _authState.value = state
        return state
    }

    fun signUp(email: String, password: String, userData: Map<String, Any>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signUp(email, password, userData)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it, userData) },
                onFailure = { AuthState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun logout() {
        userRepository.logout()
        _authState.value = AuthState.Idle
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val verificationId: String) : AuthState()
    data class Success(val user: FirebaseUser?, val userData: Map<String, Any>? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}
