package com.test.project

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import android.widget.RatingBar
import android.widget.VideoView
import android.widget.MediaController
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    // Media elements
    private lateinit var videoView: VideoView
    private lateinit var videoContainer: FrameLayout
    private lateinit var noVideoText: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private var mediaController: MediaController? = null

    // Request codes for media picker
    private val REQUEST_VIDEO = 100
    private val REQUEST_IMAGE_1 = 101
    private val REQUEST_IMAGE_2 = 102
    private val REQUEST_PERMISSIONS = 200

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Check and request permissions
        checkMediaPermissions()

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Get current user ID from session
        currentUserId = getCurrentUserId()

        // Initialize views
        val restaurantNameTextView: TextView = findViewById(R.id.restaurantNameTextView)
        val restaurantDescriptionTextView: TextView = findViewById(R.id.restaurantDescriptionTextView)
        val restaurantAddressTextView: TextView = findViewById(R.id.restaurantAddressTextView)
        val restaurantPhoneTextView: TextView = findViewById(R.id.restaurantPhoneTextView)
        val viewMenuButton: Button = findViewById(R.id.viewMenuButton)
        val backButton: ImageButton = findViewById(R.id.backButton)
        val addCommentButton: Button = findViewById(R.id.addCommentButton)
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)

        // Media elements
        videoView = findViewById(R.id.restaurantVideoView)
        videoContainer = findViewById(R.id.videoContainer)
        noVideoText = findViewById(R.id.noVideoText)
        imageView1 = findViewById(R.id.restaurantImage1)
        imageView2 = findViewById(R.id.restaurantImage2)

        // Setup MediaController for video playback
        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoContainer)
        videoView.setMediaController(mediaController)

        // Force VideoView to be visible and in front
        videoView.setZOrderOnTop(false)
        videoView.setZOrderMediaOverlay(false)

        val uploadVideoButton: Button = findViewById(R.id.uploadVideoButton)
        val uploadImage1Button: Button = findViewById(R.id.uploadImage1Button)
        val uploadImage2Button: Button = findViewById(R.id.uploadImage2Button)

        // Get restaurant name and load restaurant data
        val restaurantName = intent.getStringExtra("restaurantName") ?: "Restaurant"
        restaurantNameTextView.text = restaurantName

        // Load restaurant from database
        currentRestaurant = databaseHelper.getRestaurantByName(restaurantName)

        // Display restaurant information
        currentRestaurant?.let { restaurant ->
            restaurantDescriptionTextView.text = restaurant.description ?: "No description available"
            restaurantAddressTextView.text = "address: ${restaurant.address}"
            restaurantPhoneTextView.text = "phone: ${restaurant.phoneNumber}"

            // Load saved media
            restaurant.videoUri?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    noVideoText.visibility = android.view.View.GONE

                    videoView.setVideoURI(uri)
                    videoView.setOnPreparedListener { mp ->
                        // android.util.Log.d("RestaurantDetail", "Video prepared - Width: ${mp.videoWidth}, Height: ${mp.videoHeight}")
                        mp.isLooping = true
                        mp.start()
                        // android.util.Log.d("RestaurantDetail", "Video started playing")
                    }

                    videoView.setOnErrorListener { mp, what, extra ->
                        android.util.Log.e("RestaurantDetail", "Video error - what: $what, extra: $extra")
                        runOnUiThread {
                            noVideoText.visibility = android.view.View.VISIBLE
                            noVideoText.text = "Error loading video"
                            Toast.makeText(this, "Error playing video (code: $what)", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }

                    videoView.setOnCompletionListener { mp ->
                        //android.util.Log.d("RestaurantDetail", "Video completed, restarting")
                        mp.start()
                    }

                    videoView.setOnInfoListener { mp, what, extra ->
                        //android.util.Log.d("RestaurantDetail", "Video info - what: $what, extra: $extra")
                        false
                    }

                } catch (e: Exception) {
                    android.util.Log.e("RestaurantDetail", "Exception loading video", e)
                    e.printStackTrace()
                    noVideoText.visibility = android.view.View.VISIBLE
                    noVideoText.text = "Failed to load video"
                    Toast.makeText(this, "Error loading video: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                android.util.Log.d("RestaurantDetail", "No video URI found")
                // No video uploaded
                noVideoText.visibility = android.view.View.VISIBLE
                noVideoText.text = "No video uploaded yet"
            }

            restaurant.image1Uri?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    imageView1.setImageURI(uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            restaurant.image2Uri?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    imageView2.setImageURI(uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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

        // Upload media buttons
        uploadVideoButton.setOnClickListener {
            openVideoPicker()
        }

        uploadImage1Button.setOnClickListener {
            openImagePicker(REQUEST_IMAGE_1)
        }

        uploadImage2Button.setOnClickListener {
            openImagePicker(REQUEST_IMAGE_2)
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
                    loadReviews()

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

    // Open video picker
    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_VIDEO)
    }

    // Open image picker
    private fun openImagePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    // Handle media selection results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val selectedUri: Uri? = data.data
            val restaurant = currentRestaurant ?: return
            val restaurantId = restaurant.id ?: return

            selectedUri?.let { uri ->
                // Request persistent permission for the URI
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                when (requestCode) {
                    REQUEST_VIDEO -> {
                        android.util.Log.d("RestaurantDetail", "New video selected: $uri")
                        noVideoText.visibility = android.view.View.GONE

                        videoView.setVideoURI(uri)
                        videoView.setOnPreparedListener { mp ->
                            android.util.Log.d("RestaurantDetail", "New video prepared - Width: ${mp.videoWidth}, Height: ${mp.videoHeight}")
                            mp.isLooping = true
                            mp.start()
                            android.util.Log.d("RestaurantDetail", "New video started playing")
                        }

                        videoView.setOnErrorListener { mp, what, extra ->
                            android.util.Log.e("RestaurantDetail", "New video error - what: $what, extra: $extra")
                            runOnUiThread {
                                noVideoText.visibility = android.view.View.VISIBLE
                                noVideoText.text = "Error loading video"
                                Toast.makeText(this, "Error playing video (code: $what)", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }

                        videoView.setOnCompletionListener { mp ->
                            android.util.Log.d("RestaurantDetail", "New video completed, restarting")
                            mp.start() // Loop the video
                        }

                        videoView.setOnInfoListener { mp, what, extra ->
                            android.util.Log.d("RestaurantDetail", "New video info - what: $what, extra: $extra")
                            false
                        }

                        // Save to database
                        val success = databaseHelper.updateRestaurantMedia(
                            restaurantId,
                            uri.toString(),
                            restaurant.image1Uri,
                            restaurant.image2Uri
                        )

                        if (success) {
                            currentRestaurant = databaseHelper.getRestaurantByName(restaurant.name)
                            Toast.makeText(this, "Video saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save video", Toast.LENGTH_SHORT).show()
                        }
                    }
                    REQUEST_IMAGE_1 -> {
                        imageView1.setImageURI(uri)

                        // Save to database
                        val success = databaseHelper.updateRestaurantMedia(
                            restaurantId,
                            restaurant.videoUri,
                            uri.toString(),
                            restaurant.image2Uri
                        )

                        if (success) {
                            currentRestaurant = databaseHelper.getRestaurantByName(restaurant.name)
                            Toast.makeText(this, "Image 1 saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save image 1", Toast.LENGTH_SHORT).show()
                        }
                    }
                    REQUEST_IMAGE_2 -> {
                        imageView2.setImageURI(uri)

                        // Save to database
                        val success = databaseHelper.updateRestaurantMedia(
                            restaurantId,
                            restaurant.videoUri,
                            restaurant.image1Uri,
                            uri.toString()
                        )

                        if (success) {
                            currentRestaurant = databaseHelper.getRestaurantByName(restaurant.name)
                            Toast.makeText(this, "Image 2 saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save image 2", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // Check and request media permissions
    private fun checkMediaPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions were denied. Media features may not work.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::videoView.isInitialized) {
            videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // Video will auto-resume if it was playing
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::videoView.isInitialized) {
            videoView.stopPlayback()
        }
        mediaController = null
    }
}