package com.droibit.autoggler.data.provider.geometory

import android.content.Context
import android.support.v4.content.ContextCompat
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

class GeometryProvider(private val context: Context) {

    companion object {
        private val CIRCLE_STROKE_WIDTH = 2f
    }

    fun newCircleOptions(src: Circle): CircleOptions {
        Timber.d("newCircleOptions($src)")

        return CircleOptions()
                .center(src.latLng)
                .radius(src.radius)
                .strokeColor(ContextCompat.getColor(context, R.color.colorCircleStroke))
                .strokeWidth(CIRCLE_STROKE_WIDTH)
                .fillColor(ContextCompat.getColor(context, R.color.colorCircleFill))
    }

    fun newCircleOptions(position: LatLng, radius: Double = 0.0): CircleOptions {
        Timber.d("newCircleOptions([$position], $radius)")

        return CircleOptions()
                .center(position)
                .radius(radius)
                .strokeColor(ContextCompat.getColor(context, R.color.colorCircleStroke))
                .strokeWidth(CIRCLE_STROKE_WIDTH)
                .fillColor(ContextCompat.getColor(context, R.color.colorCircleFill))
    }

    fun newMarkerOptions(position: LatLng): MarkerOptions {
        Timber.d("newMarkerOptions($position)")

        return MarkerOptions()
                .position(position)
                .draggable(true)
                .title(context.getString(R.string.add_geofence_marker_title))
                .snippet(context.getString(R.string.add_geofence_marker_subtitle))
                .icon(BitmapDescriptorFactory.defaultMarker(HUE_RED))
    }
}