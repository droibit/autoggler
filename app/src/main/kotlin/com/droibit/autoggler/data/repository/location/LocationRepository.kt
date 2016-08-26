package com.droibit.autoggler.data.repository.location

import android.location.Location
import android.support.annotation.WorkerThread


interface LocationRepository {

    @WorkerThread
    fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location?

    @WorkerThread
    fun getLocationAvailableStatus(): AvailableStatus
}