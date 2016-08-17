package com.droibit.autoggler.data.repository.location

import android.location.Location
import android.support.annotation.WorkerThread


interface LocationRepository {

    @WorkerThread
    fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, locationTimeoutMillis: Long): Location?

    @WorkerThread
    fun getLocationAvailableStatus(): LocationAvailableStatus
}