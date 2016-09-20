package com.droibit.autoggler.edit.update

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.DragActionMode
import com.droibit.autoggler.edit.GoogleMapView
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions
import com.droibit.autoggler.utils.intent
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.lazy
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class UpdateGeofenceActivity : AppCompatActivity(),
        UpdateGeofenceContract.View,
        UpdateGeofenceContract.Navigator,
        RuntimePermissions,
        GoogleMapView.Callback,
        DragActionMode.Callback,
        KodeinAware {

    companion object {

        @JvmStatic
        private val EXTRA_GEOFENCE_ID = "EXTRA_GEOFENCE_ID"

        @JvmStatic
        private val KEY_GEOFENCE = "KEY_GEOFENCE"

        @JvmStatic
        fun createIntent(context: Context, geofenceId: Long): Intent {
            return intent<UpdateGeofenceActivity>(context).apply {
                putExtra(EXTRA_GEOFENCE_ID, geofenceId)
            }
        }
    }

    private val injector = KodeinInjector()

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        val self = this@UpdateGeofenceActivity
        import(editGeofenceModule(activity = self, interactionCallback = self, dragCallback = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_geofence)

        injector.inject(Kodein {
            extend(kodein)

            val self = this@UpdateGeofenceActivity
            val geofenceId = intent.getLongExtra(EXTRA_GEOFENCE_ID, -1L)
            val geofence = savedInstanceState?.getSerializable(KEY_GEOFENCE) as? Geofence
                    ?: Geofence().apply { id = geofenceId }

            import(updateGeofenceModule(view = self, navigator = self, permissions = self, initialGeofence = geofence))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.update_geofence, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    // UpdateGeofenceContract.View

    override fun showEditableGeofence(geofence: Geofence) {
        TODO()
    }

    override fun showUneditableGeofences(geofences: List<Geofence>) {
        TODO()
    }

    // UpdateGeofenceContract.Navigator

    override fun navigationToUp() {
        TODO()
    }

    override fun finish(result: Geofence) {
        TODO()
    }

    // UpdateGeofenceContract.RuntimePermissions
    override fun requestLocationPermission(usage: RuntimePermissions.Usage) {
        TODO()
    }

    // GoogleMapView.Callback

    override fun onMapLongClick(point: LatLng) {
        TODO()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        TODO()
    }

    override fun onMarkerDragEnd(marker: Marker) {
        TODO()
    }

    override fun onMarkerDragStart(marker: Marker) {
        TODO()
    }

    override fun onMarkerDrag(marker: Marker) {
        TODO()
    }

    override fun onInfoWindowClick(marker: Marker) {
        TODO()
    }

    // DragActionMode.Callback

    override fun onPrepareDragMode() {
        TODO()
    }

    override fun onFinishedDragMode() {
        TODO()
    }
}
