package com.example.quickcare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Details : AppCompatActivity() {

    private lateinit var hospitalNameTextView: TextView
    private lateinit var hospitalAddressTextView: TextView
    private lateinit var hospitalPhoneTextView: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        hospitalNameTextView = findViewById(R.id.hospitalName)
        hospitalAddressTextView = findViewById(R.id.hospitalAddress)
        hospitalPhoneTextView = findViewById(R.id.hospitalPhone)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get user's current location and fetch nearest hospital
        fetchNearestHospital()

        val btn: FloatingActionButton = findViewById(R.id.floatingActionButton2)
        btn.setOnClickListener {
            val intent = Intent(this, Home02::class.java)
            startActivity(intent)
        }
    }

    private fun fetchNearestHospital() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestLocationPermissions()
        } else {
            // Permission already granted, fetch location
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, OnSuccessListener { location: Location? ->
                    if (location != null) {
                        // TODO: Call method to fetch hospital details based on location
                        // Example: displayHospitalDetails(hospital)
                        // Replace with your method to fetch and display hospital details
                        displaySampleHospitalDetails()
                    }
                })
        }
    }

    private fun displaySampleHospitalDetails() {
        // Example method to display sample hospital details
        hospitalNameTextView.text = "Sample Hospital"
        hospitalAddressTextView.text = "123 Main Street, City, Country"
        hospitalPhoneTextView.text = "+1234567890"
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSIONS_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, fetch location
                    fetchNearestHospital()
                } else {
                    // Permission denied
                    // Handle what happens when location permission is denied
                    // For example, show a message or disable location-dependent functionality
                }
                return
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
    }
}
