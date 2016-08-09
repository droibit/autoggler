package com.droibit.autoggler.data.geometory

import android.content.Context
import android.support.v4.content.ContextCompat
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.latLong
import com.google.android.gms.maps.model.CircleOptions

class GeometryProvider(private val context: Context) {

    companion object {
        private val CIRCLE_STROKE_WIDTH = 2f
    }

    fun newCircle(src: Circle): CircleOptions {
        return CircleOptions()
                .center(src.latLong)
                .radius(src.radius)
                .strokeColor(ContextCompat.getColor(context, R.color.colorCircleStroke))
                .strokeWidth(CIRCLE_STROKE_WIDTH)
                .fillColor(ContextCompat.getColor(context, R.color.colorCircleFill))
    }
}