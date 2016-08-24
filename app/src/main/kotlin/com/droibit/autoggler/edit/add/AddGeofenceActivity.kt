package com.droibit.autoggler.edit.add

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.droibit.autoggler.R
import com.droibit.autoggler.edit.editGeofenceModule
import com.droibit.autoggler.utils.intent
import com.github.droibit.chopstick.bindView
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.MapView

class AddGeofenceActivity : AppCompatActivity(),
        AddGeofenceContract.View,
        AddGeofenceContract.Navigator {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = intent<AddGeofenceActivity>(context)
    }

    private val injector = KodeinInjector()

    private val presenter: AddGeofenceContract.Presenter by injector.instance()

    private val activityLauncher: RxActivityLauncher by injector.instance()

    private val mapView: MapView by bindView(R.id.map)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_geofence)

        injector.inject(Kodein {
            extend(appKodein())
            import(editGeofenceModule())

            val self = this@AddGeofenceActivity
            import(addGeofenceModule(view = self, navigator = self))
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
        }

        presenter.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
        mapView.onResume()
    }

    override fun onPause() {
        presenter.unsubscribe()
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
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

    override fun showLocationResolutionDialog(status: Status) {
        // TODO: status.startResolutionForResult()
    }

    override fun navigationToUp() {
        finish()
    }
}
