package com.droibit.autoggler.data.provider.geometory

import android.content.Context
import android.support.v4.content.ContextCompat
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import timber.log.Timber
import com.google.android.gms.maps.model.Circle as GmsCircle

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

    // For save to bundle
    fun newCircleOptions(circle: GmsCircle): CircleOptions {
        return circle.run {
            CircleOptions()
                    .center(circle.center)
                    .radius(circle.radius)
                    .strokeColor(circle.strokeColor)
                    .strokeWidth(circle.strokeWidth)
                    .fillColor(circle.fillColor)
        }
    }

    fun newMarkerOptions(position: LatLng, showSnippet: Boolean = true): MarkerOptions {
        Timber.d("newMarkerOptions($position)")

        return MarkerOptions().apply {
            position(position)
            draggable(true)
            title(context.getString(R.string.add_geofence_marker_title))
            icon(getMarkerBitmapDescriptor(isDraggable = true))
            if (showSnippet) {
                snippet(context.getString(R.string.add_geofence_marker_subtitle))
            }
        }
    }

    fun newUneditableMarkerOptions(position: LatLng): MarkerOptions {
        Timber.d("newUneditableMarkerOptions($position)")

        return MarkerOptions()
                .position(position)
                .draggable(false)
                .icon(getMarkerBitmapDescriptor(isDraggable = false))
    }

    // For save to bundle
    fun newMarkerOptions(marker: Marker): MarkerOptions {
        Timber.d("newUneditableMarkerOptions($marker)")
        return marker.run {
            MarkerOptions()
                    .position(position)
                    .draggable(marker.isDraggable)
                    .icon(getMarkerBitmapDescriptor(isDraggable = marker.isDraggable))
                    .title(marker.title)
                    .snippet(marker.snippet)

        }
    }

    private fun getMarkerBitmapDescriptor(isDraggable: Boolean)
            = BitmapDescriptorFactory.defaultMarker(if (isDraggable) HUE_RED else HUE_GREEN)
}