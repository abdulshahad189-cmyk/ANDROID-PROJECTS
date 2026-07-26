package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val role: String = "customer", // shopkeeper, service_worker, delivery, customer
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val status: String = "active",
    val extraFields: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayName: String get() = name.ifEmpty { "Anonymous User" }
    val displayEmail: String get() = email.ifEmpty { "No email provided" }
    val displayPhone: String get() = phone.ifEmpty { "No phone" }
}
