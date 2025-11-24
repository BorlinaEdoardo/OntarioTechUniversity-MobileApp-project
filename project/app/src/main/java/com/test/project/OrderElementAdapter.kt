package com.test.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.Dish

class OrderElementAdapter(
    private val dishes: MutableList<Dish>,
    private val onDeleteClick: (Dish, Int) -> Unit
) : RecyclerView.Adapter<OrderElementAdapter.OrderElementViewHolder>() {

    fun updateDishes(newDishes: List<Dish>) {
        dishes.clear()
        dishes.addAll(newDishes)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        dishes.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderElementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_element, parent, false)
        return OrderElementViewHolder(view)
    }

    override fun getItemCount(): Int = dishes.size

    override fun onBindViewHolder(holder: OrderElementViewHolder, position: Int) {
        holder.bind(dishes[position], position)
    }

    inner class OrderElementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dishName: TextView = itemView.findViewById(R.id.dishNameText)
        private val dishDescription: TextView = itemView.findViewById(R.id.dishDescriptionText)
        private val dishPrice: TextView = itemView.findViewById(R.id.dishPriceText)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteItemButton)

        fun bind(dish: Dish, position: Int) {
            dishName.text = dish.name
            dishDescription.text = dish.description
            dishPrice.text = String.format("€%.2f", dish.price)

            deleteButton.setOnClickListener {
                onDeleteClick(dish, position)
            }
        }
    }
}

