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
        // Drop all tables and recreate them
        db?.execSQL("DROP TABLE IF EXISTS reviews")
        db?.execSQL("DROP TABLE IF EXISTS order_elements")
        db?.execSQL("DROP TABLE IF EXISTS orders")
        db?.execSQL("DROP TABLE IF EXISTS dishes")
        db?.execSQL("DROP TABLE IF EXISTS restaurants")
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
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
