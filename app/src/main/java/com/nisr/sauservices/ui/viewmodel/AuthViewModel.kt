package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.repository.UserRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch

// ============================================================
// AUTH VIEW MODEL
// ============================================================

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _authState =
        mutableStateOf<AuthState>(AuthState.Idle)

    val authState: State<AuthState> =
        _authState

    // ========================================================
    // CURRENT USER
    // ========================================================

    val currentUser: UserInfo?
        get() = userRepository.getCurrentUser()


    // ========================================================
    // EMAIL / PASSWORD LOGIN
    // ========================================================

    fun signIn(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val result =
                userRepository.signIn(
                    email,
                    password
                )

            if (result.isSuccess) {

                val user =
                    userRepository.getCurrentUser()

                if (user != null) {

                    val dataResult =
                        userRepository.getUserData(
                            user.id
                        )

                    _authState.value =
                        if (dataResult.isSuccess) {

                            AuthState.Success(
                                user = user,
                                userData =
                                    dataResult
                                        .getOrNull()
                                        ?.mapValues {
                                            it.value as Any
                                        }
                            )

                        } else {

                            AuthState.Error(
                                dataResult
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to fetch user data"
                            )
                        }

                } else {

                    _authState.value =
                        AuthState.Error(
                            "User not found"
                        )
                }

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Login failed"
                    )
            }
        }
    }


    // ========================================================
    // GOOGLE LOGIN
    // ========================================================

    fun signInWithGoogle(
        idToken: String,
        role: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            if (idToken.isBlank()) {

                _authState.value =
                    AuthState.Error(
                        "Google authentication token is missing"
                    )

                return@launch
            }

            val result =
                userRepository.signInWithGoogle(
                    idToken
                )

            if (result.isSuccess) {

                val user =
                    userRepository.getCurrentUser()

                if (user != null) {

                    val dataResult =
                        userRepository.getUserData(
                            user.id
                        )

                    if (
                        dataResult.isSuccess &&
                        dataResult.getOrNull() != null
                    ) {

                        _authState.value =
                            AuthState.Success(
                                user = user,
                                userData =
                                    dataResult
                                        .getOrNull()
                                        ?.mapValues {
                                            it.value as Any
                                        }
                            )

                    } else {

                        // First Google login
                        val newProfile =
                            mapOf(
                                "id" to user.id,
                                "email" to (
                                        user.email
                                            ?: ""
                                        ),
                                "role" to role
                            )

                        userRepository.saveUserData(
                            user.id,
                            newProfile
                        )

                        _authState.value =
                            AuthState.Success(
                                user = user,
                                userData = newProfile
                            )
                    }

                } else {

                    _authState.value =
                        AuthState.Error(
                            "Google Sign-In failed"
                        )
                }

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Google Sign-In failed"
                    )
            }
        }
    }


    // ========================================================
    // SEND PHONE OTP
    // ========================================================

    fun sendOtp(
        phone: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val cleanPhone =
                phone.trim()

            /*
             * User enters:
             *
             * 9133954771
             *
             * Supabase receives:
             *
             * +919133954771
             */

            val formattedPhone =
                when {

                    cleanPhone.startsWith("+") ->
                        cleanPhone

                    cleanPhone.length == 10 ->
                        "+91$cleanPhone"

                    else ->
                        cleanPhone
                }

            if (
                formattedPhone.length < 12
            ) {

                _authState.value =
                    AuthState.Error(
                        "Please enter a valid Indian mobile number"
                    )

                return@launch
            }

            val result =
                userRepository.sendOtp(
                    formattedPhone
                )

            if (result.isSuccess) {

                _authState.value =
                    AuthState.OtpSent

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to send OTP"
                    )
            }
        }
    }


    // ========================================================
    // VERIFY PHONE OTP
    // ========================================================

    fun verifyOtp(
        phone: String,
        token: String,
        role: String = "customer"
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val cleanPhone =
                phone.trim()

            val formattedPhone =
                when {

                    cleanPhone.startsWith("+") ->
                        cleanPhone

                    cleanPhone.length == 10 ->
                        "+91$cleanPhone"

                    else ->
                        cleanPhone
                }

            val cleanToken =
                token.trim()

            if (cleanToken.length < 4) {

                _authState.value =
                    AuthState.Error(
                        "Please enter a valid OTP"
                    )

                return@launch
            }

            val result =
                userRepository.verifyOtp(
                    formattedPhone,
                    cleanToken
                )

            if (result.isSuccess) {

                val user =
                    userRepository.getCurrentUser()

                if (user != null) {

                    val dataResult =
                        userRepository.getUserData(
                            user.id
                        )

                    val existingData =
                        dataResult.getOrNull()

                    if (existingData == null) {

                        // ====================================
                        // FIRST PHONE LOGIN
                        // ====================================

                        val newProfile =
                            mapOf(
                                "id" to user.id,
                                "phone" to formattedPhone,
                                "role" to role
                            )

                        userRepository.saveUserData(
                            user.id,
                            newProfile
                        )

                        _authState.value =
                            AuthState.Success(
                                user = user,
                                userData = newProfile
                            )

                    } else {

                        // ====================================
                        // EXISTING USER
                        // ====================================

                        _authState.value =
                            AuthState.Success(
                                user = user,
                                userData =
                                    existingData.mapValues {
                                        it.value as Any
                                    }
                            )
                    }

                } else {

                    _authState.value =
                        AuthState.Error(
                            "Verification succeeded but user was not found"
                        )
                }

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Invalid OTP"
                    )
            }
        }
    }


    // ========================================================
    // SIGN UP
    // ========================================================

    fun signUp(
        email: String,
        password: String,
        userData: Map<String, Any>
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val result =
                userRepository.signUp(
                    email,
                    password,
                    userData
                )

            if (result.isSuccess) {

                val user =
                    userRepository.getCurrentUser()

                _authState.value =
                    AuthState.Success(
                        user = user,
                        userData = userData
                    )

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Registration failed"
                    )
            }
        }
    }


    // ========================================================
    // PASSWORD RESET
    // ========================================================

    fun sendPasswordReset(
        email: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val result =
                userRepository.sendPasswordReset(
                    email
                )

            if (result.isSuccess) {

                _authState.value =
                    AuthState.Idle

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to send reset email"
                    )
            }
        }
    }


    // ========================================================
    // VERIFY PASSWORD RESET OTP
    // ========================================================

    fun verifyPasswordResetOtp(
        email: String,
        token: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val result =
                userRepository.verifyPasswordResetOtp(
                    email,
                    token
                )

            if (result.isSuccess) {

                _authState.value =
                    AuthState.Idle

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Invalid OTP"
                    )
            }
        }
    }


    // ========================================================
    // UPDATE PASSWORD
    // ========================================================

    fun updatePassword(
        newPassword: String
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthState.Loading

            val result =
                userRepository.updatePassword(
                    newPassword
                )

            if (result.isSuccess) {

                _authState.value =
                    AuthState.Idle

            } else {

                _authState.value =
                    AuthState.Error(
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to update password"
                    )
            }
        }
    }


    // ========================================================
    // LOGOUT
    // ========================================================

    fun logout() {

        viewModelScope.launch {

            userRepository.logout()

            _authState.value =
                AuthState.Idle
        }
    }


    // ========================================================
    // RESET STATE
    // ========================================================

    fun resetState() {

        _authState.value =
            AuthState.Idle
    }
}


// ============================================================
// AUTH STATE
// ============================================================

sealed class AuthState {

    object Idle : AuthState()

    object Loading : AuthState()

    object OtpSent : AuthState()

    data class Success(
        val user: UserInfo?,
        val userData: Map<String, Any>? = null
    ) : AuthState()

    data class Error(
        val message: String
    ) : AuthState()
}