package com.droibit.autoggler.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.EditGeofenceContract.ToggleItem
import com.droibit.autoggler.edit.EditGeofenceContract.ToggleItemRes

class ToggleAdapter : ArrayAdapter<ToggleItem> {

    private val geofence: Geofence

    private val inflater: LayoutInflater

    constructor(context: Context, geofence: Geofence) : super(context, 0) {
        this.geofence = geofence
        this.inflater = LayoutInflater.from(context)

        this.add(ToggleItem(itemRes = ToggleItemRes.WIFI, enabled = geofence.toggle.wifi))
        this.add(ToggleItem(itemRes = ToggleItemRes.VIBRATION, enabled = geofence.toggle.vibration))
    }

    @SuppressWarnings("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.list_item_toggle, parent, false) as ToggleItemView
        return view.apply {
            val item = getItem(position)
            bind(item) { isChecked -> item.enabled = isChecked }
        }
    }
}