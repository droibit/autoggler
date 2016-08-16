package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.lang.kotlin.single
import rx.schedulers.Schedulers


class LoadTask(private val geofenceRepository: GeofenceRepository) :
        GeofencesContract.LoadTask {

    override fun loadGeofences(): Single<List<Geofence>> {
        return single<List<Geofence>> { subscriber ->
            val geofences = geofenceRepository.loadGeofences()
            if (!subscriber.isUnsubscribed) {
                subscriber.onSuccess(geofences)
            }
        }.subscribeOn(Schedulers.io())
    }
}