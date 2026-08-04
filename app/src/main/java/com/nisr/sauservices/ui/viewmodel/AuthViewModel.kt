package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.SupabaseRepository
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: SupabaseRepository = SupabaseRepository()) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val auth = SupabaseClient.client.auth

    val currentUser: User?
        get() = auth.currentUserOrNull()?.let { 
            User(id = it.id, email = it.email ?: "")
        }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val uid = auth.currentUserOrNull()?.id ?: throw Exception("Login failed")
                
                repository.getUserProfile(uid).fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { 
                        _authState.value = AuthState.Error(it.message ?: "Failed to fetch user profile")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String, userData: Map<String, Any>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val uid = auth.currentUserOrNull()?.id ?: throw Exception("Signup failed")
                
                val newUser = User(
                    id = uid,
                    name = userData["name"] as? String ?: "",
                    email = email,
                    phone = userData["phone"] as? String ?: "",
                    role = userData["role"] as? String ?: "customer"
                )

                repository.registerUser(newUser).fold(
                    onSuccess = {
                        _authState.value = AuthState.Success(newUser)
                    },
                    onFailure = {
                        _authState.value = AuthState.Error(it.message ?: "Registration failed")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWith(OTP) {
                    phone = phoneNumber
                }
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send OTP")
            }
        }
    }

    fun verifyOtp(phoneNumber: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.verifyOtp(
                    type = OtpType.Phone.SMS,
                    phone = phoneNumber,
                    token = token
                )
                val uid = auth.currentUserOrNull()?.id ?: throw Exception("Verification failed")
                
                repository.getUserProfile(uid).fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { 
                        val newUser = User(
                            id = uid,
                            phone = phoneNumber,
                            role = "customer"
                        )
                        repository.registerUser(newUser).fold(
                            onSuccess = { _authState.value = AuthState.Success(newUser) },
                            onFailure = { _authState.value = AuthState.Error(it.message ?: "Failed to create user profile") }
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.resetPasswordForEmail(email)
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    fun verifyPasswordResetOtp(email: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.verifyOtp(
                    type = OtpType.Email.RECOVERY,
                    email = email,
                    token = token
                )
                // After verification, the user is logged in with a temporary session
                _authState.value = AuthState.Idle 
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Invalid reset code")
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.modifyUser(password = newPassword)
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to update password")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
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
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
