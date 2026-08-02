package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OperationResult(
    val isSuccess: Boolean,
    val message: String? = null
)

@Serializable
data class BookingModel(
    val bookingId: String = "",
    val customerId: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val status: String = "pending", // pending, accepted, completed
    val workerId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val category: String = "",
    val subcategory: String = ""
) {
    val displayAddress: String get() = address.ifEmpty { location }.ifEmpty { "No address provided" }
    val displayDate: String get() = scheduledDate.ifEmpty { date }.ifEmpty { "TBD" }
    val displayTime: String get() = scheduledTime.ifEmpty { time }.ifEmpty { "" }
    val displayService: String get() = serviceName.ifEmpty { subcategory }.ifEmpty { category }.ifEmpty { "Service Request" }
}

@Serializable
data class OrderModel(
    val orderId: String = "",
    val customerId: String = "",
    val items: List<CartModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val customerLocation: LiveLocation = LiveLocation(),
    val paymentStatus: String = "pending",
    val orderStatus: String = "pending",
    val assignedDeliveryBoy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val liveLocation: LiveLocation = LiveLocation(),
    val serviceName: String = "",
    val category: String = "",
    val subcategory: String = "",
    val scheduleDate: String? = null,
    val scheduleTime: String? = null,
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = ""
) {
    val displayStatus: String get() = orderStatus.ifEmpty { status }.ifEmpty { "pending" }
    val displayAddress: String get() = address.ifEmpty { "Address not specified" }
}

@Serializable
data class LiveLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class FirebaseUser(
    val userId: String = "",
    val uid: String = "",
    val name: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val phoneNumber: String = "",
    val role: String = "customer",
    val status: String = "active"
) {
    val displayName: String get() = name.ifEmpty { fullName }.ifEmpty { "Anonymous User" }
    val displayEmail: String get() = email.ifEmpty { "No email provided" }
    val displayPhone: String get() = phone.ifEmpty { phoneNumber }.ifEmpty { "No phone" }
}
