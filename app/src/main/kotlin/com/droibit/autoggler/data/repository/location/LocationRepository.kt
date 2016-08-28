package com.droibit.autoggler.data.repository.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.support.annotation.RequiresPermission
import android.support.annotation.WorkerThread


interface LocationRepository {

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    @WorkerThread
    fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location?

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    @WorkerThread
    fun getLocationAvailableStatus(): AvailableStatus
}