package com.droibit.autoggler.geofences

import android.content.Intent
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
import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.provider.rx.RxBus
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.EditGeofenceContract.Companion.EXTRA_GEOFENCE
import com.droibit.autoggler.edit.add.AddGeofenceActivity
import com.droibit.autoggler.geofences.GeofencesContract.EditGeofenceEvent
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import com.github.droibit.chopstick.bindView
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.jakewharton.rxrelay.PublishRelay
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

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

    private val geofencesView: RecyclerView by bindView(R.id.list)

    private val rxActivityLauncher: RxActivityLauncher by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val emptyView: View by bindView(R.id.empty)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var geofenceAdapter: GeofenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofences)

        injector.inject(Kodein {
            extend(appKodein())

            val self = this@GeofencesActivity
            import(geofencesModule(view = self, navigator = self))
        })

        fab.apply {
            val intent = AddGeofenceActivity.createIntent(this@GeofencesActivity)
            rxActivityLauncher.from(this@GeofencesActivity)
                    .on(PublishRelay.create<Any>().apply { fab.tag = this })
                    .startActivityForResult(intent, REQUEST_ADD_GEOFENCE, null)
                    .filter { it.isOk }
                    .map { it.data?.getSerializableExtra(EXTRA_GEOFENCE) as Geofence }
                    .subscribe {
                        Timber.d("result=$it")
                        rxBus.call(EditGeofenceEvent.OnAdd(geofence = it))
                    }

            setOnClickListener { presenter.onGeofenceAddButtonClicked() }
        }

        geofenceAdapter = GeofenceAdapter(this, geometryProvider).apply {
            itemClickListener = { geofence ->
                Toast.makeText(this@GeofencesActivity, "$geofence", Toast.LENGTH_SHORT).show()
            }
            popupItemClickListener = { menuItem, geofence ->
                Toast.makeText(this@GeofencesActivity, "Delete: ${geofence.id}", Toast.LENGTH_SHORT).show()
            }
        }

        geofencesView.apply {
            layoutManager = LinearLayoutManager(this@GeofencesActivity)
            adapter = geofenceAdapter
            setHasFixedSize(true)
        }

        presenter.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        rxActivityLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
        subscribeEditGeofence()
    }

    override fun onPause() {
        presenter.unsubscribe()
        subscriptions.clear()
        super.onPause()
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
        emptyView.visibility = View.GONE
        geofencesView.visibility = View.VISIBLE
        geofenceAdapter.addAll(geofences)
    }

    override fun showNoGeofences() {
        emptyView.visibility = View.VISIBLE
        geofencesView.visibility = View.GONE
        geofenceAdapter.clear()
    }

    override fun showGeofence(geofence: Geofence) {
        emptyView.visibility = View.GONE
        geofencesView.visibility = View.VISIBLE
        geofenceAdapter.add(geofence)
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

    // Private

    private fun subscribeEditGeofence() {
        rxBus.asObservable()
                .ofType(EditGeofenceEvent::class.java)
                .subscribe {
                    Timber.d("subscribeEditGeofence($it)")
                    when (it) {
                        is EditGeofenceEvent.OnAdd -> presenter.onAddGeofenceResult(newGeofence = it.geofence)
                    }
                }.addTo(subscriptions)
    }
}
