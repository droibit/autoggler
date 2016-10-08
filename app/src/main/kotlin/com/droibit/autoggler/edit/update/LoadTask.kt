package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.lang.kotlin.single
import rx.schedulers.Schedulers

class LoadTask(private val geofenceRepository: GeofenceRepository) :
        UpdateGeofenceContract.LoadTask {

    override fun loadGeofences(ignoreId: Long): Single<List<Geofence>> {
        return single<List<Geofence>> { subscriber ->
            val geofences = geofenceRepository.loadGeofences()

            if (!subscriber.isUnsubscribed) {
                val needGeofences = geofences.filter { it.id != ignoreId }
                subscriber.onSuccess(needGeofences)
            }
        }.subscribeOn(Schedulers.io())
    }
}