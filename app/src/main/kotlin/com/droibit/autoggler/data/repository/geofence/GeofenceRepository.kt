package com.droibit.autoggler.data.repository.geofence

import rx.Single

interface GeofenceRepository {

    fun loadGeofences(): Single<List<Geofence>>

    fun addGeofence(name: String, circle: Circle, trigger: Trigger): Single<Geofence>
}