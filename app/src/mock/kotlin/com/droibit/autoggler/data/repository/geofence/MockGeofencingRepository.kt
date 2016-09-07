package com.droibit.autoggler.data.repository.geofence

object MockGeofencingRepository {

    class Success : GeofencingRepository {

        override fun register(geofence: Geofence) = true

        override fun unregister(geofence: Geofence) = true
    }
}