package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.WorkerThread
import rx.Single

interface GeofenceRepository {

    @WorkerThread
    fun loadGeofences(): Single<List<Geofence>>

    @WorkerThread
    fun loadGeofence(targetId: Long): Single<Geofence?>

    @WorkerThread
    fun addGeofence(name: String, circle: Circle, trigger: Trigger): Single<Geofence>

    @WorkerThread
    fun updateGeofence(srcGeofence: Geofence): Single<Geofence>

    @WorkerThread
    fun deleteGeofence(targetId: Long): Single<Geofence>

    // TODO: registerGeofenceing

    // TODO: unregisterGeofencing
}