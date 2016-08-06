package com.droibit.autoggler.data.repository.geofence

import rx.Observable

interface GeofenceRepository {

    fun loadGeofences(): Observable<Geofence>
}