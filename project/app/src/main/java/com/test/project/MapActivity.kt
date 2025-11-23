package com.test.project

import android.os.Bundle
import android.content.Intent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Back button
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, RestaurantViewActivity::class.java))
            finish()
        }

        // Load the map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Zoom in button
        findViewById<ImageButton>(R.id.zoomInButton).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomIn())
        }

        // Zoom out button
        findViewById<ImageButton>(R.id.zoomOutButton).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomOut())
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Example: Move camera to OTU
        val OTU = LatLng(43.9456, 78.8968)
        map.addMarker(MarkerOptions().position(OTU).title("Ontario Tech University"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(OTU, 12f))
    }
}

