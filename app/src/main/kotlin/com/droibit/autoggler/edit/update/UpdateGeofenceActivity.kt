package com.droibit.autoggler.edit.update

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.data.provider.geometory.CompositeGeometory
import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.provider.rx.RxBus
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.DragActionMode
import com.droibit.autoggler.edit.PendingRuntimePermissions
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions
import com.droibit.autoggler.utils.intent
import com.droibit.autoggler.utils.self
import com.github.droibit.chopstick.bindIntArray
import com.github.droibit.chopstick.bindView
import com.github.droibit.rxruntimepermissions.RxRuntimePermissions
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker
import rx.subscriptions.CompositeSubscription

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

    private val pendingGetLocationPermission: PendingRuntimePermissions by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val dragActionMode: DragActionMode by injector.instance()

    private val rxRuntimePermissions: RxRuntimePermissions by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val mapView: MapView by bindView(R.id.map)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private val geofenceRadiuses: IntArray by bindIntArray(R.array.edit_geofence_circle_radius_items)

    private lateinit var editableCompositeGeometory: CompositeGeometory

    private var uneditableCompositeGeometories: List<CompositeGeometory>? = null

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
    }

    override fun onPause() {
        googleMapView.onPause()
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
        menuInflater.inflate(R.menu.update_geofence, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    // UpdateGeofenceContract.View

    override fun saveInstanceState(target: Geofence, outStateWrapper: () -> Bundle) {
        TODO()
    }

    override fun showEditableGeofence(geofence: Geofence) {
        TODO()
    }

    override fun showUneditableGeofences(geofences: List<Geofence>) {
        TODO()
    }

    override fun isEditableMarker(marker: Marker): Boolean {
        // TODO: marker.tag as String == editableCompositeGeometory.marker
        TODO()
    }

    override fun showMarkerInfoWindow(marker: Marker) {
        TODO()
    }

    override fun hideMarkerInfoWindow(marker: Marker) {
        TODO()
    }

    override fun isDragActionModeShown(): Boolean {
        TODO()
    }

    override fun showEditDialog(target: Geofence) {
        TODO()
    }

    override fun startMarkerDragMode() {
        TODO()
    }

    override fun setDoneButtonEnabled(enabled: Boolean) {
        TODO()
    }

    override fun showDoneButton() {
        TODO()
    }

    override fun hideDoneButton() {
        TODO()
    }

    override fun showGeofenceCircle() {
        TODO()
    }

    override fun hideGeofenceCircle() {
        TODO()
    }

    override fun setGeofenceRadius(radius: Double) {
        TODO()
    }

    override fun showErrorToast(msgId: Int) {
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
