package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single


class LoadTask(private val geofenceRepository: GeofenceRepository) : GeofencesContract.LoadTask {

    override fun loadGeofences(): Single<List<Geofence>> {
        TODO()
    }
}