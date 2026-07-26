package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OperationResult(
    val isSuccess: Boolean,
    val message: String? = null
)

@Serializable
data class LiveLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class BookingModel(
    val id: String = "",
    val user_id: String = "",
    val service_id: String = "",
    val service_name: String = "",
    val scheduled_date: String = "",
    val scheduled_time: String = "",
    val status: String = "pending",
    val provider_id: String = "",
    val created_at: String? = null,
    val address: String = ""
) {
    val displayAddress: String get() = address.ifEmpty { "No address provided" }
    val displayDate: String get() = scheduled_date.ifEmpty { "TBD" }
    val displayTime: String get() = scheduled_time.ifEmpty { "" }
    val displayService: String get() = service_name.ifEmpty { "Service Request" }
}

@Serializable
data class OrderModel(
    val id: String = "",
    val user_id: String = "",
    val items: List<CartModel> = emptyList(),
    val total_amount: Double = 0.0,
    val address: String = "",
    val status: String = "placed",
    val delivery_partner_id: String? = null,
    val created_at: String? = null
)

@Serializable
data class SupabaseUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer",
    val status: String = "active"
) {
    val displayName: String get() = name.ifEmpty { "Anonymous User" }
    val displayEmail: String get() = email.ifEmpty { "No email provided" }
    val displayPhone: String get() = phone.ifEmpty { "No phone" }
}

@Serializable
data class ServiceModel(
    val id: String = "",
    val category_id: String = "",
    val provider_id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val image_url: String? = null
)

@Serializable
data class ProductModel(
    val id: String = "",
    val shopkeeper_id: String = "",
    val category_id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock_quantity: Int = 0,
    val image_url: String? = null
)
