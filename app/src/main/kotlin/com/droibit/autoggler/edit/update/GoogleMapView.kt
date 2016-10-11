package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.geometory.CompositeGeometory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import rx.AsyncEmitter
import rx.Observable
import rx.Single
import rx.functions.Action1
import timber.log.Timber

private val KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION"
private val KEY_GEOMETORY_OPTIONS = "KEY_GEOMETORY_OPTIONS"

class GoogleMapView(
        private val interactionCallback: Callback,
        private val appConfig: ApplicationConfig,
        private val restorer: Restorer) {

    interface Callback :
            GoogleMap.OnMarkerClickListener,
            GoogleMap.OnMarkerDragListener,
            GoogleMap.OnInfoWindowClickListener

    class Restorer {

        var storedCameraPosition: CameraPosition? = null

        var storedGeometoryOptions: CompositeGeometory.Options = CompositeGeometory.Options()

        fun storeLocation(outState: Bundle, cameraPosition: CameraPosition) {
            outState.putParcelable(KEY_CAMERA_POSITION, cameraPosition)
        }

        fun storeGeometoryOptions(outState: Bundle, geometoryOptions: CompositeGeometory.Options?) {
            outState.putParcelable(KEY_GEOMETORY_OPTIONS, geometoryOptions ?: CompositeGeometory.Options())
        }

        fun restore(savedInstanceState: Bundle) {
            storedCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            storedGeometoryOptions = savedInstanceState.getParcelable(KEY_GEOMETORY_OPTIONS)
        }
    }

    private lateinit var mapView: MapView

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    fun onCreate(savedInstanceState: Bundle?) {
        checkNotNull(mapView) { "Need to call #getMapAsync" }
        mapView.onCreate(null)

        if (savedInstanceState != null) {
            this.restorer.restore(savedInstanceState)
        }
    }

    fun getMapAsync(rawMapView: MapView): Observable<CompositeGeometory?> {
        Timber.d("getMapAsync")

        mapView = rawMapView
        return Observable.fromEmitter({ emitter ->
            mapView.getMapAsync {
                onMapReady(googleMap = it)

                val compositeGeometory = restoreInstanceState()
                emitter.onNext(compositeGeometory)
                emitter.onCompleted()
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() = mapView.onDestroy()

    fun onSaveInstanceState(outState: Bundle, geometoryOptions: CompositeGeometory.Options) {
        restorer.storeLocation(outState, checkNotNull(googleMap).cameraPosition)
        restorer.storeGeometoryOptions(outState, geometoryOptions)
    }

    fun updateMyLocation(location: LatLng) {
        if (mapReady) {
            moveCamera(CameraPosition.fromLatLngZoom(location, checkNotNull(googleMap).cameraPosition.zoom))
        } else {
            restorer.storedCameraPosition = CameraPosition.fromLatLngZoom(location, appConfig.googleMapDefaultZoom)
        }
    }

    fun addMarker(markerOptions: MarkerOptions): Marker {
        return checkNotNull(googleMap).addMarker(markerOptions)
    }

    fun addCircle(circleOptions: CircleOptions): Circle {
        return checkNotNull(googleMap).addCircle(circleOptions)
    }

    // Private

    private fun onMapReady(googleMap: GoogleMap) {
        Timber.d("onMapReady")

        this.googleMap = googleMap.apply {
            setOnMarkerClickListener(interactionCallback)
            setOnMarkerDragListener(interactionCallback)
            setOnInfoWindowClickListener(interactionCallback)
        }
        this.mapReady = true
    }

    private fun restoreInstanceState(): CompositeGeometory? {
        this.restorer.storedCameraPosition?.let { moveCamera(cameraPosition = it) }
        Timber.d("storedLocation=${restorer.storedCameraPosition}")

        val (marker, circle) = this.restorer.storedGeometoryOptions
        Timber.d("addCompositeGeometory(marker=[$marker], circle=[$circle])")
        return if (marker != null && circle != null) {
            addCompositeGeometory(markerOptions = marker, circleOptions = circle)
        } else {
            null
        }
    }

    private fun moveCamera(cameraPosition: CameraPosition) {
        val newCameraPosition = CameraUpdateFactory.newCameraPosition(cameraPosition)
        checkNotNull(googleMap).moveCamera(newCameraPosition)
    }

    private fun addCompositeGeometory(markerOptions: MarkerOptions, circleOptions: CircleOptions): CompositeGeometory {
        val googleMap = checkNotNull(googleMap)
        val marker = googleMap.addMarker(markerOptions)
        val circle = googleMap.addCircle(circleOptions)
        return CompositeGeometory(marker, circle)
    }
}