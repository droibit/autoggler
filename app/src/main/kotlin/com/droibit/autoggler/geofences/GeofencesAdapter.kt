package com.droibit.autoggler.geofences

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.droibit.autoggler.R
import com.droibit.autoggler.data.geometory.GeometryProvider
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.latLong
import com.github.droibit.chopstick.bindView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle as CircleGeometry

class GeofencesAdapter(context: Context, private val geometryProvider: GeometryProvider) :
        ArrayAdapter<Geofence>(context, R.layout.list_item_geofence) {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflateView(parent).apply { tag = ViewHolder(geometryProvider, view = this) }
        return view.apply {
            val viewHolder = tag as ViewHolder
            viewHolder.bind(geofence = getItem(position))
        }
    }

    private fun inflateView(parent: ViewGroup) = inflater.inflate(R.layout.list_item_geofence, parent, false)
}

/**
 * [LiteListDemoActivity#ViewHolder](https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/LiteListDemoActivity.java)
 */
private class ViewHolder(private val provider: GeometryProvider, view: View) : OnMapReadyCallback {

    companion object {
        private val KEY_CIRCLE = R.id.key_circle
        private val KEY_CIRCLE_GEO = R.id.key_circle_geometry
    }

    private val mapView: MapView by view.bindView(R.id.map)

    private val iconView: ImageView by view.bindView(R.id.enabled_icon)

    private val nameView: TextView by view.bindView(R.id.genfence_name)

    private var googleMap: GoogleMap? = null

    init {
        mapView.apply {
            onCreate(null)
            getMapAsync(this@ViewHolder)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val circle = mapView.getTag(KEY_CIRCLE) as? Circle
        if (circle != null) {
            updateGoogleMap(googleMap, src = circle)
            mapView.setTag(KEY_CIRCLE, null)
        }
    }

    fun bind(geofence: Geofence) {
        // TODO: geofence.enabled

        nameView.text = geofence.name

        val googleMap = googleMap
        if (googleMap != null) {
            updateGoogleMap(googleMap, src = geofence.circle)
        } else {
            mapView.setTag(KEY_CIRCLE, geofence.circle)
        }
    }

    private fun updateGoogleMap(googleMap: GoogleMap, src: Circle) {
        val circleGeo = mapView.getTag(KEY_CIRCLE_GEO) as? CircleGeometry
        if (circleGeo == null) {
            googleMap.addCircle(provider.newCircle(src)).apply {
                mapView.setTag(KEY_CIRCLE_GEO, this)
            }
            return
        }
        circleGeo.center = src.latLong

        CameraUpdateFactory.newLatLng(src.latLong).apply {
            googleMap.moveCamera(this)
        }
    }
}