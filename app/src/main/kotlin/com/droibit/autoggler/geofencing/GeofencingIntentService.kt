package com.droibit.autoggler.geofencing

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.droibit.autoggler.utils.intent


class GeofencingIntentService : IntentService(GeofencingIntentService::class.java.simpleName) {

    companion object {

        @JvmStatic
        fun createIntent(context: Context, id: Long): Intent {
            return intent<GeofencingIntentService>(context)
                    .putExtra(EXTRA_GEOFENCE_ID, id)
        }

        private val EXTRA_GEOFENCE_ID = "EXTRA_GEOFENCE_ID"
    }

    override fun onHandleIntent(intent: Intent?) {

    }
}
