package com.droibit.autoggler.geofences

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.droibit.autoggler.R
import com.droibit.autoggler.data.geometory.GeometryProvider
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.Trigger
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GeofencesActivity : AppCompatActivity(),
        GeofencesContract.View,
        GeofencesContract.Navigator {

    private val injector = KodeinInjector()

    private val presenter: GeofencesContract.Presenter by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val repository: GeofenceRepository by injector.instance()

    private val listView: ListView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var listAdapter: GeofencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofences)

        injector.inject(Kodein {
            extend(appKodein())

            val self = this@GeofencesActivity
            import(geofencesModule(view = self, navigator = self))
        })

        listView.adapter = GeofencesAdapter(this, geometryProvider).apply {
            listAdapter = this
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.geofences, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navItem = NavItem.from(item.itemId)
        return presenter.onMenuItemSelected(navItem)
    }

    override fun navigateSettings() {
        TODO()
    }

    override fun showGeofences(geofences: List<Geofence>) {
        TODO()
    }

    override fun showNoGeofences() {
        TODO()
    }

    override fun navigateAddGeofence() {
        TODO()
    }

    override fun navigateUpdateGeofence(id: Long) {
        TODO()
    }

    fun onClickCount(v: View) {
        repository.loadGeofences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(this@GeofencesActivity, "Count: ${it.size}", Toast.LENGTH_SHORT).show()
                }
    }

    fun onClickAdd(v: View) {
        repository.addGeofence(name = "hoge", circle = Circle(1.0, 2.0, 3.0), trigger = Trigger(true, false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(this@GeofencesActivity, "$it", Toast.LENGTH_SHORT).show()
                }
    }
}
