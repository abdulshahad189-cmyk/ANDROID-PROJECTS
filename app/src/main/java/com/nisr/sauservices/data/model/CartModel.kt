package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartModel(
    val itemId: String = "", 
    val productId: String = "",
    val itemName: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val quantity: Int = 1,
    val category: String = "",
    val subcategory: String = "",
    val totalPrice: Double = 0.0,
    val date: String? = null,
    val time: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
