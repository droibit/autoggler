package com.droibit.autoggler.geofences

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

    private val recyclerView: RecyclerView by bindView(R.id.list)

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

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@GeofencesActivity)

            adapter = GeofencesAdapter(this@GeofencesActivity, geometryProvider).apply {
                listAdapter = this
            }
        }

        listAdapter.itemClickListener = { geofence ->
            Toast.makeText(this@GeofencesActivity, "$geofence", Toast.LENGTH_SHORT).show()
        }

        listAdapter.addAll(
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.7121228,139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = false,
                        circle = Circle(35.3121228,139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.4121228,139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.5121228,139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = false,
                        circle = Circle(35.6121228,139.7740507, 500.0),
                        trigger = Trigger()
                )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.geofences, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navItem = NavItem.from(item.itemId)
        return presenter.onMenuItemSelected(navItem)
    }

    // GeofencesContract.View

    override fun showGeofences(geofences: List<Geofence>) {
        TODO()
    }

    override fun showNoGeofences() {
        TODO()
    }

    override fun hideGeofence(geofence: Geofence): Int {
        TODO()
    }

    override fun showDeleteConfirmDialog(targetId: Long) {
        TODO()
    }

    // GeofenceContract.Navigator

    override fun navigateSettings() {
        TODO()
    }

    override fun navigateAddGeofence() {
        TODO()
    }

    override fun navigateUpdateGeofence(id: Long) {
        TODO()
    }
}
