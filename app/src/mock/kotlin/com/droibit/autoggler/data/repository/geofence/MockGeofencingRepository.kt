package com.droibit.autoggler.data.repository.geofence

import com.droibit.autoggler.data.repository.geofence.GeofencingException.ErrorStatus

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

    class RemoveFailed : GeofencingRepository {

        override fun register(geofence: Geofence) = true

        override fun unregister(geofence: Geofence) = true

        override fun update(geofence: Geofence) = throw GeofencingException(status = ErrorStatus.FAILED_REMOVE)
    }

    class AddFailed : GeofencingRepository {

        override fun register(geofence: Geofence) = true

        override fun unregister(geofence: Geofence) = true

        override fun update(geofence: Geofence) = throw GeofencingException(status = ErrorStatus.FAILED_ADD)

    }
}