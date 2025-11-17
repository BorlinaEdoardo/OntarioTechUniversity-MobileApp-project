package com.test.project

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.Dish

class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var restaurantNameToolbar: TextView
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var dishAdapter: DishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        // Initialize views
        initViews()

        // Get restaurant name from intent
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "Restaurant Menu"
        restaurantNameToolbar.text = restaurantName

        // Setup RecyclerView
        setupRecyclerView()

        // Load menu data
        loadMenuData()

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

    private fun loadMenuData() {
        // For demonstration, create some sample dishes
        // In a real app, this would come from a database or API
        val sampleDishes = listOf(
            Dish(
                id = 1,
                name = "Margherita Pizza",
                description = "Classic pizza with tomato sauce, mozzarella cheese, and fresh basil",
                price = 12.50
            ),
            Dish(
                id = 2,
                name = "Spaghetti Carbonara",
                description = "Traditional Italian pasta with eggs, cheese, pancetta, and black pepper",
                price = 14.00
            ),
            Dish(
                id = 3,
                name = "Caesar Salad",
                description = "Fresh romaine lettuce, parmesan cheese, croutons with caesar dressing",
                price = 9.50
            ),
            Dish(
                id = 4,
                name = "Grilled Salmon",
                description = "Fresh salmon fillet grilled to perfection with lemon and herbs",
                price = 18.00
            ),
            Dish(
                id = 5,
                name = "Tiramisu",
                description = "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone",
                price = 6.50
            ),
            Dish(
                id = 6,
                name = "Bruschetta",
                description = "Grilled bread topped with fresh tomatoes, garlic, and basil",
                price = 7.00
            ),
            Dish(
                id = 7,
                name = "Risotto ai Funghi",
                description = "Creamy rice dish with mixed mushrooms and parmesan cheese",
                price = 15.50
            ),
            Dish(
                id = 8,
                name = "Gelato",
                description = "Italian ice cream available in various flavors",
                price = 4.50
            )
        )

        dishAdapter.updateDishes(sampleDishes)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }
}
