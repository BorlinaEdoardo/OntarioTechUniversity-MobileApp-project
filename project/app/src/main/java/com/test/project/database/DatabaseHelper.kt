package com.test.project.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context,
    DATABASE_VERSION: Int,
) : SQLiteOpenHelper(
    context,
    "project.db",
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                passwordHash TEXT NOT NULL,
                salt TEXT NOT NULL
            )
        """)

        // Create Restaurants table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS restaurants (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                phoneNumber TEXT NOT NULL,
                description TEXT,
                shortDescription TEXT,
                rating REAL
            )
        """)

        // Create Dishes table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS dishes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                price REAL NOT NULL,
                imageUrl TEXT
            )
        """)

        // Create Orders table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS orders (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                restaurantId INTEGER NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY (restaurantId) REFERENCES restaurants(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """)

        // Create OrderElements table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS order_elements (
                orderId INTEGER NOT NULL,
                dishId INTEGER NOT NULL,
                PRIMARY KEY (orderId, dishId),
                FOREIGN KEY (orderId) REFERENCES orders(orderId) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY (dishId) REFERENCES dishes(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """)

        // Create Reviews table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS reviews (
                userId INTEGER NOT NULL,
                restaurantId INTEGER NOT NULL,
                rating REAL NOT NULL,
                comment TEXT,
                PRIMARY KEY (userId, restaurantId),
                FOREIGN KEY (userId) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY (restaurantId) REFERENCES restaurants(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """)

        // Create Menu Elements table
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS menu_elements (
                restaurantId INTEGER NOT NULL,
                dishId INTEGER NOT NULL,
                PRIMARY KEY (restaurantId, dishId),
                FOREIGN KEY (restaurantId) REFERENCES restaurants(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY (dishId) REFERENCES dishes(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        insertPremadeRestaurants(db)
        // For major version changes, drop and recreate
        db?.execSQL("DROP TABLE IF EXISTS menu_elements")
        db?.execSQL("DROP TABLE IF EXISTS reviews")
        db?.execSQL("DROP TABLE IF EXISTS order_elements")
        db?.execSQL("DROP TABLE IF EXISTS orders")
        db?.execSQL("DROP TABLE IF EXISTS dishes")
        db?.execSQL("DROP TABLE IF EXISTS restaurants")
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)

        insertPremadeRestaurants(db)
    }

    // example restaurants
    private fun insertPremadeRestaurants(db: SQLiteDatabase?) {
        val restaurants = listOf(
            Restaurant(
                name = "Symposium Cafe Restaurant & Lounge",
                description = "A vibrant restaurant and lounge offering contemporary Canadian cuisine with international influences. Known for their extensive menu featuring everything from pasta and steaks to fresh salads and creative desserts. The atmosphere is warm and welcoming, perfect for both casual dining and special occasions.",
                shortDescription = "Contemporary Canadian cuisine",
                rating = 4.2f,
                address = "1300 King St E, Oshawa, ON L1H 8J4",
                phoneNumber = "(905) 436-3354"
            ),
            Restaurant(
                name = "The Keg Oshawa",
                description = "A classic steakhouse chain known for their premium steaks, fresh seafood, and signature caesar salads. The Keg offers a sophisticated dining experience with their dark wood decor and comfortable atmosphere. Their menu features AAA grade steaks, lobster, and an extensive wine selection.",
                shortDescription = "Premium steakhouse",
                rating = 4.4f,
                address = "1200 Thornton Rd N, Oshawa, ON L1H 7K4",
                phoneNumber = "(905) 433-3700"
            ),
            Restaurant(
                name = "Fazio's Italian Restaurant",
                description = "Family-owned authentic Italian restaurant serving traditional recipes passed down through generations. Features homemade pasta, wood-fired pizza, and classic Italian entrees. The cozy atmosphere and friendly service make it feel like dining at an Italian family's home.",
                shortDescription = "Authentic Italian family dining",
                rating = 4.6f,
                address = "1668 Simcoe St N, Oshawa, ON L1G 4X6",
                phoneNumber = "(905) 436-3287"
            ),
            Restaurant(
                name = "Sushi Masa Japanese Restaurant",
                description = "Fresh and authentic Japanese cuisine featuring expertly crafted sushi, sashimi, and traditional Japanese dishes. The chef uses only the finest ingredients to create beautiful presentations. The minimalist decor creates a peaceful dining environment perfect for enjoying the artistry of Japanese cuisine.",
                shortDescription = "Fresh authentic Japanese sushi",
                rating = 4.5f,
                address = "1300 King St E Unit 3, Oshawa, ON L1H 8J4",
                phoneNumber = "(905) 240-0888"
            ),
            Restaurant(
                name = "Oshawa House Restaurant",
                description = "A local landmark serving comfort food and traditional favorites for over 30 years. Known for their generous portions, friendly service, and classic diner atmosphere. Popular for breakfast all day, hearty burgers, and homestyle dinners that remind you of home cooking.",
                shortDescription = "Classic comfort food diner",
                rating = 4.1f,
                address = "1425 King St E, Oshawa, ON L1H 8J6",
                phoneNumber = "(905) 579-4449"
            )
        )

        restaurants.forEach { restaurant ->
            db?.insert("restaurants", null, restaurant.toContentValues())
        }
    }

    /// Restaurants Table Methods

    // Get all restaurants
    fun getAllRestaurants(): List<Restaurant> {
        val restaurants = mutableListOf<Restaurant>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM restaurants", null)
        while (cursor.moveToNext()) {
            val restaurant: Restaurant = Restaurant.getFromCursor(cursor)
            restaurants.add(restaurant)
        }
        cursor.close()
        return restaurants
    }

    // insert a restaurant
    fun insertRestaurant(restaurant: Restaurant){
        val db = writableDatabase
        db.insert("restaurants", null, restaurant.toContentValues())
    }
}
