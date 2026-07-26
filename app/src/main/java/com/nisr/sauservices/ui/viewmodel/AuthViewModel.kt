package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.UserRepository
import io.github.jan.supabase.gotrue.user.UserInfo
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
            result.fold(
                onSuccess = {
                    val user = userRepository.getCurrentUser()
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.id)
                        _authState.value = dataResult.fold(
                            onSuccess = { userData ->
                                AuthState.Success(user, userData)
                            },
                            onFailure = { AuthState.Error(it.message ?: "Failed to fetch user data") }
                        )
                    } else {
                        _authState.value = AuthState.Error("User session not found")
                    }
                },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    // Google Sign-In with Supabase typically uses OAuth or native ID Token exchange
    // For now, refactoring to keep the signature similar but using Supabase
    fun signInWithGoogle(idToken: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Supabase Auth ID Token exchange implementation would go here
            // userRepository.signInWithIdToken(idToken) ...
            _authState.value = AuthState.Error("Google Sign-In needs Supabase OAuth configuration")
        }
    }

    fun signUp(email: String, password: String, userData: User) {
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
    data class Success(val user: UserInfo?, val userData: User? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}
