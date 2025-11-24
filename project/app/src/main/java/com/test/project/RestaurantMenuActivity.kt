package com.test.project

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private var currentUserId: Int? = null
    private var currentRestaurantId: Int? = null
    private var currentRestaurantName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Get current user ID
        currentUserId = getCurrentUserId()

        // Initialize views
        initViews()

        // Get restaurant name from intent
        currentRestaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "Restaurant Menu"
        restaurantNameToolbar.text = currentRestaurantName

        // Get restaurant ID from name
        val restaurant = databaseHelper.getRestaurantByName(currentRestaurantName)
        currentRestaurantId = restaurant?.id

        // Setup RecyclerView
        setupRecyclerView()

        // Load menu data from database
        loadMenuData(currentRestaurantName)

        // Setup click listeners
        setupClickListeners()
    }

    private fun getCurrentUserId(): Int? {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return null

        val userEmail = sharedPref.getString("user_email", null) ?: return null
        val user = databaseHelper.getUserByEmail(userEmail)
        return user?.id
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        restaurantNameToolbar = findViewById(R.id.restaurantNameToolbar)
        menuRecyclerView = findViewById(R.id.menuRecyclerView)
    }

    private fun setupRecyclerView() {
        dishAdapter = DishAdapter(emptyList()) { dish ->
            // Dish click - add to order
            addDishToOrder(dish)
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

    private fun addDishToOrder(dish: Dish) {
        val userId = currentUserId
        val restaurantId = currentRestaurantId
        val dishId = dish.id

        // Check if user is logged in
        if (userId == null) {
            Toast.makeText(this, "Please log in to add items to your order", Toast.LENGTH_SHORT).show()
            return
        }

        if (restaurantId == null || dishId == null) {
            Toast.makeText(this, "Error: Invalid restaurant or dish", Toast.LENGTH_SHORT).show()
            return
        }

        // Get or create order
        val orderId = databaseHelper.getOrCreateOrder(userId, restaurantId)

        if (orderId == null) {
            Toast.makeText(this, "Failed to create order", Toast.LENGTH_SHORT).show()
            return
        }

        // Add dish to order
        val success = databaseHelper.addDishToOrder(orderId, dishId)

        if (success) {
            // Show success dialog
            AlertDialog.Builder(this)
                .setTitle("Added to Order")
                .setMessage("${dish.name} has been added to your order!")
                .setPositiveButton("OK", null)
                .setNeutralButton("View Order") { _, _ ->
                    // Navigate to order detail
                    val intent = android.content.Intent(this, OrderDetailActivity::class.java)
                    intent.putExtra("ORDER_ID", orderId)
                    intent.putExtra("RESTAURANT_ID", restaurantId)
                    startActivity(intent)
                }
                .show()
        } else {
            Toast.makeText(this, "Failed to add item to order", Toast.LENGTH_SHORT).show()
        }
    }
}