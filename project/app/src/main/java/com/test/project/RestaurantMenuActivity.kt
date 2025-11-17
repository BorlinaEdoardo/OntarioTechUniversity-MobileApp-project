package com.test.project

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.Dish
import com.test.project.database.DatabaseHelper

class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var restaurantNameToolbar: TextView
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var dishAdapter: DishAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this, 4)

        // Initialize views
        initViews()

        // Get restaurant name from intent
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "Restaurant Menu"
        restaurantNameToolbar.text = restaurantName

        // Setup RecyclerView
        setupRecyclerView()

        // Load menu data from database
        loadMenuData(restaurantName)

        // Setup click listeners
        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        restaurantNameToolbar = findViewById(R.id.restaurantNameToolbar)
        menuRecyclerView = findViewById(R.id.menuRecyclerView)
    }

    private fun setupRecyclerView() {
        dishAdapter = DishAdapter(emptyList()) { dish ->
            // dish click - for now, just show a toast
            android.widget.Toast.makeText(
                this,
                "Selected: ${dish.name}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        menuRecyclerView.apply {
            adapter = dishAdapter
            layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)
        }
    }

    private fun loadMenuData(restaurantName: String) {
        val dishes = databaseHelper.getMenuForRestaurant(restaurantName)
        dishAdapter.updateDishes(dishes)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }
}