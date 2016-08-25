package com.droibit.autoggler.edit

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback

class MyLocationMapView(private val mapView: MapView) : OnMapReadyCallback {

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    var currentLocation: Location? = null
        set(value) { updateLocation(value!!) }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.mapReady = true

        this.currentLocation?.let { moveCameraTo(location = it) }
    }

    private fun updateLocation(location: Location) {
        if (mapReady) {
            moveCameraTo(location)
        }
        currentLocation = location
    }

    private fun moveCameraTo(location: Location) {
        // TODO: 
    }
}