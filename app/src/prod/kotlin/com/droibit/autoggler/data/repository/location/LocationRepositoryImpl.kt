package com.droibit.autoggler.data.repository.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Looper
import android.support.annotation.RequiresPermission
import android.support.annotation.WorkerThread
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.droibit.autoggler.data.repository.source.api.GoogleApiProvider
import com.droibit.autoggler.data.repository.source.api.await
import com.droibit.autoggler.data.repository.source.api.blockingConnect
import com.droibit.autoggler.data.repository.source.api.use
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationSettingsRequest
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationRepositoryImpl(
        private val looper: Looper,
        private val config: ApplicationConfig,
        private val timeProvider: TimeProvider,
        private val googleApiProvider: GoogleApiProvider) : LocationRepository {


    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    @WorkerThread
    override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location? {
        googleApiProvider.newLocationClient().use {
            val connectionResult = blockingConnect(config.googleApiTimeoutMillis)
            if (!connectionResult.isSuccess) {
                return null
            }

//            if (!isAvailableLocation(client = this)) {
//                return null
//            }

            val lastLocation = googleApiProvider.fusedLocationProviderApi.getLastLocation(this@use)
            if (shouldUseLastLocation(lastLocation, maxLastLocationElapsedTimeMillis)) {
                Timber.d("FusedLocationProviderApi.getLastLocation: $lastLocation")
                return lastLocation
            }
            return requestCurrentLocation(client = this, locationTimeoutMillis = timeoutMillis)
        }
    }

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    @WorkerThread
    override fun getLocationAvailableStatus(): AvailableStatus {
        googleApiProvider.newLocationClient().use {
            val connectionResult = blockingConnect(config.googleApiTimeoutMillis)
            if (!connectionResult.isSuccess) {
                return LocationAvailableStatus(connectionResult)
            }
            val status = checkLocationSettings(client = this)
            return LocationAvailableStatus(status)
        }
    }

    private fun isAvailableLocation(client: GoogleApiClient): Boolean {
        return googleApiProvider.run {
            val locationAvailability = fusedLocationProviderApi.getLocationAvailability(client)
            locationAvailability.isLocationAvailable.apply {
                Timber.d("LocationAvailability#isLocationAvailable($this)")
            }
        }
    }

    private fun shouldUseLastLocation(lastLocation: Location?, maxLastLocationElapsedTimeMillis: Long): Boolean {
        if (lastLocation == null) {
            return false
        }
        val maxLastLocationElapsedTimeNanos = TimeUnit.MILLISECONDS.toNanos(maxLastLocationElapsedTimeMillis)
        val elapsedTimeNanos = timeProvider.elapsedRealTimeNanos - lastLocation.elapsedRealtimeNanos
        return elapsedTimeNanos <= maxLastLocationElapsedTimeNanos
    }

    private fun requestCurrentLocation(client: GoogleApiClient, locationTimeoutMillis: Long): Location? {
        val fusedLocationProviderApi = googleApiProvider.fusedLocationProviderApi
        val locationHolder = googleApiProvider.newSyncLocationHolder()

        try {
            val locationRequest = createLocationRequest(PRIORITY_HIGH_ACCURACY)
            fusedLocationProviderApi.requestLocationUpdates(client, locationRequest, locationHolder, looper)
            Timber.d("Start: requestLocationUpdates(PRIORITY_HIGH_ACCURACY)")

            locationHolder.await(locationTimeoutMillis)
        } finally {
            fusedLocationProviderApi.removeLocationUpdates(client, locationHolder)
        }
        Timber.d("End: requestLocationUpdates(PRIORITY_HIGH_ACCURACY): ${locationHolder.value}")

        return locationHolder.value
    }

    private fun checkLocationSettings(client: GoogleApiClient): Status {
        val locationSettingsRequest = createLocationSettingsRequest(PRIORITY_HIGH_ACCURACY)
        val locationSettingsResult = googleApiProvider.run {
            locationSettingsApi.checkLocationSettings(client, locationSettingsRequest)
                    .await(config.googleApiTimeoutMillis)
        }
        Timber.d("checkLocationSettings(PRIORITY_HIGH_ACCURACY): ${locationSettingsResult.status}")

        return locationSettingsResult.status
    }

    private fun createLocationSettingsRequest(priority: Int): LocationSettingsRequest {
        return LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .setNeedBle(false)
                .addLocationRequest(LocationRequest().apply { this.priority = priority })
                .build()
    }

    private fun createLocationRequest(priority: Int): LocationRequest {
        return LocationRequest.create().apply {
            this.priority = priority
            this.numUpdates = 1
        }
    }
}