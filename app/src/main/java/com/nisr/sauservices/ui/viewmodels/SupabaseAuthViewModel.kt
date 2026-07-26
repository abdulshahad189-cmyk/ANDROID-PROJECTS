package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.SupabaseRepository
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {
    private val repository = SupabaseRepository()
    private val auth = SupabaseClient.client.auth

    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun register(email: String, pass: String, name: String, phone: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                }
                val uid = auth.currentUserOrNull()?.id ?: throw Exception("Signup failed")
                val newUser = User(
                    id = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    role = role
                )
                
                repository.registerUser(newUser).fold(
                    onSuccess = {
                        _userState.value = newUser
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }
                val uid = auth.currentUserOrNull()?.id ?: throw Exception("Login failed")
                
                repository.getUserProfile(uid).fold(
                    onSuccess = { user ->
                        _userState.value = user
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            _userState.value = null
        }
    }
}
