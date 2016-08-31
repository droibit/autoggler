package com.droibit.autoggler.edit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.droibit.autoggler.R
import com.github.droibit.chopstick.bindView


class EditGeofenceView : LinearLayout {

    private val geofenceRadiusView: Spinner by bindView(R.id.geofence_radius)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.view_edit_geofence, this)

        geofenceRadiusView.adapter = ArrayAdapter.createFromResource(
                context, R.array.edit_geofence_circle_radius_labels, R.layout.list_item_geofence_radius)
    }
}