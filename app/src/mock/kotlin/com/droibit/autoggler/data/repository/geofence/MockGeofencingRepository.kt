package com.droibit.autoggler.data.repository.geofence

object MockGeofencingRepository {

    class Success : GeofencingRepository {

        override fun register(geofence: Geofence) = true

        override fun unregister(geofence: Geofence) = true

        override fun update(geofence: Geofence) = true
    }

    class Failed : GeofencingRepository {

        override fun register(geofence: Geofence) = false

        override fun unregister(geofence: Geofence) = false

        override fun update(geofence: Geofence) = false
    }
}