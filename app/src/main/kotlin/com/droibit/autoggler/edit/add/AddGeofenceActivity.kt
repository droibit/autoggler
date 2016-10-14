package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.data.provider.geometory.CompositeGeometory
import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.provider.rx.RxBus
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.droibit.autoggler.edit.DragActionMode
import com.droibit.autoggler.edit.EditGeofenceContract.Companion.EXTRA_GEOFENCE
import com.droibit.autoggler.edit.EditGeofenceContract.EditGeofenceEvent
import com.droibit.autoggler.edit.EditGeofenceDialogFragment
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GET_LOCATION
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.utils.intent
import com.droibit.autoggler.utils.self
import com.droibit.autoggler.utils.showShortToast
import com.github.droibit.chopstick.bindIntArray
import com.github.droibit.chopstick.bindView
import com.github.droibit.chopstick.findView
import com.github.droibit.rxactivitylauncher.PendingLaunchAction
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.droibit.rxruntimepermissions.GrantResult
import com.github.droibit.rxruntimepermissions.PendingRequestPermissionsAction
import com.github.droibit.rxruntimepermissions.RxRuntimePermissions
import com.github.droibit.rxruntimepermissions.Transforms.toSingleGrantResult
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class AddGeofenceActivity : AppCompatActivity(),
        AddGeofenceContract.View,
        AddGeofenceContract.Navigator,
        AddGeofenceContract.RuntimePermissions,
        GoogleMapView.Callback,
        DragActionMode.Callback,
        KodeinAware {

    companion object {

        @JvmStatic
        private val REQUEST_LOCATION_RESOLUTION = 0

        @JvmStatic
        private val KEY_GEOFENCE = "KEY_GEOFENCE"

        @JvmStatic
        fun createIntent(context: Context) = intent<AddGeofenceActivity>(context)
    }

    private val injector = KodeinInjector()

    private val presenter: AddGeofenceContract.Presenter by injector.instance()

    private val googleMapView: GoogleMapView by injector.instance()

    private val pendingLocationResolution: PendingLaunchAction by injector.instance()

    private val pendingGetLocationPermission: PendingRequestPermissionsAction by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val dragActionMode: DragActionMode by injector.instance()

    private val rxActivityLauncher: RxActivityLauncher by injector.instance()

    private val rxRuntimePermissions: RxRuntimePermissions by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private val geofenceRadiuses: IntArray by bindIntArray(R.array.edit_geofence_circle_radius_items)

    private var compositeGeometry: CompositeGeometory? = null

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        import(editGeofenceModule(activity = self, dragCallback = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_geofence)

        injector.inject(Kodein {
            extend(kodein)

            val geofence = savedInstanceState?.getSerializable(KEY_GEOFENCE)
                    as? Geofence ?: Geofence()
            import(addGeofenceModule(
                    view = self,
                    navigator = self,
                    permissions = self,
                    interactionCallback = self,
                    initialGeofence = geofence
            ))
        })

        val mapView: MapView = findView(R.id.map)
        googleMapView.onCreate(mapView, savedInstanceState) { compositeGeometory ->
            this.compositeGeometry = compositeGeometry
        }

        fab.apply {
            val pendingGeofencingPermissions = PendingRequestPermissionsAction(self).apply {
                fab.tag = this
            }
            subscribeLocationPermission(usage = GEOFENCING, pendingRequestPermissions = pendingGeofencingPermissions)
            setOnClickListener { presenter.onDoneButtonClicked() }
        }

        subscribeLocationPermission(usage = GET_LOCATION, pendingRequestPermissions = pendingGetLocationPermission)
        subscribeLocationResolution()

        // TODO: Review necessary
        if (savedInstanceState == null) {
            presenter.onCreate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        rxActivityLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        rxRuntimePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
        presenter.subscribe()
        subscribeEditGeofence()
    }

    override fun onPause() {
        googleMapView.onPause()
        presenter.unsubscribe()
        subscriptions.clear()
        super.onPause()
    }

    override fun onDestroy() {
        googleMapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        presenter.onSavedInstanceState { outState }
    }

    @SuppressWarnings("PrivateResource")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                presenter.onUpNavigationButtonClicked(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // AddGeofenceContract.View

    override fun saveInstanceState(outStateWrapper: () -> Bundle, geofence: Geofence) {
        val outState = outStateWrapper()
        outState.putSerializable(KEY_GEOFENCE, geofence)

        val compositeGeometry = compositeGeometry
        val options = if (compositeGeometry != null) {
            CompositeGeometory.Options(
                    marker = geometryProvider.newMarkerOptions(compositeGeometry.marker),
                    circle = geometryProvider.newCircleOptions(compositeGeometry.circle)
            )
        } else {
            null
        }
        googleMapView.onSaveInstanceState(outState, options)
    }

    override fun hasGeofenceGeometory(): Boolean {
        return compositeGeometry != null
    }

    override fun canDropMarker(): Boolean {
        return compositeGeometry == null
    }

    override fun dropMarker(point: LatLng) {
        val markerOptions = geometryProvider.newMarkerOptions(point)
        googleMapView.addMarker(markerOptions) { marker ->
            val circleOptions = geometryProvider.newCircleOptions(marker.position, geofenceRadiuses.first().toDouble())
            val circle = googleMapView.addCircle(circleOptions)
            compositeGeometry = CompositeGeometory(marker, circle)

            presenter.onMarkerDropped(marker)
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

    override fun setMarkerInfoWindow(title: String, snippet: String?) {
        checkNotNull(compositeGeometry).apply {
            marker.title = title
            marker.snippet = snippet
            marker.showInfoWindow()
        }
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

    override fun enableMyLocationButton(enabled: Boolean) {
        googleMapView.enableMyLocationButton(enabled)
    }

    override fun setLocation(location: Location) {
        googleMapView.updateMyLocation(location)
    }

    override fun setLocation(location: LatLng) {
        googleMapView.updateMyLocation(location)
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

    override fun showGeofenceCircle() {
        checkNotNull(compositeGeometry).apply {
            circle.center = marker.position
            circle.isVisible = true
        }
    }

    override fun hideGeofenceCircle() {
        checkNotNull(compositeGeometry).apply {
            circle.center = marker.position
            circle.isVisible = false
        }
    }

    override fun setGeofenceRadius(radius: Double) {
        checkNotNull(compositeGeometry).apply {
            circle.radius = radius
        }
    }

    override fun showErrorToast(@StringRes msgId: Int) {
        showShortToast(msgId)
    }

    override fun showLocationPermissionRationaleSnackbar() {
        //TODO()
    }

    // AddGeofenceContract.Navigator

    override fun showLocationResolutionDialog(status: AvailableStatus) {
        pendingLocationResolution {
            Timber.d("pendingLocationResolutionAction")
            status.startResolutionForResult(activity = self, requestCode = REQUEST_LOCATION_RESOLUTION)
        }
    }

    override fun navigationToUp() {
        finish()
    }

    override fun finish(result: Geofence) {
        Timber.d("finish($result)")
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_GEOFENCE, result) })
        finish()
    }

    // AddGeofenceContract.RuntimePermission

    override fun requestLocationPermission(usage: Usage) {
        @Suppress("UNCHECKED_CAST")
        val pendingPermission = when (usage) {
            GET_LOCATION -> pendingGetLocationPermission
            GEOFENCING -> fab.tag as PendingRequestPermissionsAction
        }
        pendingPermission.call()
    }

    // GoogleMapView.Callback

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
        val marker = checkNotNull(compositeGeometry).marker
        presenter.onPrepareDragMode(marker)
    }

    override fun onFinishedDragMode() {
        val marker = checkNotNull(compositeGeometry).marker
        presenter.onFinishedDragMode(marker)
    }

    // private

    private fun subscribeEditGeofence() {
        rxBus.asObservable()
                .ofType(EditGeofenceEvent::class.java)
                .subscribe { event ->
                    when (event) {
                        is EditGeofenceEvent.OnUpdate -> presenter.onGeofenceUpdated(updated = event.geofence)
                    }
                }.addTo(subscriptions)
    }

    private fun subscribeLocationPermission(usage: Usage, pendingRequestPermissions: PendingRequestPermissionsAction) {
        rxRuntimePermissions
                .from(pendingRequestPermissions)
                .requestPermissions(usage.requestCode, ACCESS_FINE_LOCATION)
                .map(toSingleGrantResult())
                .subscribe {
                    presenter.onLocationPermissionsResult(usage, granted = (it == GrantResult.GRANTED))
                }
    }

    private fun subscribeLocationResolution() {
        rxActivityLauncher
                .from(pendingLocationResolution)
                .startActivityForResult(REQUEST_LOCATION_RESOLUTION)
                .subscribe {
                    presenter.onLocationResolutionResult(it.isOk)
                }
    }
}
