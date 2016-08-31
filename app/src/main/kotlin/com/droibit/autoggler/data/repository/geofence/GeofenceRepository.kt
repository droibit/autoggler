package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.WorkerThread
import rx.Single

interface GeofenceRepository {

    @WorkerThread
    fun loadGeofences(): List<Geofence>

    @WorkerThread
    fun loadGeofence(targetId: Long): Geofence?

    @WorkerThread
    fun addGeofence(name: String, circle: Circle, toggle: Toggle): Geofence

    @WorkerThread
    fun updateGeofence(srcGeofence: Geofence): Geofence

    @WorkerThread
    fun deleteGeofence(targetId: Long): Geofence

    // TODO: registerGeofenceing

    // TODO: unregisterGeofencing
}