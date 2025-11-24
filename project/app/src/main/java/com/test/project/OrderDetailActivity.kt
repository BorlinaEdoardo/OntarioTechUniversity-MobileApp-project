package com.test.project

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper
import com.test.project.database.Dish

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderElementAdapter: OrderElementAdapter
    private lateinit var orderItemsRecyclerView: RecyclerView
    private lateinit var orderTotalText: TextView
    private var orderId: Int? = null
    private var restaurantId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Initialize database
        databaseHelper = DatabaseHelper(this)

        // Get order info from intent
        orderId = intent.getIntExtra("ORDER_ID", -1)
        restaurantId = intent.getIntExtra("RESTAURANT_ID", -1)

        // Initialize views
        val backButton: ImageButton = findViewById(R.id.backButton)
        val orderTitleText: TextView = findViewById(R.id.orderTitleText)
        val restaurantNameText: TextView = findViewById(R.id.restaurantNameText)
        val orderIdDetailText: TextView = findViewById(R.id.orderIdDetailText)
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        orderTotalText = findViewById(R.id.orderTotalText)

        // Set order info
        orderIdDetailText.text = "Order #$orderId"

        // Get restaurant name from DB
        val restaurant = databaseHelper.getRestaurantById(restaurantId!!)
        restaurantNameText.text = restaurant?.name ?: "Restaurant"

        // Setup RecyclerView
        setupRecyclerView()

        // Load order items
        loadOrderItems()

        // Back button
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderElementAdapter = OrderElementAdapter(mutableListOf()) { dish, position ->
            showDeleteConfirmDialog(dish, position)
        }

        orderItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = orderElementAdapter
        }
    }

    private fun loadOrderItems() {
        val orderIdValue = orderId ?: return

        val dishes = databaseHelper.getOrderItems(orderIdValue)

        orderElementAdapter.updateDishes(dishes)
        updateTotal(dishes)
    }

    private fun showDeleteConfirmDialog(dish: Dish, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Remove Item")
            .setMessage("Remove ${dish.name} from your order?")
            .setPositiveButton("Remove") { _, _ ->
                deleteOrderItem(dish, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteOrderItem(dish: Dish, position: Int) {
        val orderIdValue = orderId ?: return
        val dishId = dish.id ?: return

        val success = databaseHelper.removeDishFromOrder(orderIdValue, dishId)

        if (success) {
            orderElementAdapter.removeItem(position)
            Toast.makeText(this, "${dish.name} removed", Toast.LENGTH_SHORT).show()

            // Reload to update total
            loadOrderItems()
        } else {
            Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTotal(dishes: List<Dish>) {
        val total = dishes.sumOf { it.price }
        orderTotalText.text = String.format("€%.2f", total)
    }

    override fun onResume() {
        super.onResume()
        loadOrderItems()
    }
}

