package com.droibit.autoggler.edit.add

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.droibit.autoggler.edit.GoogleMapView
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.utils.intent
import com.github.droibit.chopstick.findView
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.MapView

class AddGeofenceActivity : AppCompatActivity(),
        AddGeofenceContract.View,
        AddGeofenceContract.Navigator,
        AddGeofenceContract.RuntimePermissions {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = intent<AddGeofenceActivity>(context)
    }

    private val injector = KodeinInjector()

    private val presenter: AddGeofenceContract.Presenter by injector.instance()

    private val activityLauncher: RxActivityLauncher by injector.instance()

    private val googleMapView: GoogleMapView by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_geofence)

        injector.inject(Kodein {
            extend(appKodein())
            import(editGeofenceModule())

            val thiz = this@AddGeofenceActivity
            import(addGeofenceModule(view = thiz, navigator = thiz, permissions = thiz))
        })

        val mapView: MapView = findView(R.id.map)
        googleMapView.onCreate(mapView, savedInstanceState)

        presenter.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    override fun showLocationResolutionDialog(status: AvailableStatus) {
        // TODO: status.startResolutionForResult()
        TODO()
    }

    override fun enableMyLocationButton(enable: Boolean) {
        TODO()
    }

    override fun showLocation(location: Location) {
        TODO()
    }

    override fun showErrorToast(@StringRes msgId: Int) {
        TODO()
    }

    // AddGeofenceContract.Navigator

    override fun navigationToUp() {
        finish()
    }

    // AddGeofenceContract.RuntimePermission

    override fun requestPermissions(vararg permissions: String) {
        TODO()
    }
}
