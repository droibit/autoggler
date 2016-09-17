package com.droibit.autoggler.data.repository.geofence

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.support.annotation.RequiresPermission
import android.support.annotation.WorkerThread


interface GeofencingRepository {

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION))
    @WorkerThread
    fun register(geofence: Geofence): Boolean

    @WorkerThread
    fun unregister(geofence: Geofence): Boolean

    @RequiresPermission(anyOf = arrayOf(ACCESS_FINE_LOCATION))
    @WorkerThread
    fun update(geofence: Geofence): Boolean
}