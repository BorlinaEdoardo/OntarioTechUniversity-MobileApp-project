package com.test.project

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.test.project.database.DatabaseHelper
import com.test.project.database.Restaurant
import android.location.Geocoder
import android.widget.EditText
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher



class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var dbHelper: DatabaseHelper

    // UI components
    private lateinit var bottomCard: MaterialCardView
    private lateinit var restaurantName: TextView
    private lateinit var restaurantAddress: TextView
    private lateinit var restaurantRating: TextView
    private lateinit var zoomInButton: ImageButton
    private lateinit var zoomOutButton: ImageButton

    private lateinit var searchEditText: EditText
    private val markerList = mutableListOf<Pair<Restaurant, LatLng>>()

    private fun showBottomCard(restaurant: Restaurant) {
        bottomCard.visibility = View.VISIBLE
        restaurantName.text = restaurant.name
        restaurantAddress.text = restaurant.address
        restaurantRating.text = "⭐ ${String.format("%.1f", restaurant.rating ?: 0f)}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        dbHelper = DatabaseHelper(this)

        // Back button
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, RestaurantViewActivity::class.java))
            finish()
        }

        // Initialize views here
        bottomCard = findViewById(R.id.bottomCard)
        restaurantName = findViewById(R.id.restaurantName)
        restaurantAddress = findViewById(R.id.restaurantAddress)
        restaurantRating = findViewById(R.id.restaurantRating)
        zoomInButton = findViewById(R.id.zoomInButton)
        zoomOutButton = findViewById(R.id.zoomOutButton)
        searchEditText = findViewById(R.id.searchEditText)


        // Map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Center map on OTU
        val OTU = LatLng(43.9456, -78.8968)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(OTU, 13f))

        val restaurants = dbHelper.getAllRestaurants()
        val geocoder = Geocoder(this, Locale.getDefault())

        restaurants.forEach { restaurant ->
            var position: LatLng? = null

            // Use stored coordinates if available
            if (restaurant.lat != 0.0 && restaurant.lng != 0.0) {
                position = LatLng(restaurant.lat, restaurant.lng)
            } else if (!restaurant.address.isNullOrEmpty()) {
                // Geocode the address
                val addresses = geocoder.getFromLocationName(restaurant.address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val loc = addresses[0]
                    position = LatLng(loc.latitude, loc.longitude)
                }
            }

            if (position != null) {
                // Add marker to map and attach Restaurant object to marker tag
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(restaurant.name)
                )
                marker?.tag = restaurant

                // Save for search filtering
                markerList.add(Pair(restaurant, position))
            }
        }

        // function to filter text to display specific markers
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                if (query.isEmpty()) {
                    map.clear()
                    markerList.forEach { pair ->
                        map.addMarker(
                            MarkerOptions()
                                .position(pair.second)
                                .title(pair.first.name)
                        )?.tag = pair.first
                    }
                    return
                }

                val matches = markerList.filter {
                    it.first.name.lowercase().contains(query)
                }

                map.clear()

                if (matches.isNotEmpty()) {
                    matches.forEach { pair ->
                        map.addMarker(
                            MarkerOptions()
                                .position(pair.second)
                                .title(pair.first.name)
                        )?.tag = pair.first
                    }

                    val firstPos = matches.first().second
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(firstPos, 15f))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        map.setOnMarkerClickListener { marker ->
            val restaurant = marker.tag as? Restaurant
            restaurant?.let { showBottomCard(it) }

            map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            true
        }

        // Zoom buttons
        zoomInButton.setOnClickListener { map.animateCamera(CameraUpdateFactory.zoomIn()) }
        zoomOutButton.setOnClickListener { map.animateCamera(CameraUpdateFactory.zoomOut()) }
    }
}


