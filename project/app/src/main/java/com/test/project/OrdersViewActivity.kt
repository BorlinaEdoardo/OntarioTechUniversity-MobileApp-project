package com.test.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper
import com.test.project.database.Order

class OrdersViewActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private var currentUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_view)

        // Initialize database
        databaseHelper = DatabaseHelper(this)

        // Get current user ID
        currentUserId = getCurrentUserId()

        // Initialize views
        val backButton: ImageButton = findViewById(R.id.backButton)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        // Setup RecyclerView
        setupRecyclerView()

        // Load orders
        loadOrders()

        // Back button
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getCurrentUserId(): Int? {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return null

        val userEmail = sharedPref.getString("user_email", null) ?: return null
        val user = databaseHelper.getUserByEmail(userEmail)
        return user?.id
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(mutableListOf()) { order ->
            // Navigate to order detail
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("ORDER_ID", order.orderId)
            intent.putExtra("RESTAURANT_ID", order.restaurantId)
            startActivity(intent)
        }

        ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrdersViewActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        val userId = currentUserId ?: return

        val orders = databaseHelper.getOrdersForUser(userId)

        if (orders.isEmpty()) {
            ordersRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            ordersRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            orderAdapter.updateOrders(orders)
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}
