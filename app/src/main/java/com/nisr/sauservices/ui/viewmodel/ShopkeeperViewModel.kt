package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _pendingOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val pendingOrders = _pendingOrders.asStateFlow()

    private val _acceptedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val acceptedOrders = _acceptedOrders.asStateFlow()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _deliveryBoyLocation = MutableStateFlow<LatLng?>(null)
    val deliveryBoyLocation = _deliveryBoyLocation.asStateFlow()

    // LiveData for compatibility with legacy UI
    private val _orders = MutableLiveData<List<Order>>(emptyList())
    val orders: LiveData<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observeAllOrders()
    }

    private fun observeAllOrders() {
        viewModelScope.launch {
            repository.getPendingOrders().collect { list ->
                _pendingOrders.value = list
                updateLegacyOrders()
            }
        }
        viewModelScope.launch {
            repository.getAcceptedOrders().collect { list ->
                _acceptedOrders.value = list
                updateLegacyOrders()
            }
        }
        viewModelScope.launch {
            repository.getAssignedOrdersForShop().collect { list ->
                _assignedOrders.value = list
                updateLegacyOrders()
            }
        }
    }

    private fun updateLegacyOrders() {
        val allModels = _pendingOrders.value + _acceptedOrders.value + _assignedOrders.value
        _orders.value = allModels.map { it.toLegacyOrder() }
    }

    private fun OrderModel.toLegacyOrder() = Order(
        orderId = id,
        customerName = "Customer",
        items = items.map { OrderItem(it.itemName, it.quantity) },
        totalPrice = total_amount,
        status = status,
        address = address
    )

    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "accepted")
        }
    }

    fun rejectOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "rejected")
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun assignDeliveryBoy(orderId: String, deliveryBoyId: String) {
        viewModelScope.launch {
            // Update order with delivery partner id
            // Assuming repository has a way to set delivery_partner_id
            repository.updateOrderStatus(orderId, "assigned")
        }
    }

    fun trackDeliveryBoy(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.listenToDeliveryLocations().collect { locations ->
                // Logic to find specific delivery boy location
            }
        }
    }
}
