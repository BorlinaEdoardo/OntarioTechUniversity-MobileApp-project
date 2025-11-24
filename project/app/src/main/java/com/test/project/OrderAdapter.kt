package com.test.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper
import com.test.project.database.Order

class OrderAdapter(
    private val orders: MutableList<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private lateinit var databaseHelper: DatabaseHelper

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        databaseHelper = DatabaseHelper(parent.context)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView as CardView
        private val restaurantName: TextView = itemView.findViewById(R.id.orderRestaurantName)
        private val orderId: TextView = itemView.findViewById(R.id.orderIdText)
        private val itemsCount: TextView = itemView.findViewById(R.id.orderItemsCount)
        private val totalPrice: TextView = itemView.findViewById(R.id.orderTotalPrice)

        fun bind(order: Order) {
            val orderIdValue = order.orderId ?: 0
            val restaurantIdValue = order.restaurantId

            // Get restaurant name
            val restaurant = databaseHelper.getRestaurantById(restaurantIdValue)
            restaurantName.text = restaurant?.name ?: "Restaurant"

            // Order ID
            orderId.text = "#$orderIdValue"

            // Items count
            val count = databaseHelper.getOrderItemsCount(orderIdValue)
            itemsCount.text = if (count == 1) "1 item" else "$count items"

            // Total price
            val total = databaseHelper.getOrderTotal(orderIdValue)
            totalPrice.text = String.format("Total: €%.2f", total)

            card.setOnClickListener {
                onOrderClick(order)
            }
        }
    }
}

