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
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_restaurant)

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextPhone = findViewById(R.id.editTextPhone)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Initialize database helper
        val databaseVersion = 1
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

        try {
            // Create restaurant object and save to database
            val restaurant = Restaurant(
                name = name,
                address = address,
                phoneNumber = phone
            )

            databaseHelper.insertRestaurant(restaurant)

            Toast.makeText(this, "Restaurant saved successfully!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity and return to previous screen

        } catch (e: Exception) {
            Toast.makeText(this, "Error saving restaurant: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
