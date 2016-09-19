package com.droibit.autoggler.edit.update

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.droibit.autoggler.R
import com.droibit.autoggler.utils.intent

class UpdateGeofenceActivity : AppCompatActivity() {

    companion object {

        private val EXTRA_GEOFENCE_ID = "EXTRA_GEOFEONCE_ID"

        @JvmStatic
        fun createIntent(context: Context, geofenceId: Long): Intent {
            return intent<UpdateGeofenceActivity>(context).apply {
                putExtra(EXTRA_GEOFENCE_ID, geofenceId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_geofence)
    }
}
