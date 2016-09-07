package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.WorkerThread


interface GeofencingRepository {

    @WorkerThread
    fun register(geofence: Geofence): Boolean

    @WorkerThread
    fun unregister(geofence: Geofence): Boolean
}