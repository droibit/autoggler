package com.droibit.autoggler.geofences

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.Trigger
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GeofencesActivity : AppCompatActivity(), GeofencesContract.View {

    private val injector = KodeinInjector()

    private val presenter: GeofencesContract.Presenter by injector.instance()

    private val repository: GeofenceRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofences)

        injector.inject(Kodein {
            extend(appKodein())
            import(geofencesModule(this@GeofencesActivity))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.geofences, menu)
        return true
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
