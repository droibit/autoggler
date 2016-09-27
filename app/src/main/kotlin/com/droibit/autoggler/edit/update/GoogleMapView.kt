package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import rx.Completable
import timber.log.Timber

class GoogleMapView(private val callback: Callback) {

    interface Callback :
            GoogleMap.OnMarkerClickListener,
            GoogleMap.OnMarkerDragListener,
            GoogleMap.OnInfoWindowClickListener

    private lateinit var mapView: MapView

    private lateinit var googleMap: GoogleMap

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

    fun onCreate(savedInstanceState: Bundle?) {
        checkNotNull(mapView) { "Need to call #getMapAsync" }
        mapView.onCreate(null)
    }

    fun onResume() = mapView.onResume()

    fun onPause() = mapView.onPause()

    fun onDestroy() = mapView.onDestroy()

    fun onSaveInstanceState(outState: Bundle) {
    }

    fun addCircle(circleOptions: CircleOptions): Circle {
        return googleMap.addCircle(circleOptions)
    }

    fun addMarker(markerOptions: MarkerOptions): Marker {
        return googleMap.addMarker(markerOptions)
    }

    // Private

    private fun onMapReady(googleMap: GoogleMap) {
        Timber.d("onMapReady")

        this.googleMap = googleMap.apply {
            setOnMarkerClickListener(callback)
            setOnMarkerDragListener(callback)
            setOnInfoWindowClickListener(callback)
        }
    }
}