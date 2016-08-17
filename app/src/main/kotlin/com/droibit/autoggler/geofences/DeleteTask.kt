package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.lang.kotlin.single
import rx.schedulers.Schedulers

class DeleteTask(private val geofenceRepository: GeofenceRepository) :
    GeofencesContract.DeleteTask {

    override fun deleteGeofence(targetId: Long): Single<Geofence> {
        // TODO unregisterGeofencing
        return single<Geofence> { subscriber ->
            try {
                geofenceRepository.deleteGeofence(targetId).apply {
                    if (!subscriber.isUnsubscribed) {
                        subscriber.onSuccess(this)
                    }
                }
            } catch (e: RuntimeException) {
                subscriber.onError(e)
            }
        }.subscribeOn(Schedulers.io())
    }
}