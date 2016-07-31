package com.droibit.autoggler.geofences

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.droibit.autoggler.R

class GeofencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofences)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.geofences, menu)
        return true
    }
}
