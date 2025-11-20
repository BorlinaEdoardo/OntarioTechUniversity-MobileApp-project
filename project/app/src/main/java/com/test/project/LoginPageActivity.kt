package com.test.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.test.project.database.DatabaseHelper
import org.mindrot.jbcrypt.BCrypt
import androidx.core.content.edit

class LoginPageActivity : AppCompatActivity()
{
    lateinit var usernameInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginBtn : Button

    lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            val intent = Intent(this, RestaurantViewActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login_page)

        databaseHelper = DatabaseHelper(this)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_button)

        loginBtn.setOnClickListener()
        {
            val email = usernameInput.text.toString();
            val password = passwordInput.text.toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = databaseHelper.authenticateUser(email, password)
            if (user != null) {
                // Login successful
                Toast.makeText(this, "Welcome ${user.name}!", Toast.LENGTH_SHORT).show()

                // Save user session
                saveUserSession(user)

                // Navigate to main activity
                val intent = Intent(this, RestaurantViewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserSession(user: com.test.project.database.User) {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        sharedPref.edit() {
            putString("user_name", user.name)
            putString("user_email", user.email)
            putBoolean("is_logged_in", true)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }


}