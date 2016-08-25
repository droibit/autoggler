package com.droibit.autoggler.edit

import android.location.Location
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

private val DEFAULT_ZOOM = 18f

class GoogleMapView() : OnMapReadyCallback {

    private lateinit var mapView: MapView

    private var currentLocation: Location? = null

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    fun onCreate(rawMapView: MapView, savedInstanceState: Bundle?) {
        this.mapView = rawMapView
        this.mapView.getMapAsync(this)
        this.mapView.onCreate(savedInstanceState)
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() = mapView.onDestroy()

    fun myLocation(location: Location) {
        if (mapReady) {
            moveCameraTo(location)
        }
        currentLocation = location
    }

    // OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.mapReady = true
        this.currentLocation?.let { moveCameraTo(location = it) }
    }

    private fun moveCameraTo(location: Location) {
        googleMap?.let {
            val newCamera = CameraUpdateFactory.newLatLngZoom(location.toLatLng(), DEFAULT_ZOOM)
            it.animateCamera(newCamera)
        }
    }
}

private fun Location.toLatLng() = LatLng(latitude, longitude)