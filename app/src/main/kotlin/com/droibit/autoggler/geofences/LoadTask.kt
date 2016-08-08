package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.schedulers.Schedulers


class LoadTask(private val geofenceRepository: GeofenceRepository) : GeofencesContract.LoadTask {

    override fun loadGeofences(): Single<List<Geofence>> {
        return geofenceRepository.loadGeofences()
                .subscribeOn(Schedulers.io())
    }
}