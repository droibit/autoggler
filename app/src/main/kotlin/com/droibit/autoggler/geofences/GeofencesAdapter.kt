package com.droibit.autoggler.geofences

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.droibit.autoggler.R
import com.droibit.autoggler.data.geometory.GeometryProvider
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.latLng
import com.github.droibit.chopstick.bindView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import java.util.*
import com.google.android.gms.maps.model.Circle as CircleGeometry

class GeofencesAdapter(context: Context, private val geometryProvider: GeometryProvider) :
        RecyclerView.Adapter<ViewHolder>() {

    private val geofences = ArrayList<Geofence>()

    private val inflater = LayoutInflater.from(context)

    var itemClickListener: ((Geofence)->Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(geofences[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_item_geofence, parent, false)
        return ViewHolder(view, geometryProvider).apply {
            clickListener { itemClickListener?.invoke(geofences[adapterPosition]) }
        }
    }

    override fun getItemCount() = geofences.size

    fun addAll(vararg geofences: Geofence) {
        this.geofences.clear()
        this.geofences.addAll(geofences)
        this.notifyDataSetChanged()
    }
}

/**
 * [LiteListDemoActivity#ViewHolder](https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/LiteListDemoActivity.java)
 */
class ViewHolder(view: View,
                 private val provider: GeometryProvider) :
        RecyclerView.ViewHolder(view), OnMapReadyCallback {

    companion object {
        private val KEY_CIRCLE = R.id.key_circle
        private val KEY_CIRCLE_GEO = R.id.key_circle_geometry
    }

    private val mapOverlay: View by bindView(R.id.map_overlay)

    private val mapView: MapView by bindView(R.id.map)

    private val iconView: ImageView by bindView(R.id.enabled_icon)

    private val nameView: TextView by bindView(R.id.genfence_name)

    private var googleMap: GoogleMap? = null

    init {
        mapView.apply {
            onCreate(null)
            getMapAsync(this@ViewHolder)
        }
    }

    fun clickListener(listener: (View)->Unit) = mapOverlay.setOnClickListener(listener)

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.uiSettings.apply {
            isMapToolbarEnabled = false
        }

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
        } else {
            circleGeo.center = src.latLng
        }

        CameraUpdateFactory.newLatLng(src.latLng).apply {
            googleMap.moveCamera(this)
        }
    }
}