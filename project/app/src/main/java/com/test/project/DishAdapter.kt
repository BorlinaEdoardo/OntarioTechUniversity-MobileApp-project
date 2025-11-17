package com.test.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.Dish

class DishAdapter(
    private var dishes: List<Dish>,
    private val onItemClick: (Dish) -> Unit = {}
) : RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishImageView: ImageView = itemView.findViewById(R.id.dishImageView)
        val dishNameTextView: TextView = itemView.findViewById(R.id.dishNameTextView)
        val dishDescriptionTextView: TextView = itemView.findViewById(R.id.dishDescriptionTextView)
        val dishPriceTextView: TextView = itemView.findViewById(R.id.dishPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]

        holder.dishNameTextView.text = dish.name
        holder.dishDescriptionTextView.text = dish.description
        holder.dishPriceTextView.text = String.format("€%.2f", dish.price)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(dish)
        }

        // placeholder image, for now
    }

    override fun getItemCount() = dishes.size

    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
    }
}
