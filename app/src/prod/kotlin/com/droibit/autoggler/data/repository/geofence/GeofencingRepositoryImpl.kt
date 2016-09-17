package com.droibit.autoggler.data.repository.geofence

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.support.annotation.RequiresPermission
import android.support.annotation.VisibleForTesting
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.repository.source.api.GoogleApiProvider
import com.droibit.autoggler.data.repository.source.api.await
import com.droibit.autoggler.data.repository.source.api.blockingConnect
import com.droibit.autoggler.data.repository.source.api.use
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence.*
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER
import timber.log.Timber
import java.util.Collections.singletonList
import com.google.android.gms.location.Geofence as GmsGeofence

class GeofencingRepositoryImpl(
        private val config: ApplicationConfig,
        private val googleApiProvider: GoogleApiProvider,
        private val intentCreator: (Long) -> Intent) : GeofencingRepository {

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION))
    override fun register(geofence: Geofence): Boolean {
        googleApiProvider.newClient().use {
            val connectionResult = blockingConnect(config.googleApiTimeoutMillis)
            if (!connectionResult.isSuccess) {
                Timber.d("Google api connect failed: ${connectionResult.status}")
                return false
            }


            val statusResult = addGeofence(client = this, geofence = geofence)
            Timber.d("addGeofences($geofence): ${statusResult.status}")

            return statusResult.isSuccess
        }
    }

    override fun unregister(geofence: Geofence): Boolean {
        googleApiProvider.newClient().use {
            val connectionResult = blockingConnect(config.googleApiTimeoutMillis)
            if (!connectionResult.isSuccess) {
                Timber.d("Google api connect failed: ${connectionResult.status}")
                return false
            }

            val statusResult = removeGeofence(client = this, geofence = geofence)
            Timber.d("removeGeofences($geofence): ${statusResult.status}")

            return statusResult.isSuccess
        }
    }

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION))
    override fun update(geofence: Geofence): Boolean {
        googleApiProvider.newClient().use {
            val connectionResult = blockingConnect(config.googleApiTimeoutMillis)
            if (!connectionResult.isSuccess) {
                Timber.d("Google api connect failed: ${connectionResult.status}")
                return false
            }

            val removeResult = removeGeofence(client = this, geofence = geofence)
            Timber.d("removeGeofences($geofence): ${removeResult.status}")

            if (!removeResult.isSuccess) {
                return false
            }

            val addResult = addGeofence(client = this, geofence = geofence)
            Timber.d("addGeofences($geofence): ${addResult.status}")

            return addResult.isSuccess
        }
    }

    @VisibleForTesting
    internal fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        val builder = GeofencingRequest.Builder().apply {
            val gmsGeofence = GmsGeofence.Builder()
                    .setRequestId("${geofence.id}")
                    .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
                    .setCircularRegion(geofence.circle)
                    .setExpirationDuration(NEVER_EXPIRE)
                    .build()
            addGeofence(gmsGeofence)
            setInitialTrigger(INITIAL_TRIGGER_ENTER)
        }
        return builder.build()
    }

    private fun addGeofence(client: GoogleApiClient, geofence: Geofence): Status {
        val request = createGeofencingRequest(geofence)
        val pendingIntent = PendingIntent.getService(client.context, 0, intentCreator(geofence.id), FLAG_UPDATE_CURRENT)
        return googleApiProvider.geofencingApi.addGeofences(client, request, pendingIntent)
                .await(config.googleApiTimeoutMillis)
    }

    private fun removeGeofence(client: GoogleApiClient, geofence: Geofence): Status {
        return googleApiProvider.geofencingApi.removeGeofences(client, singletonList("${geofence.id}"))
                .await(config.googleApiTimeoutMillis)
    }
}

private fun GmsGeofence.Builder.setCircularRegion(circle: Circle)
        = setCircularRegion(circle.lat, circle.lng, circle.radius.toFloat())