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
import java.util.Locale


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

            // Use coordinates if available
            if (restaurant.lat != 0.0 && restaurant.lng != 0.0) {
                position = LatLng(restaurant.lat, restaurant.lng)
            } else if (restaurant.address.isNotEmpty()) {
                // Geocode the address
                val addresses = geocoder.getFromLocationName(restaurant.address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    position = LatLng(location.latitude, location.longitude)
                }
            }

            // Add marker if we got a position
            position?.let {
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(it)
                        .title(restaurant.name)
                )
                marker?.tag = restaurant
            }
        }

        map.setOnMarkerClickListener { marker ->
            val restaurant = marker.tag as? Restaurant
            restaurant?.let {
                bottomCard.visibility = View.VISIBLE
                restaurantName.text = it.name
                restaurantAddress.text = it.address
                restaurantRating.text = "⭐ ${it.rating}"
            }
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            true
        }

        // Zoom buttons
        zoomInButton.setOnClickListener { map.animateCamera(CameraUpdateFactory.zoomIn()) }
        zoomOutButton.setOnClickListener { map.animateCamera(CameraUpdateFactory.zoomOut()) }
    }
}


