package com.test.project.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper(
    context: Context,
) : SQLiteOpenHelper(
    context,
    "project.db",
    null,
    7
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
                rating REAL,
                videoUri TEXT,
                image1Uri TEXT,
                image2Uri TEXT
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

        // insert premade data
        insertPremadeUsers(db)
        insertPremadeRestaurants(db)
        insertPremadeDishes(db)
        insertPremadeReviews(db)
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


    // example dishes
    private  fun insertPremadeDishes(db: SQLiteDatabase?) {
        val dishes = listOf(
            Dish(
                name = "Margherita Pizza",
                description = "Classic pizza with tomato sauce, mozzarella cheese, and fresh basil",
                price = 12.50
            ),
            Dish(
                name = "Spaghetti Carbonara",
                description = "Traditional Italian pasta with eggs, cheese, pancetta, and black pepper",
                price = 14.00
            ),
            Dish(
                name = "Caesar Salad",
                description = "Fresh romaine lettuce, parmesan cheese, croutons with caesar dressing",
                price = 9.50
            ),
            Dish(
                name = "Grilled Salmon",
                description = "Fresh salmon fillet grilled to perfection with lemon and herbs",
                price = 18.00
            ),
            Dish(
                name = "Tiramisu",
                description = "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone",
                price = 6.50
            ),
            Dish(
                name = "Bruschetta",
                description = "Grilled bread topped with fresh tomatoes, garlic, and basil",
                price = 7.00
            ),
            Dish(
                name = "Risotto ai Funghi",
                description = "Creamy rice dish with mixed mushrooms and parmesan cheese",
                price = 15.50
            ),
            Dish(
                name = "Gelato",
                description = "Italian ice cream available in various flavors",
                price = 4.50
            )
        )

        dishes.forEach { dish ->
            db?.insert("dishes", null, dish.toContentValues())
        }

        // link dishes to restaurants in menu_elements table
        val menuElements = listOf(
            Pair(1, 1), // Spaghetti Carbonara to Symposium Cafe
            Pair(1, 2), // Margherita Pizza to Symposium Cafe
            Pair(2, 3),  // Caesar Salad to The Keg Oshawa
            Pair(3, 4), // Grilled Salmon to Fazio's Italian Restaurant
            Pair(3, 5), // Tiramisu to Fazio's Italian Restaurant
            Pair(4, 6), // Bruschetta to Sushi Masa Japanese Restaurant
            Pair(5, 7), // Risotto ai Funghi to Oshawa House Restaurant
            Pair(5, 8)  // Gelato to Oshawa House Restaurant
        )

        menuElements.forEach { (restaurantId, dishId) ->
            val contentValues = android.content.ContentValues().apply {
                put("restaurantId", restaurantId)
                put("dishId", dishId)
            }
            db?.insert("menu_elements", null, contentValues)
        }
    }

    // example restaurants
    private fun insertPremadeRestaurants(db: SQLiteDatabase?) {
        val restaurants = listOf(
            Restaurant(
                name = "Symposium Cafe Restaurant & Lounge",
                description = "A vibrant restaurant and lounge offering contemporary Canadian cuisine with international influences. Known for their extensive menu featuring everything from pasta and steaks to fresh salads and creative desserts. The atmosphere is warm and welcoming, perfect for both casual dining and special occasions.",
                shortDescription = "Contemporary Canadian cuisine",
                rating = 0f,
                address = "1300 King St E, Oshawa, ON L1H 8J4",
                phoneNumber = "(905) 436-3354"
            ),
            Restaurant(
                name = "The Keg Oshawa",
                description = "A classic steakhouse chain known for their premium steaks, fresh seafood, and signature caesar salads. The Keg offers a sophisticated dining experience with their dark wood decor and comfortable atmosphere. Their menu features AAA grade steaks, lobster, and an extensive wine selection.",
                shortDescription = "Premium steakhouse",
                rating = 0f, // Will be calculated from reviews
                address = "1200 Thornton Rd N, Oshawa, ON L1H 7K4",
                phoneNumber = "(905) 433-3700"
            ),
            Restaurant(
                name = "Fazio's Italian Restaurant",
                description = "Family-owned authentic Italian restaurant serving traditional recipes passed down through generations. Features homemade pasta, wood-fired pizza, and classic Italian entrees. The cozy atmosphere and friendly service make it feel like dining at an Italian family's home.",
                shortDescription = "Authentic Italian family dining",
                rating = 0f, // Will be calculated from reviews
                address = "1668 Simcoe St N, Oshawa, ON L1G 4X6",
                phoneNumber = "(905) 436-3287"
            ),
            Restaurant(
                name = "Sushi Masa Japanese Restaurant",
                description = "Fresh and authentic Japanese cuisine featuring expertly crafted sushi, sashimi, and traditional Japanese dishes. The chef uses only the finest ingredients to create beautiful presentations. The minimalist decor creates a peaceful dining environment perfect for enjoying the artistry of Japanese cuisine.",
                shortDescription = "Fresh authentic Japanese sushi",
                rating = 0f, // Will be calculated from reviews
                address = "1300 King St E Unit 3, Oshawa, ON L1H 8J4",
                phoneNumber = "(905) 240-0888"
            ),
            Restaurant(
                name = "Oshawa House Restaurant",
                description = "A local landmark serving comfort food and traditional favorites for over 30 years. Known for their generous portions, friendly service, and classic diner atmosphere. Popular for breakfast all day, hearty burgers, and homestyle dinners that remind you of home cooking.",
                shortDescription = "Classic comfort food diner",
                rating = 0f, // Will be calculated from reviews
                address = "1425 King St E, Oshawa, ON L1H 8J6",
                phoneNumber = "(905) 579-4449"
            )
        )

        restaurants.forEach { restaurant ->
            db?.insert("restaurants", null, restaurant.toContentValues())
        }
    }

    // insert premade users
    private fun insertPremadeUsers(db: SQLiteDatabase?) {
        if (db == null) return
        
        // Insert users directly during database creation to avoid recursion
        val users = listOf(
            Triple("Edoardo", "edoardo.borlina@gmail.com", "password"),
            Triple("Bralyn", "bralynlp@gmail.com", "password123"),
            Triple("User", "user.example@email.com", "password")
        )
        
        users.forEach { (name, email, password) ->
            val salt = BCrypt.gensalt()
            val passwordHash = BCrypt.hashpw(password, salt)
            
            val values = android.content.ContentValues().apply {
                put("name", name)
                put("email", email)
                put("passwordHash", passwordHash)
                put("salt", salt)
            }
            
            db.insert("users", null, values)
        }
    }

    // insert premade reviews
    private fun insertPremadeReviews(db: SQLiteDatabase?) {
        if (db == null) return

        // Example reviews from different users
        val reviews = listOf(
            // Reviews for Symposium Cafe (restaurantId = 1)
            Review(userId = 1, restaurantId = 1, rating = 4.0f, comment = "Great atmosphere and delicious food!"),
            Review(userId = 2, restaurantId = 1, rating = 4.5f, comment = "Love their desserts, highly recommended!"),

            // Reviews for The Keg Oshawa (restaurantId = 2)
            Review(userId = 1, restaurantId = 2, rating = 5.0f, comment = "Best steaks in town! Perfect cooking every time."),
            Review(userId = 3, restaurantId = 2, rating = 4.0f, comment = "Excellent service and quality meat."),

            // Reviews for Fazio's Italian Restaurant (restaurantId = 3)
            Review(userId = 2, restaurantId = 3, rating = 4.5f, comment = "Authentic Italian cuisine, feels like home!"),
            Review(userId = 3, restaurantId = 3, rating = 5.0f, comment = "The pasta is amazing, family-friendly atmosphere."),

            // Reviews for Sushi Masa (restaurantId = 4)
            Review(userId = 1, restaurantId = 4, rating = 4.5f, comment = "Fresh sushi, beautiful presentation!"),
            Review(userId = 2, restaurantId = 4, rating = 4.0f, comment = "Good quality fish, nice ambiance."),

            // Reviews for Oshawa House Restaurant (restaurantId = 5)
            Review(userId = 3, restaurantId = 5, rating = 4.0f, comment = "Classic diner with great comfort food!")
        )

        reviews.forEach { review ->
            db.insert("reviews", null, review.toContentValues())
        }

        // Update restaurant ratings based on these reviews
        for (restaurantId in 1..5) {
            val avgRating = calculateAverageRatingFromDb(db, restaurantId)
            val contentValues = android.content.ContentValues().apply {
                put("rating", avgRating)
            }
            db.update("restaurants", contentValues, "id = ?", arrayOf(restaurantId.toString()))
        }
    }

    // Helper method to calculate average rating during onCreate (when db is being created)
    private fun calculateAverageRatingFromDb(db: SQLiteDatabase, restaurantId: Int): Float {
        val cursor = db.rawQuery(
            "SELECT AVG(rating) as avgRating FROM reviews WHERE restaurantId = ?",
            arrayOf(restaurantId.toString())
        )

        var avgRating = 0f
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("avgRating")
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                avgRating = cursor.getFloat(columnIndex)
            }
        }
        cursor.close()

        return avgRating
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

    // Update restaurant media (video and images)
    fun updateRestaurantMedia(restaurantId: Int, videoUri: String?, image1Uri: String?, image2Uri: String?): Boolean {
        val db = writableDatabase
        val contentValues = android.content.ContentValues().apply {
            put("videoUri", videoUri)
            put("image1Uri", image1Uri)
            put("image2Uri", image2Uri)
        }
        val result = db.update("restaurants", contentValues, "id = ?", arrayOf(restaurantId.toString()))
        return result > 0
    }


    // Menu and Menu elements Methods
    // Get menu for a restaurant given the name
    fun getMenuForRestaurant(restaurantName: String): List<Dish> {
        val dishes = mutableListOf<Dish>()
        val db = readableDatabase
        val query = """
            SELECT d.* FROM dishes d
            JOIN menu_elements me ON d.id = me.dishId
            JOIN restaurants r ON me.restaurantId = r.id
            WHERE r.name = ?
        """
        val cursor = db.rawQuery(query, arrayOf(restaurantName))
        while (cursor.moveToNext()) {
            val dish: Dish = Dish.getFromCursor(cursor)
            dishes.add(dish)
        }
        cursor.close()
        return dishes
    }

    // User Table Methods

    // retrieve user by email
    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User.getFromCursor(cursor)
        }
        cursor.close()
        return user
    }

    // register user
    fun registerUser(name: String, email: String, password: String): Boolean {
        val db = writableDatabase

        // check if user already exists
        val existingUser = getUserByEmail(email)
        if (existingUser != null) {
            return false
        }


        val salt = BCrypt.gensalt()
        val passwordHash = BCrypt.hashpw(password, salt)

        val user = User(
            name = name,
            email = email,
            passwordHash = passwordHash,
            salt = salt
        )

        val result = db.insert("users", null, user.toContentValues())
        return result != -1L
    }

    // autenticate userù
    fun authenticateUser(email: String, password: String): User? {
        val user = getUserByEmail(email) ?: return null

        return if (BCrypt.checkpw(password, user.passwordHash)) {
            user
        } else {
            null
        }
    }

    // Review Methods

    // Get all reviews for a restaurant
    fun getReviewsForRestaurant(restaurantId: Int): List<Review> {
        val reviews = mutableListOf<Review>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM reviews WHERE restaurantId = ?", arrayOf(restaurantId.toString()))
        while (cursor.moveToNext()) {
            val review: Review = Review.getFromCursor(cursor)
            reviews.add(review)
        }
        cursor.close()
        return reviews
    }

    // Get review by user and restaurant (since user can only have one review per restaurant)
    fun getUserReviewForRestaurant(userId: Int, restaurantId: Int): Review? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM reviews WHERE userId = ? AND restaurantId = ?",
            arrayOf(userId.toString(), restaurantId.toString()))
        var review: Review? = null
        if (cursor.moveToFirst()) {
            review = Review.getFromCursor(cursor)
        }
        cursor.close()
        return review
    }

    // Insert or update a review (since user can only have one review per restaurant)
    fun insertOrUpdateReview(review: Review): Boolean {
        val db = writableDatabase
        val result = db.insertWithOnConflict("reviews", null, review.toContentValues(),
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)

        // Update restaurant rating after insertion/update
        if (result != -1L) {
            updateRestaurantRating(review.restaurantId)
        }

        return result != -1L
    }

    // Delete a review
    fun deleteReview(userId: Int, restaurantId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("reviews", "userId = ? AND restaurantId = ?",
            arrayOf(userId.toString(), restaurantId.toString()))

        // Update restaurant rating after deletion
        if (result > 0) {
            updateRestaurantRating(restaurantId)
        }

        return result > 0
    }

    // Calculate average rating for a restaurant
    fun calculateAverageRating(restaurantId: Int): Float {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT AVG(rating) as avgRating FROM reviews WHERE restaurantId = ?",
            arrayOf(restaurantId.toString())
        )

        var avgRating = 0f
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("avgRating")
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                avgRating = cursor.getFloat(columnIndex)
            }
        }
        cursor.close()

        return avgRating
    }

    // Update restaurant rating based on reviews
    fun updateRestaurantRating(restaurantId: Int) {
        val db = writableDatabase
        val avgRating = calculateAverageRating(restaurantId)

        val contentValues = android.content.ContentValues().apply {
            put("rating", avgRating)
        }

        db.update("restaurants", contentValues, "id = ?", arrayOf(restaurantId.toString()))
    }

    // Get restaurant by ID
    fun getRestaurantById(restaurantId: Int): Restaurant? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM restaurants WHERE id = ?", arrayOf(restaurantId.toString()))
        var restaurant: Restaurant? = null
        if (cursor.moveToFirst()) {
            restaurant = Restaurant.getFromCursor(cursor)
        }
        cursor.close()
        return restaurant
    }

    // Get restaurant by name
    fun getRestaurantByName(restaurantName: String): Restaurant? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM restaurants WHERE name = ?", arrayOf(restaurantName))
        var restaurant: Restaurant? = null
        if (cursor.moveToFirst()) {
            restaurant = Restaurant.getFromCursor(cursor)
        }
        cursor.close()
        return restaurant
    }

    // Get user name by ID
    fun getUserNameById(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM users WHERE id = ?", arrayOf(userId.toString()))
        var userName: String? = null
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        }
        cursor.close()
        return userName
    }


}


