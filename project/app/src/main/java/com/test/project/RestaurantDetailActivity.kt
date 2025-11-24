package com.test.project

import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper
import com.test.project.database.Review
import com.test.project.database.Restaurant

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewsRecyclerView: RecyclerView
    private var currentRestaurant: Restaurant? = null
    private var currentUserId: Int? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Get current user ID from session
        currentUserId = getCurrentUserId()

        // Initialize views
        val restaurantNameTextView: TextView = findViewById(R.id.restaurantNameTextView)
        val viewMenuButton: Button = findViewById(R.id.viewMenuButton)
        val backButton: ImageButton = findViewById(R.id.backButton)
        val addCommentButton: Button = findViewById(R.id.addCommentButton)
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)

        // Get restaurant name and load restaurant data
        val restaurantName = intent.getStringExtra("restaurantName") ?: "Restaurant"
        restaurantNameTextView.text = restaurantName

        // Load restaurant from database
        currentRestaurant = databaseHelper.getRestaurantByName(restaurantName)

        // Set up RecyclerView
        setupRecyclerView()

        // Load reviews
        loadReviews()

        // Set up button click listeners
        viewMenuButton.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java)
            intent.putExtra("RESTAURANT_NAME", restaurantName)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        addCommentButton.setOnClickListener {
            if (currentUserId == null) {
                Toast.makeText(this, "Please log in to add a review", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showAddReviewDialog()
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
        reviewAdapter = ReviewAdapter(mutableListOf(), databaseHelper)
        reviewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RestaurantDetailActivity)
            adapter = reviewAdapter
        }
    }

    private fun loadReviews() {
        currentRestaurant?.let { restaurant ->
            val restaurantId = restaurant.id
            if (restaurantId != null) {
                val reviews = databaseHelper.getReviewsForRestaurant(restaurantId)
                reviewAdapter.updateReviews(reviews)
            } else {
                // Restaurant doesn't have an ID (shouldn't happen with existing restaurants)
                Toast.makeText(this, "Unable to load reviews for this restaurant", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddReviewDialog() {
        val currentUser = currentUserId ?: return
        val restaurant = currentRestaurant ?: return
        val restaurantId = restaurant.id ?: run {
            Toast.makeText(this, "Unable to add review for this restaurant", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user already has a review for this restaurant
        val existingReview = databaseHelper.getUserReviewForRestaurant(currentUser, restaurantId)

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_review, null)
        val ratingBar: RatingBar = dialogView.findViewById(R.id.dialogRatingBar)
        val commentEditText: EditText = dialogView.findViewById(R.id.commentEditText)

        // Pre-fill with existing review if available
        existingReview?.let { review ->
            ratingBar.rating = review.rating
            commentEditText.setText(review.comment)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(if (existingReview != null) "Update" else "Submit") { _, _ ->
                val rating = ratingBar.rating
                val comment = commentEditText.text.toString().trim()

                if (rating == 0f) {
                    Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val review = Review(
                    userId = currentUser,
                    restaurantId = restaurantId,
                    rating = rating,
                    comment = comment
                )

                if (databaseHelper.insertOrUpdateReview(review)) {
                    Toast.makeText(this,
                        if (existingReview != null) "Review updated successfully" else "Review added successfully",
                        Toast.LENGTH_SHORT).show()
                    loadReviews() // Refresh the reviews list

                    // Reload restaurant data to get updated rating
                    currentRestaurant = databaseHelper.getRestaurantByName(restaurant.name)

                    // Set result to notify calling activity to refresh
                    setResult(RESULT_OK)
                } else {
                    Toast.makeText(this, "Failed to save review", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)

        // Add delete option if review exists
        if (existingReview != null) {
            dialog.setNeutralButton("Delete") { _, _ ->
                if (databaseHelper.deleteReview(currentUser, restaurantId)) {
                    Toast.makeText(this, "Review deleted successfully", Toast.LENGTH_SHORT).show()
                    loadReviews() // Refresh the reviews list

                    // Reload restaurant data to get updated rating
                    currentRestaurant = databaseHelper.getRestaurantByName(restaurant.name)

                    // Set result to notify calling activity to refresh
                    setResult(RESULT_OK)
                } else {
                    Toast.makeText(this, "Failed to delete review", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.create().show()
    }
}