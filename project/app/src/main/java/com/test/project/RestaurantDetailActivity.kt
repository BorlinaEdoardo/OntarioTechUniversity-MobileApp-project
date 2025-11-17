package com.test.project

import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RestaurantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Initialize views
        val restaurantNameTextView: TextView = findViewById(R.id.restaurantNameTextView)
        val viewMenuButton: Button = findViewById(R.id.viewMenuButton)

        // Get restaurant name
        val restaurantName = intent.getStringExtra("restaurantName") ?: "Restaurant"
        restaurantNameTextView.text = restaurantName

        // Set up button click listener
        viewMenuButton.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java)
            intent.putExtra("RESTAURANT_NAME", restaurantName)
            startActivity(intent)
        }
    }
}