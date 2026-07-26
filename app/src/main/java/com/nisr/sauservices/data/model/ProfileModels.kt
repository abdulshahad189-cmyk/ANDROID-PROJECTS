package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profile_pic_url: String? = null,
    val role: String = "customer"
)

@Serializable
data class Address(
    val id: String = "",
    val full_name: String = "",
    val phone: String = "",
    val house_no: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val is_default: Boolean = false
)

@Serializable
data class NotificationPreferences(
    val order_updates: Boolean = true,
    val promotions: Boolean = true,
    val service_alerts: Boolean = true,
    val app_updates: Boolean = true
)
