package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Bundle
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.edit.BounceDropAnimator
import com.droibit.autoggler.utils.toLatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import timber.log.Timber

private val DEFAULT_ZOOM = 16f

private val KEY_LOCATION = "KEY_LOCATION"
private val KEY_ZOOM = "KEY_ZOOM"

class GoogleMapView(
        private val interactionCallback: Callback,
        private val bounceDropAnimator: BounceDropAnimator,
        private val permissionChecker: RuntimePermissionChecker) : OnMapReadyCallback {

    private sealed class LocationEvent(val location: LatLng, val zoom: Float) {
        class Move(location: LatLng, zoom: Float) : LocationEvent(location, zoom)
        class Restore(location: LatLng, zoom: Float) : LocationEvent(location, zoom)
    }

    interface Callback :
            GoogleMap.OnMapLongClickListener,
            GoogleMap.OnMarkerClickListener,
            GoogleMap.OnMarkerDragListener,
            GoogleMap.OnInfoWindowClickListener

    private lateinit var mapView: MapView

    private var currentLocation: LocationEvent? = null

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    fun onCreate(rawMapView: MapView, savedInstanceState: Bundle?) {
        this.mapView = rawMapView
        this.mapView.getMapAsync(this)
        this.mapView.onCreate(null)

        if (savedInstanceState != null) {
            currentLocation = LocationEvent.Restore(
                    location = savedInstanceState.getParcelable(KEY_LOCATION),
                    zoom = savedInstanceState.getFloat(KEY_ZOOM)
            )
        }
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() {
        bounceDropAnimator.stop()
        mapView.onDestroy()
    }

    fun onSaveInstanceState(outState: Bundle) {
        googleMap?.let {
            outState.putParcelable(KEY_LOCATION, it.cameraPosition.target)
            outState.putFloat(KEY_ZOOM, it.cameraPosition.zoom)
        }
    }

    fun updateMyLocation(location: Location) = updateMyLocation(location.toLatLng())

    fun updateMyLocation(location: LatLng) {
        val event = LocationEvent.Move(location, zoom = DEFAULT_ZOOM).apply { currentLocation = this }
        if (mapReady) {
            moveCamera(event)
        }
    }

    fun enableMyLocationButton(enabled: Boolean) {
        googleMap?.let { it.enabledMyLocationIfAllowed(enabled) }
    }

    fun addCircle(circleOptions: CircleOptions): Circle {
        return checkNotNull(googleMap).addCircle(circleOptions)
    }

    fun addMarker(markerOptions: MarkerOptions, callback: (Marker) -> Unit) {
        val marker = checkNotNull(googleMap).addMarker(markerOptions)
        bounceDropAnimator.start(target = marker, dropCallback = callback)
    }

    // OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        Timber.d("onMapReady, currentLocation=$currentLocation")

        this.googleMap = googleMap.apply {
            enabledMyLocationIfAllowed(true)

            setOnMapLongClickListener(interactionCallback)
            setOnMarkerClickListener(interactionCallback)
            setOnMarkerDragListener(interactionCallback)
            setOnInfoWindowClickListener(interactionCallback)
        }
        this.mapReady = true
        this.currentLocation?.let { moveCamera(event = it) }
    }

    // Private

    private fun moveCamera(event: LocationEvent) {
        Timber.d("moveCamera(location=${event.location}, zoom=${event.zoom})")

        val newCamera = CameraUpdateFactory.newLatLngZoom(event.location, event.zoom)
        when (event) {
            is LocationEvent.Move -> checkNotNull(googleMap).animateCamera(newCamera)
            is LocationEvent.Restore -> checkNotNull(googleMap).moveCamera(newCamera)
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