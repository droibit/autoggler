package com.droibit.autoggler.edit.add

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.droibit.autoggler.R
import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.droibit.autoggler.edit.*
import com.droibit.autoggler.utils.intent
import com.github.droibit.chopstick.bindView
import com.github.droibit.chopstick.findView
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import timber.log.Timber

class AddGeofenceActivity : AppCompatActivity(),
        AddGeofenceContract.View,
        AddGeofenceContract.Navigator,
        AddGeofenceContract.RuntimePermissions,
        GoogleMapView.Listener,
        DragActionMode.Callback {

    companion object {

        @JvmStatic
        private val REQUEST_PERMISSION_ACCESS_LOCATION = 0
        @JvmStatic
        private val REQUEST_LOCATION_RESOLUTION = 0

        @JvmStatic
        fun createIntent(context: Context) = intent<AddGeofenceActivity>(context)
    }

    private val injector = KodeinInjector()

    private val presenter: AddGeofenceContract.Presenter by injector.instance()

    private val activityLauncher: RxActivityLauncher by injector.instance()

    private val googleMapView: GoogleMapView by injector.instance()

    private val locationResolutionSource: LocationResolutionSource by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val dragActionMode: DragActionMode by injector.instance()

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private var compositeGeometry: CompositeGeometory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_geofence)

        injector.inject(Kodein {
            extend(appKodein())

            val self = this@AddGeofenceActivity
            import(editGeofenceModule(activity = self, interactionListener = self, dragCallback = self))
            import(addGeofenceModule(view = self, navigator = self, permissions = self))
        })

        val mapView: MapView = findView(R.id.map)
        googleMapView.onCreate(mapView, savedInstanceState)

        fab.apply {
            setOnClickListener { presenter.onDoneButtonClicked() }
        }

        activityLauncher
                .from { locationResolutionSource.startResolutionForResult() }
                .on(locationResolutionSource.trigger)
                .startActivityForResult(Intent(), REQUEST_LOCATION_RESOLUTION, null)
                .subscribe {
                    presenter.onLocationResolutionResult(it.isOk)
                }

        // TODO: Review necessary
        if (savedInstanceState == null) {
            presenter.onCreate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // TODO: Using RxJava
        when (requestCode) {
            REQUEST_PERMISSION_ACCESS_LOCATION -> presenter.onRequestPermissionsResult(grantResults)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        googleMapView.onPause()
        presenter.unsubscribe()
        super.onPause()
    }

    override fun onDestroy() {
        googleMapView.onDestroy()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                presenter.onUpNavigationButtonClicked(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // AddGeofenceContract.View

    override fun canDropMarker(): Boolean {
        return compositeGeometry == null
    }

    override fun dropMarker(point: LatLng) {
        val markerOptions = geometryProvider.newMarkerOptions(point)
        googleMapView.addMarker(markerOptions) { marker ->
            val radius = resources.getIntArray(R.array.edit_geofence_circle_radius_items).first()
            val circleOptions = geometryProvider.newCircleOptions(marker.position, radius.toDouble())
            val circle = googleMapView.addCircle(circleOptions)
            compositeGeometry = CompositeGeometory(marker, circle)
        }
    }

    override fun isDragActionModeShown(): Boolean {
        return dragActionMode.isShown
    }

    override fun showMarkerInfoWindow(marker: Marker) {
        marker.showInfoWindow()
    }

    override fun hideMarkerInfoWindow(marker: Marker) {
        marker.hideInfoWindow()
    }

    override fun showEditDialog() {
        EditGeofenceDialogFragment.newInstance(Geofence())
                .show(supportFragmentManager)
    }

    override fun startMarkerDragMode() {
        startSupportActionMode(dragActionMode)
    }

    override fun enableMyLocationButton(enabled: Boolean) {
        googleMapView.enableMyLocationButton(enabled)
    }

    override fun showLocation(location: Location) {
        googleMapView.updateMyLocation(location)
    }

    override fun showDoneButton() {
        fab.show()
    }

    override fun hideDoneButton() {
        fab.hide()
    }

    override fun showGeofenceCircle() {
        compositeGeometry?.let {
            it.circle.center = it.marker.position
            it.circle.isVisible = true
        }
    }

    override fun hideGeofenceCircle() {
        compositeGeometry?.let {
            it.circle.center = it.marker.position
            it.circle.isVisible = false
        }
    }

    override fun showErrorToast(@StringRes msgId: Int) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show()
    }

    // AddGeofenceContract.Navigator

    override fun showLocationResolutionDialog(status: AvailableStatus) {
        locationResolutionSource.prepareStartResolution {
            Timber.d("prepareStartResolution")
            status.startResolutionForResult(this@AddGeofenceActivity, REQUEST_LOCATION_RESOLUTION)
        }
    }

    override fun navigationToUp() {
        finish()
    }

    // AddGeofenceContract.RuntimePermission

    override fun requestPermissions(vararg permissions: String) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_ACCESS_LOCATION)
    }

    // GoogleMapView.Listener

    override fun onMapLongClick(point: LatLng) {
        presenter.onMapLongClicked(point)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.onMarkerClicked(marker)
        return true
    }

    override fun onInfoWindowClick(marker: Marker) {
        presenter.onMarkerInfoWindowClicked()
    }

    override fun onMarkerDragStart(marker: Marker) {
        presenter.onMarkerDragStart(marker)
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.onMarkerDragEnd()
    }

    // MoveActionMode.Callback

    override fun onPrepareDragMode() {
        presenter.onPrepareDragMode()
    }

    override fun onFinishedDragMode() {
        presenter.onFinishedDragMode()
    }
}
