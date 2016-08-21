package com.droibit.autoggler.geofences

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.droibit.autoggler.R
import com.droibit.autoggler.data.geometory.GeometryProvider
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.Trigger
import com.droibit.autoggler.edit.add.AddGeofenceActivity
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import com.github.droibit.chopstick.bindView
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.jakewharton.rxrelay.PublishRelay

class GeofencesActivity : AppCompatActivity(),
        GeofencesContract.View,
        GeofencesContract.Navigator {

    companion object {
        @JvmStatic
        private val REQUEST_ADD_GEOFENCE = 1
    }

    private val injector = KodeinInjector()

    private val presenter: GeofencesContract.Presenter by injector.instance()

    private val geometryProvider: GeometryProvider by injector.instance()

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val activityLauncher: RxActivityLauncher by injector.instance()

    private val emptyView: View by bindView(R.id.empty)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var listAdapter: GeofencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofences)

        injector.inject(Kodein {
            extend(appKodein())

            import(geofencesModule(
                    view = this@GeofencesActivity,
                    navigator = this@GeofencesActivity
            ))
        })

        fab.apply {
            val clickObservable = PublishRelay.create<Any>()
            tag = clickObservable

            setOnClickListener { presenter.onGeofenceAddButtonClicked() }

            val intent = AddGeofenceActivity.createIntent(this@GeofencesActivity)
            activityLauncher.from(this@GeofencesActivity)
                .on(clickObservable)
                .startActivityForResult(intent, REQUEST_ADD_GEOFENCE, null)
                .subscribe {  }
        }

        listAdapter = GeofencesAdapter(this, geometryProvider).apply {
            itemClickListener = { geofence ->
                Toast.makeText(this@GeofencesActivity, "$geofence", Toast.LENGTH_SHORT).show()
            }
            popupItemClickListener = { menuItem, geofence ->
                Toast.makeText(this@GeofencesActivity, "Delete: ${geofence.id}", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@GeofencesActivity)
            adapter = listAdapter
            setHasFixedSize(true)
        }

        listAdapter.addAll(
                Geofence(id = 1L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.7121228, 139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 2L,
                        name = "テスト",
                        enabled = false,
                        circle = Circle(35.3121228, 139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 3L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.4121228, 139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 4L,
                        name = "テスト",
                        enabled = true,
                        circle = Circle(35.5121228, 139.7740507, 500.0),
                        trigger = Trigger()
                ),
                Geofence(id = 5L,
                        name = "テスト",
                        enabled = false,
                        circle = Circle(35.6121228, 139.7740507, 500.0),
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

    override fun hideGeofence(geofence: Geofence) {
        TODO()
    }

    override fun showDeleteConfirmDialog(targetId: Long) {
        TODO()
    }

    override fun showGeofenceErrorToast() {
        TODO()
    }

    // GeofenceContract.Navigator

    override fun navigateSettings() {
        TODO()
    }

    override fun navigateAddGeofence() {
        @Suppress("UNCHECKED_CAST")
        val subject = fab.tag as PublishRelay<Any>
        subject.call(null)
    }

    override fun navigateUpdateGeofence(id: Long) {
        TODO()
    }
}
