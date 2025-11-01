package com.test.project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper

class RestaurantViewActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_view)

        val addTaskBtn: Button = findViewById(R.id.addTaskBtn)
        val searchBox: EditText = findViewById(R.id.search_bar)
        val rv: RecyclerView = findViewById(R.id.recyclerView)

        adapter = RestaurantAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        addTaskBtn.setOnClickListener {
            startActivity(Intent(this, NewRestaurantActivity::class.java))
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })


    }

    override fun onResume() {
        super.onResume()
        val databaseVersion = 3
        databaseHelper = DatabaseHelper(this, databaseVersion)

        adapter.submit(databaseHelper.getAllRestaurants())
    }
}