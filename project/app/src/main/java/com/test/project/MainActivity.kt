package com.test.project

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.test.project.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // defining variables
        val text: TextView = findViewById(R.id.textView)
        val btn1: Button = findViewById(R.id.button1)

        val loginPage: Button = findViewById(R.id.login_page_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // make a click listener for the text view that navigate to RestaurantViewActivity
        text.setOnClickListener {
            val intent = android.content.Intent(this, RestaurantViewActivity::class.java)
            startActivity(intent)
        }

        //Click Listener for button to navigate to Login Page
        loginPage.setOnClickListener {
            val intent = android.content.Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }

        // button send the user to the map
        btn1.setOnClickListener {
            val intent = android.content.Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        }

    override fun onResume() {
        super.onResume()
        val databaseVersion = 4
        databaseHelper = DatabaseHelper(this, databaseVersion)
    }
}