package com.nisr.sauservices.ui.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.data.model.Order

class OrdersAdapter(
    private var orders: List<Order>,
    private val onStatusChange: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView) {
        val idText: TextView = cardView.findViewById(ID_TXT_ID)
        val nameText: TextView = cardView.findViewById(NAME_TXT_ID)
        val amountText: TextView = cardView.findViewById(AMOUNT_TXT_ID)
        val statusButton: MaterialButton = cardView.findViewById(BTN_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val context = parent.context
        val cardView = MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 8, 16, 8) }
            radius = 16f * context.resources.displayMetrics.density
            cardElevation = 8f * context.resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
            setContentPadding(32, 32, 32, 32)
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val idTxt = TextView(context).apply { id = ID_TXT_ID; textSize = 14f; setTextColor(Color.GRAY) }
        val nameTxt = TextView(context).apply { id = NAME_TXT_ID; textSize = 18f; setTextColor(Color.BLACK); setTypeface(null, android.graphics.Typeface.BOLD) }
        val amountTxt = TextView(context).apply { id = AMOUNT_TXT_ID; textSize = 16f; setTextColor(Color.DKGRAY) }
        val btn = MaterialButton(context).apply {
            id = BTN_ID
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.END; topMargin = 16 }
            cornerRadius = (24 * context.resources.displayMetrics.density).toInt()
            setBackgroundColor("#2E7D6B".toColorInt())
        }

        layout.addView(idTxt)
        layout.addView(nameTxt)
        layout.addView(amountTxt)
        layout.addView(btn)
        cardView.addView(layout)

        return OrderViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.idText.text = "Order ID: ${order.orderId}" // TODO: Use string resources
        holder.nameText.text = order.customerName
        holder.amountText.text = "Total: ${order.amount}" // TODO: Use string resources
        holder.statusButton.text = order.status

        holder.statusButton.setOnClickListener {
            val nextStatus = when (order.status) {
                "Pending" -> "Accepted"
                "Accepted" -> "Completed"
                else -> "Completed"
            }
            if (nextStatus != order.status) {
                order.status = nextStatus
                notifyItemChanged(position)
                onStatusChange(order)
            }
        }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyItemRangeChanged(0, orders.size)
    }

    companion object {
        private const val ID_TXT_ID = 1001
        private const val NAME_TXT_ID = 1002
        private const val AMOUNT_TXT_ID = 1003
        private const val BTN_ID = 1004
    }
}
