package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.Address
import com.nisr.sauservices.data.model.NotificationPreferences
import com.nisr.sauservices.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUserProfile()
        fetchAddresses()
        fetchNotificationPreferences()
    }

    fun fetchUserProfile() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = withContext(Dispatchers.IO) {
                    postgrest["users"].select {
                        filter { eq("id", uid) }
                    }.decodeSingleOrNull<UserProfile>()
                }
                _userProfile.value = profile ?: UserProfile(name = "Guest User", email = "guest@example.com")
            } catch (e: Exception) {
                _userProfile.value = UserProfile(name = "Guest User", email = "guest@example.com")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    postgrest["users"].update({
                        set("name", name)
                        set("phone", phone)
                    }) {
                        filter { eq("id", uid) }
                    }
                }
                fetchUserProfile()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAddresses() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    postgrest["addresses"].select {
                        filter { eq("user_id", uid) }
                    }.decodeList<Address>()
                }
                _addresses.value = list
            } catch (e: Exception) { }
        }
    }

    fun addAddress(address: Address) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["addresses"].insert(address.copy(id = ""))
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["addresses"].delete {
                        filter { eq("id", addressId) }
                    }
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun setDefaultAddress(addressId: String) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // First set all to false
                    postgrest["addresses"].update({
                        set("is_default", false)
                    }) {
                        filter { eq("user_id", uid) }
                    }
                    // Then set specific to true
                    postgrest["addresses"].update({
                        set("is_default", true)
                    }) {
                        filter { eq("id", addressId) }
                    }
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun fetchNotificationPreferences() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val prefs = withContext(Dispatchers.IO) {
                    postgrest["notification_preferences"].select {
                        filter { eq("user_id", uid) }
                    }.decodeSingleOrNull<NotificationPreferences>()
                }
                if (prefs != null) {
                    _notificationPrefs.value = prefs
                }
            } catch (e: Exception) { }
        }
    }

    fun updateNotificationPref(key: String, value: Boolean) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["notification_preferences"].upsert(mapOf(
                        "user_id" to uid,
                        key to value
                    ))
                }
                fetchNotificationPreferences()
            } catch (e: Exception) { }
        }
    }

    fun submitSupportMessage(subject: String, message: String) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "user_id" to uid,
                    "subject" to subject,
                    "message" to message
                )
                withContext(Dispatchers.IO) {
                    postgrest["support_messages"].insert(data)
                }
            } catch (e: Exception) { }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}
