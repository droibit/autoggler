package com.droibit.autoggler.toggles

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.droibit.autoggler.R

class TogglesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toggles)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toggles, menu)
        return true
    }
}
