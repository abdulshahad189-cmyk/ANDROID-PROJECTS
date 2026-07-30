package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OperationResult(
    val isSuccess: Boolean,
    val message: String? = null
)

@Serializable
data class LiveLocation(
    @SerialName("lat") val latitude: Double = 0.0,
    @SerialName("lng") val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class BookingModel(
    @SerialName("id") val bookingId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("service_id") val serviceId: String = "",
    @SerialName("service_name") val serviceName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("scheduled_date") val scheduleDate: String = "",
    @SerialName("scheduled_time") val scheduleTime: String = "",
    val status: String = "pending",
    @SerialName("provider_id") val providerId: String = "",
    @SerialName("created_at") val timestamp: String? = null,
    val address: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "Cash"
) {
    // Aliases for compatibility with repository logic using snake_case
    val id: String get() = bookingId
    val user_id: String get() = userId
    val service_id: String get() = serviceId
    val service_name: String get() = serviceName
    val scheduled_date: String get() = scheduleDate
    val scheduled_time: String get() = scheduleTime
    val provider_id: String get() = providerId
    val created_at: String? get() = timestamp

    val displayAddress: String get() = address.ifEmpty { "No address provided" }
    val displayDate: String get() = scheduleDate.ifEmpty { "TBD" }
    val displayTime: String get() = scheduleTime.ifEmpty { "" }
    val displayService: String get() = serviceName.ifEmpty { "Service Request" }
}

@Serializable
data class OrderModel(
    @SerialName("id") val orderId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("shop_id") val shopId: String? = null,
    val items: List<CartModel> = emptyList(),
    @SerialName("total_amount") val totalPrice: Double = 0.0,
    val address: String = "",
    @SerialName("status") val orderStatus: String = "placed",
    @SerialName("delivery_partner_id") val deliveryPartnerId: String? = null,
    @SerialName("created_at") val timestamp: String? = null
) {
    // Aliases for compatibility
    val id: String get() = orderId
    val user_id: String get() = userId
    val shop_id: String? get() = shopId
    val status: String get() = orderStatus
    val total_amount: Double get() = totalPrice
    val delivery_partner_id: String? get() = deliveryPartnerId
    val assignedDeliveryBoy: String? get() = deliveryPartnerId
}

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
