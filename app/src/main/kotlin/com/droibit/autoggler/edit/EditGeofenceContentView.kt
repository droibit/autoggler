package com.droibit.autoggler.edit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.github.droibit.chopstick.bindView
import com.linearlistview.LinearListView


class EditGeofenceContentView : LinearLayout {

    private val geofenceRadiusView: Spinner by bindView(R.id.geofence_radius)

    private val toggleListView: LinearListView by bindView(R.id.toggle_list)

    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.view_edit_geofence, this)

        geofenceRadiusView.adapter = ArrayAdapter.createFromResource(
                context, R.array.edit_geofence_circle_radius_labels, R.layout.list_item_geofence_radius)
    }

    fun init(srcGeofence: Geofence) {
        toggleListView.adapter = ToggleAdapter(context, srcGeofence)
    }
}