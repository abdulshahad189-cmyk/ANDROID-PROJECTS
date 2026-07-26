package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PLSCategory(
    val id: String = "",
    val name: String = "",
    val icon: Int? = null,
    val subcategories: List<String> = emptyList()
)

@Serializable
data class PLSService(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val category: String = "",
    val subcategory: String = "",
    val description: String = "",
    val is_enabled: Boolean = true
)

@Serializable
data class PLSBooking(
    val id: String = "",
    val user_id: String = "",
    val user_name: String = "",
    val user_phone: String = "",
    val user_address: String = "",
    val service_id: String = "",
    val service_name: String = "",
    val category: String = "",
    val subcategory: String = "",
    val date: String = "",
    val time_slot: String = "",
    val status: String = "Pending",
    val created_at: String? = null,
    val total_price: Double = 0.0,
    val payment_method: String = "",
    val guests_count: Int? = null,
    val duration: Int? = null,
    val area_sqft: Double? = null,
    val requirements: String? = null,
    val assigned_worker_id: String = "",
    val assigned_worker_name: String = ""
)
