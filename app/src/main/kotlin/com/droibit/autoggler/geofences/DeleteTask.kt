package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.schedulers.Schedulers

class DeleteTask(private val geofenceRepository: GeofenceRepository) :
    GeofencesContract.DeleteTask {

    override fun deleteGeofence(targetId: Long): Single<Geofence> {
        // TODO unregisterGeofencing
        return geofenceRepository.deleteGeofence(targetId)
                .subscribeOn(Schedulers.io())
    }
}