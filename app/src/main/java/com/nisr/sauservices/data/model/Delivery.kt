package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Delivery(
    val id: String = "", // Changed from deliveryId to id for Supabase consistency
    val customer_name: String = "",
    val pickup_address: String = "",
    val drop_address: String = "",
    val distance: String = "",
    var status: String = "", // Assigned, Accepted, Picked, On The Way, Delivered
    val cart_added_time: String = "3:15 PM",
    val items: String = "2 Items",
    val otp: String = "7193",
    val payment_mode: String = "Prepaid",
    val pickup_shop: String = "FreshMart Express",
    val eta: String = "15 min",
    val customer_phone: String = "+91 9876543210",
    val order_amount: String = "₹370"
)
