package com.test.project

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.DatabaseHelper

class RestaurantViewActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: RestaurantAdapter

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    val permissionArray = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_view)

        val searchBox: EditText = findViewById(R.id.search_bar)
        val rv: RecyclerView = findViewById(R.id.recyclerView)
        val mapbtn: ImageButton = findViewById(R.id.btnMap)
        val logbtn: ImageButton = findViewById(R.id.btnAccount)
        val addbtn: ImageButton = findViewById(R.id.addTaskBtn)
        val micSearchBtn: ImageButton = findViewById(R.id.voiceSearchBtn)

        adapter = RestaurantAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

      addbtn.setOnClickListener {
            startActivity(Intent(this, NewRestaurantActivity::class.java))
        }

        mapbtn.setOnClickListener {
            val intent = android.content.Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        logbtn.setOnClickListener {
            val intent = android.content.Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // vocal research
        if (checkSelfPermission(permissionArray[2]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissionArray, 200)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        micSearchBtn.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    searchBox.setText("")
                    searchBox.setHint("Listening...")

                    speechRecognizer.startListening(speechRecognizerIntent)
                    true
                }
                android.view.MotionEvent.ACTION_UP -> {
                    searchBox.setHint("Search restaurants or cuisine")

                    speechRecognizer.stopListening()
                    true
                }
                else -> false
            }

        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    searchBox.setText(matches[0])
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })



    }

    override fun onResume() {
        super.onResume()
        val databaseVersion = 3
        databaseHelper = DatabaseHelper(this, databaseVersion)

        adapter.submit(databaseHelper.getAllRestaurants())
    }
}