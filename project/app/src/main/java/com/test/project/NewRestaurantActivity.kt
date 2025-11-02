package com.test.project

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.test.project.database.DatabaseHelper
import com.test.project.database.Restaurant

class NewRestaurantActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextShortDescription: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var editTextRating: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_restaurant)

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextShortDescription = findViewById(R.id.editTextShortDescription)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextRating = findViewById(R.id.editTextRating)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Initialize database helper
        val databaseVersion = 3
        databaseHelper = DatabaseHelper(this, databaseVersion)

        // Set up button listeners
        btnSave.setOnClickListener {
            saveRestaurant()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveRestaurant() {
        val name = editTextName.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val phone = editTextPhone.text.toString().trim()
        val shortDescription = editTextShortDescription.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val ratingText = editTextRating.text.toString().trim()

        // Validate input
        if (name.isEmpty()) {
            editTextName.error = "Restaurant name is required"
            editTextName.requestFocus()
            return
        }

        if (address.isEmpty()) {
            editTextAddress.error = "Address is required"
            editTextAddress.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            editTextPhone.error = "Phone number is required"
            editTextPhone.requestFocus()
            return
        }

        // Validate rating
        val rating = try {
            if (ratingText.isEmpty()) 4.5f else ratingText.toFloat()
        } catch (e: NumberFormatException) {
            editTextRating.error = "Please enter a valid rating"
            editTextRating.requestFocus()
            return
        }

        if (rating < 1.0f || rating > 5.0f) {
            editTextRating.error = "Rating must be between 1.0 and 5.0"
            editTextRating.requestFocus()
            return
        }

        try {
            // Create restaurant object and save to database
            val restaurant = Restaurant(
                name = name,
                address = address,
                phoneNumber = phone,
                description = if (description.isEmpty()) "Description not available" else description,
                shortDescription = if (shortDescription.isEmpty()) "Short description not available" else shortDescription,
                rating = rating
            )

            databaseHelper.insertRestaurant(restaurant)

            Toast.makeText(this, "Restaurant saved successfully!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity and return to previous screen

        } catch (e: Exception) {
            Toast.makeText(this, "Error saving restaurant: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
