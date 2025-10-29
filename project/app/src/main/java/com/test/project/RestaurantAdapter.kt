package com.test.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.test.project.R
import com.test.project.database.Restaurant

class RestaurantAdapter : RecyclerView.Adapter<RestaurantAdapter.RestaurantVH>() {

    private val all = mutableListOf<Restaurant>()
    private val visible = mutableListOf<Restaurant>()

    fun submit(list: List<Restaurant>) {
        all.clear()
        all.addAll(list)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        visible.clear()
        if (q.isEmpty()) {
            visible.addAll(all)
        } else {
            visible.addAll(all.filter { it.name.lowercase().contains(q) })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantVH(v)
    }

    override fun getItemCount(): Int = visible.size

    override fun onBindViewHolder(holder: RestaurantVH, position: Int) {
        holder.bind(visible[position])
    }

    class RestaurantVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView as CardView
        private val name: TextView = itemView.findViewById(R.id.nameText)
        private val address: TextView = itemView.findViewById(R.id.addressText)
        private val phoneNumber: TextView = itemView.findViewById(R.id.phoneText)

        fun bind(restaurant: Restaurant) {
            name.text = restaurant.name
            address.text = restaurant.address ?: ""
            phoneNumber.text = restaurant.phoneNumber ?: ""


            card.setCardBackgroundColor(android.graphics.Color.WHITE)


            name.setTextColor(android.graphics.Color.BLACK)
            address.setTextColor(android.graphics.Color.DKGRAY)
            phoneNumber.setTextColor(android.graphics.Color.DKGRAY)
        }
    }

}
