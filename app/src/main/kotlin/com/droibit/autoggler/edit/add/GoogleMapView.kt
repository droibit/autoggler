package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Bundle
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.geometory.CompositeGeometry
import com.droibit.autoggler.edit.BounceDropAnimator
import com.droibit.autoggler.utils.toLatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import timber.log.Timber

private val KEY_LOCATION = "KEY_LOCATION"
private val KEY_ZOOM = "KEY_ZOOM"
private val KEY_GEOMETORY_OPTIONS = "KEY_GEOMETORY_OPTIONS"

class GoogleMapView(
        private val interactionCallback: Callback,
        private val appConfig: ApplicationConfig,
        private val restorer: Restorer,
        private val bounceDropAnimator: BounceDropAnimator,
        private val permissionChecker: RuntimePermissionChecker) : OnMapReadyCallback {

    class Restorer {

        var storedLocation: LocationEvent? = null

        var storedGeometoryOptions: CompositeGeometry.Options = CompositeGeometry.Options()

        fun storeLocation(outState: Bundle, googleMap: GoogleMap) {
            googleMap.apply {
                outState.putParcelable(KEY_LOCATION, cameraPosition.target)
                outState.putFloat(KEY_ZOOM, cameraPosition.zoom)
            }
        }

        fun storeGeometoryOptions(outState: Bundle, geometoryOptions: CompositeGeometry.Options?) {
            outState.putParcelable(KEY_GEOMETORY_OPTIONS, geometoryOptions ?: CompositeGeometry.Options())
        }

        fun restore(savedInstanceState: Bundle) {
            storedLocation = LocationEvent.Restore(
                    location = savedInstanceState.getParcelable(KEY_LOCATION),
                    zoom = savedInstanceState.getFloat(KEY_ZOOM)
            )
            storedGeometoryOptions = savedInstanceState.getParcelable(KEY_GEOMETORY_OPTIONS)
        }
    }

    sealed class LocationEvent(val location: LatLng, val zoom: Float) {
        class Move(location: LatLng, zoom: Float) : LocationEvent(location, zoom)
        class Restore(location: LatLng, zoom: Float) : LocationEvent(location, zoom)
    }

    interface Callback :
            GoogleMap.OnMapLongClickListener,
            GoogleMap.OnMarkerClickListener,
            GoogleMap.OnMarkerDragListener,
            GoogleMap.OnInfoWindowClickListener

    private var restoreCallback: ((CompositeGeometry)->Unit)? = null

    private lateinit var mapView: MapView

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    fun onCreate(rawMapView: MapView, savedInstanceState: Bundle?, restoreCallback: (CompositeGeometry) -> Unit) {
        this.mapView = rawMapView
        this.mapView.getMapAsync(this)
        this.mapView.onCreate(null)

        if (savedInstanceState != null) {
            this.restorer.restore(savedInstanceState)
            this.restoreCallback = restoreCallback
        }
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() {
        bounceDropAnimator.stop()
        mapView.onDestroy()
    }

    fun onSaveInstanceState(outState: Bundle, geometoryOptions: CompositeGeometry.Options?) {
        restorer.storeLocation(outState, checkNotNull(googleMap))
        restorer.storeGeometoryOptions(outState, geometoryOptions)
    }

    fun updateMyLocation(location: Location) = updateMyLocation(location.toLatLng())

    fun updateMyLocation(location: LatLng) {
        if (mapReady) {
            moveCamera(LocationEvent.Move(location, zoom = checkNotNull(googleMap).cameraPosition.zoom))
        } else {
            restorer.storedLocation = LocationEvent.Move(location, zoom = appConfig.googleMapDefaultZoom)
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
        Timber.d("onMapReady")

        this.googleMap = googleMap.apply {
            enabledMyLocationIfAllowed(true)

            setOnMapLongClickListener(interactionCallback)
            setOnMarkerClickListener(interactionCallback)
            setOnMarkerDragListener(interactionCallback)
            setOnInfoWindowClickListener(interactionCallback)
        }
        this.mapReady = true

        this.restorer.storedLocation?.let { moveCamera(event = it) }
        Timber.d("storedLocation=${restorer.storedLocation}")

        val (marker, circle) = this.restorer.storedGeometoryOptions
        if (marker != null && circle != null) {
            this.addCompositeGeometory(markerOptions = marker, circleOptions = circle)
        }
        Timber.d("addCompositeGeometory(marker=[$marker], circle=[$circle])")
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

    private fun addCompositeGeometory(markerOptions: MarkerOptions, circleOptions: CircleOptions) {
        val googleMap = checkNotNull(this.googleMap)
        val marker = googleMap.addMarker(markerOptions)
        val circle = googleMap.addCircle(circleOptions)
        checkNotNull(restoreCallback).invoke(CompositeGeometry(marker, circle))
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