package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.geometory.CompositeGeometory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import rx.Completable
import timber.log.Timber
import java.util.*

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

        var storedGeometoryOptions = ArrayList<CompositeGeometory.Options>()

        fun storeLocation(outState: Bundle, cameraPosition: CameraPosition) {
            outState.putParcelable(KEY_CAMERA_POSITION, cameraPosition)
        }

        fun storeGeometoryOptions(outState: Bundle, geometoryOptions: List<CompositeGeometory.Options>) {
            outState.putParcelableArrayList(KEY_GEOMETORY_OPTIONS, ArrayList(geometoryOptions))
        }

        fun restore(savedInstanceState: Bundle) {
            storedCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            storedGeometoryOptions = savedInstanceState.getParcelableArrayList(KEY_GEOMETORY_OPTIONS)
        }
    }

    private lateinit var mapView: MapView

    private var mapReady = false

    private var googleMap: GoogleMap? = null

    private var restoreCallback: ((List<CompositeGeometory>) -> Unit)? = null

    fun onCreate(savedInstanceState: Bundle?, restoreCallback: (List<CompositeGeometory>) -> Unit) {
        checkNotNull(mapView) { "Need to call #getMapAsync" }
        mapView.onCreate(null)

        if (savedInstanceState != null) {
            this.restorer.restore(savedInstanceState)
            this.restoreCallback = restoreCallback
        }
    }

    fun getMapAsync(rawMapView: MapView): Completable {
        Timber.d("getMapAsync")

        mapView = rawMapView
        return Completable.fromEmitter { emitter ->
            mapView.getMapAsync {
                onMapReady(googleMap = it)
                emitter.onCompleted()
            }
        }
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() = mapView.onDestroy()

    fun onSaveInstanceState(outState: Bundle, geometoryOptions: ArrayList<CompositeGeometory.Options>) {
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

        this.restorer.storedCameraPosition?.let { moveCamera(cameraPosition = it) }
        Timber.d("storedLocation=${restorer.storedCameraPosition}")

        this.restorer
    }

    private fun moveCamera(cameraPosition: CameraPosition) {
        val newCameraPosition = CameraUpdateFactory.newCameraPosition(cameraPosition)
        checkNotNull(googleMap).moveCamera(newCameraPosition)
    }

    private fun addCompositeGeometory(geometoryOptions: List<CompositeGeometory.Options>) {
        val googleMap = checkNotNull(googleMap)
        val geometories = geometoryOptions.map { options ->
            CompositeGeometory(
                    marker = googleMap.addMarker(options.marker),
                    circle = googleMap.addCircle(options.circle)
            )
        }
        checkNotNull(restoreCallback).invoke(geometories)
    }
}