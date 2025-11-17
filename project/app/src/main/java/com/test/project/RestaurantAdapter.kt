package com.test.project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
        private val image: ImageView = itemView.findViewById(R.id.restaurantImage)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val name: TextView = itemView.findViewById(R.id.restaurantName)
        private val rating: TextView = itemView.findViewById(R.id.restaurantRating)
        private val cuisine: TextView = itemView.findViewById(R.id.restaurantCuisine)

        fun bind(restaurant: Restaurant) {
            name.text = restaurant.name
            rating.text = String.format("%.1f", restaurant.rating ?: 4.5f)
            cuisine.text = restaurant.shortDescription ?: "Restaurant"

            // Set sample image (same for all restaurants for now)
            image.setImageResource(R.drawable.sample_restaurant)

            // Handle favorite button click
            var isFavorite = false
            favoriteButton.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_outline)
                }
            }


            card.setOnClickListener {
                // TODO: Navigate to detail screen with:
                // restaurant description, video presentation, images, review section, etc.
                // implementing placeholder for now
                val context = itemView.context
                val detailIntent = Intent(context, RestaurantDetailActivity::class.java)
                detailIntent.putExtra("restaurantName", restaurant.name)
                detailIntent.putExtra("restaurantDescription", restaurant.description)
                // if needed add more extras here

                context.startActivity(detailIntent)

            }
        }
    }
}