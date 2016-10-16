package com.droibit.autoggler.edit.update

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.data.provider.geometory.CompositeGeometry
import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.provider.rx.RxBus
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.DragActionMode
import com.droibit.autoggler.edit.EditGeofenceContract
import com.droibit.autoggler.edit.EditGeofenceDialogFragment
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions.Usage
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.droibit.autoggler.utils.intent
import com.droibit.autoggler.utils.self
import com.droibit.autoggler.utils.showShortToast
import com.github.droibit.chopstick.bindView
import com.github.droibit.rxruntimepermissions.GrantResult
import com.github.droibit.rxruntimepermissions.PendingRequestPermissionsAction
import com.github.droibit.rxruntimepermissions.RxRuntimePermissions
import com.github.droibit.rxruntimepermissions.Transforms
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class UpdateGeofenceActivity : AppCompatActivity(),
        UpdateGeofenceContract.View,
        UpdateGeofenceContract.Navigator,
        RuntimePermissions,
        GoogleMapView.Callback,
        DragActionMode.Callback,
        KodeinAware {

    companion object {

        @JvmStatic
        private val EXTRA_GEOFENCE = "EXTRA_GEOFENCE"

        @JvmStatic
        private val KEY_GEOFENCE = "KEY_GEOFENCE"

        @JvmStatic
        fun createIntent(context: Context, geofence: Geofence): Intent {
            return intent<UpdateGeofenceActivity>(context).apply {
                putExtra(EXTRA_GEOFENCE, geofence)
            }
        }
    }

    private val injector = KodeinInjector()

    private val presenter: UpdateGeofenceContract.Presenter by injector.instance()

    private val googleMapView: GoogleMapView by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val dragActionMode: DragActionMode by injector.instance()

    private val rxRuntimePermissions: RxRuntimePermissions by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val mapView: MapView by bindView(R.id.map)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var editableCompositeGeometory: CompositeGeometry

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        import(editGeofenceModule(activity = self, dragCallback = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_geofence)

        injector.inject(Kodein {
            extend(kodein)

            val geofence = savedInstanceState?.getSerializable(KEY_GEOFENCE) as? Geofence
                    ?: intent.getSerializableExtra(EXTRA_GEOFENCE) as Geofence

            import(updateGeofenceModule(
                    view = self,
                    navigator = self,
                    permissions = self,
                    interactionCallback = self,
                    initialGeofence = geofence
            ))
        })

        fab.apply {
            val pendingGeofencingPermissions = PendingRequestPermissionsAction(self).apply {
                fab.tag = this
            }
            subscribeLocationPermission(usage = GEOFENCING, pendingRequestPermissions = pendingGeofencingPermissions)
            setOnClickListener { presenter.onDoneButtonClicked() }
        }

        googleMapView.apply {
            getMapAsync(rawMapView = mapView)
                    .subscribe { restoredCompositeGeometory ->
                        if (restoredCompositeGeometory != null) {
                            editableCompositeGeometory = restoredCompositeGeometory
                        }
                        presenter.onMapReady(isRestoredGeometory = restoredCompositeGeometory != null)
                    }
            onCreate(savedInstanceState)
        }

        //presenter.onCreate()
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
        subscribeEditGeofence()
    }

    override fun onPause() {
        googleMapView.onPause()
        subscriptions.clear()
        super.onPause()
    }

    override fun onDestroy() {
        googleMapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.update_geofence, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    // UpdateGeofenceContract.View

    override fun saveInstanceState(target: Geofence, outStateWrapper: () -> Bundle) {
        val outState = outStateWrapper()
        outState.putSerializable(KEY_GEOFENCE, target)

        val options = CompositeGeometry.Options(
                marker = geometryProvider.newMarkerOptions(editableCompositeGeometory.marker),
                circle = geometryProvider.newCircleOptions(editableCompositeGeometory.circle)
        )
        googleMapView.onSaveInstanceState(outState, options)
    }

    override fun showEditableGeofence(geofence: Geofence) {
        editableCompositeGeometory = addCompositeGeometory(src = geofence, editable = true).apply {
            marker.title = geofence.name
            marker.tag = geofence.id
            marker.showInfoWindow()
        }
    }

    override fun showUneditableGeofences(geofences: List<Geofence>) {
        geofences.forEach {
            addCompositeGeometory(src = it, editable = false)
        }
    }

    override fun isEditableMarker(marker: Marker): Boolean {
        return editableCompositeGeometory.marker.tag == marker.tag
    }

    override fun showMarkerInfoWindow(marker: Marker) {
        marker.showInfoWindow()
    }

    override fun hideMarkerInfoWindow(marker: Marker) {
        checkNotNull(editableCompositeGeometory).apply {
            circle.center = marker.position
            circle.isVisible = false
        }
    }

    override fun isDragActionModeShown(): Boolean {
        return dragActionMode.isShown
    }

    override fun showEditDialog(target: Geofence) {
        val df = EditGeofenceDialogFragment.newInstance(target).apply {
            isCancelable = false
        }
        df.show(supportFragmentManager)
    }

    override fun startMarkerDragMode() {
        startSupportActionMode(dragActionMode)
    }

    override fun setDoneButtonEnabled(enabled: Boolean) {
        fab.isEnabled = enabled
    }

    override fun showDoneButton() {
        fab.show()
    }

    override fun hideDoneButton() {
        fab.hide()
    }

    override fun showEditableGeofenceCircle() {
        editableCompositeGeometory.apply {
            circle.center = marker.position
            circle.isVisible = true
        }
    }

    override fun hideEditableGeofenceCircle() {
        editableCompositeGeometory.apply {
            circle.center = marker.position
            circle.isVisible = false
        }
    }

    override fun setMarkerInfoWindow(title: String, snippet: String?) {
        editableCompositeGeometory.apply {
            marker.title = title
            marker.snippet = snippet
            marker.showInfoWindow()
        }
    }

    override fun setGeofenceRadius(radius: Double) {
        editableCompositeGeometory.apply {
            circle.radius = radius
        }
    }

    override fun setLocation(location: LatLng) {
        googleMapView.updateMyLocation(location)
    }

    override fun showErrorToast(msgId: Int) {
        showShortToast(msgId)
    }

    // UpdateGeofenceContract.Navigator

    override fun navigateToUp() {
        finish()
    }

    override fun finish(result: Geofence) {
        Timber.d("finish($result)")
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_GEOFENCE, result) })
        finish()
    }

    // UpdateGeofenceContract.RuntimePermissions

    override fun requestLocationPermission(usage: Usage) {
        val pendingPermission = fab.tag as PendingRequestPermissionsAction
        pendingPermission.call()
    }

    // GoogleMapView.Callback

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.onMarkerClicked(marker)
        return true
    }

    override fun onMarkerDragStart(marker: Marker) {
        presenter.onMarkerDragStart(marker)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.onMarkerDragEnd()
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onInfoWindowClick(marker: Marker) {
        presenter.onMarkerInfoWindowClicked(marker)
    }

    // DragActionMode.Callback

    override fun onPrepareDragMode() {
        presenter.onPrepareDragMode(editableCompositeGeometory.marker)
    }

    override fun onFinishedDragMode() {
        presenter.onFinishedDragMode(editableCompositeGeometory.marker)
    }

    // Private

    private fun subscribeEditGeofence() {
        rxBus.asObservable()
                .ofType(EditGeofenceContract.EditGeofenceEvent::class.java)
                .subscribe { event ->
                    when (event) {
                        is EditGeofenceContract.EditGeofenceEvent.OnUpdate -> presenter.onGeofenceUpdated(updated = event.geofence)
                    }
                }.addTo(subscriptions)
    }

    private fun addCompositeGeometory(src: Geofence, editable: Boolean): CompositeGeometry {
        val markerOptions = if (editable) {
            geometryProvider.newMarkerOptions(src.latLong, showSnippet = false)
        } else {
            geometryProvider.newUneditableMarkerOptions(src.latLong)
        }
        val marker = googleMapView.addMarker(markerOptions)

        val circleOptions = geometryProvider.newCircleOptions(src.latLong, src.radius)
        val circle = googleMapView.addCircle(circleOptions)

        return CompositeGeometry(marker, circle)
    }

    private fun subscribeLocationPermission(usage: Usage, pendingRequestPermissions: PendingRequestPermissionsAction) {
        rxRuntimePermissions
                .from(pendingRequestPermissions)
                .requestPermissions(usage.requestCode, ACCESS_FINE_LOCATION)
                .map(Transforms.toSingleGrantResult())
                .subscribe {
                    presenter.onLocationPermissionsResult(usage, granted = (it == GrantResult.GRANTED))
                }
    }
}
