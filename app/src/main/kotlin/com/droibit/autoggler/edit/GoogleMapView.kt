package com.droibit.autoggler.edit

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Bundle
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

private val DEFAULT_ZOOM = 16f

class GoogleMapView(private val permissionChecker: RuntimePermissionChecker) : OnMapReadyCallback {

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

    fun updateMyLocation(location: Location) {
        if (mapReady) {
            moveCameraTo(location)
        }
        currentLocation = location
    }

    fun enableMyLocationButton(enabled: Boolean) {
        googleMap?.let {
            it.enabledMyLocationIfAllowed(enabled)
        }
    }

    // OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        Timber.d("onMapReady, currentLocation=$currentLocation")

        this.googleMap = googleMap.apply {
            enabledMyLocationIfAllowed(true)
        }
        this.mapReady = true
        this.currentLocation?.let { moveCameraTo(location = it) }
    }

    private fun moveCameraTo(location: Location) {
        Timber.d("moveCameraTo=$location")

        googleMap?.let {
            val newCamera = CameraUpdateFactory.newLatLngZoom(location.toLatLng(), DEFAULT_ZOOM)
            it.animateCamera(newCamera)
        }
    }

    private fun GoogleMap.enabledMyLocationIfAllowed(enabled: Boolean) {
        if (permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)) {
            isMyLocationEnabled = enabled
            Timber.d("myLocationEnabled: Actual=$enabled")
        } else {
            Timber.d("myLocationEnabled: permissions denied")
        }
    }
}

private fun Location.toLatLng() = LatLng(latitude, longitude)