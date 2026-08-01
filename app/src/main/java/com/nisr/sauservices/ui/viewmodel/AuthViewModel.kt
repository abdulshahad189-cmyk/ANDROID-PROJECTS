package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.repository.UserRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    val currentUser: UserInfo?
        get() = userRepository.getCurrentUser()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = {
                    val user = userRepository.getCurrentUser()
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.id)
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

    fun signUp(email: String, password: String, userData: Map<String, Any>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signUp(email, password, userData)
            _authState.value = result.fold(
                onSuccess = {
                    val user = userRepository.getCurrentUser()
                    AuthState.Success(user, userData)
                },
                onFailure = { AuthState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun signInWithGoogle(idToken: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signInWithGoogle(idToken)
            _authState.value = result.fold(
                onSuccess = {
                    val user = userRepository.getCurrentUser()
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.id)
                        dataResult.fold(
                            onSuccess = { AuthState.Success(user, it) },
                            onFailure = { 
                                // If user data doesn't exist, it might be first time sign in
                                AuthState.Success(user, mapOf("role" to role))
                            }
                        )
                    } else {
                        AuthState.Error("User not found")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Google Sign In failed") }
            )
        }
    }

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.sendOtp(phone)
            _authState.value = result.fold(
                onSuccess = { AuthState.Idle },
                onFailure = { AuthState.Error(it.message ?: "Failed to send OTP") }
            )
        }
    }

    fun verifyOtp(phone: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.verifyOtp(phone, token)
            _authState.value = result.fold(
                onSuccess = {
                    val user = userRepository.getCurrentUser()
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.id)
                        dataResult.fold(
                            onSuccess = { AuthState.Success(user, it?.mapValues { entry -> entry.value as Any }) },
                            onFailure = { AuthState.Success(user, null) }
                        )
                    } else {
                        AuthState.Error("Verification failed")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Invalid OTP") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _authState.value = AuthState.Idle
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserInfo?, val userData: Map<String, Any>? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}
