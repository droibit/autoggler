package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import rx.Single
import rx.lang.kotlin.single
import rx.schedulers.Schedulers
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.LoadTask.Event as LoadTaskEvent

class LoadTask(private val geofenceRepository: GeofenceRepository) :
        UpdateGeofenceContract.LoadTask {

    override fun loadGeofences(editableId: Long): Single<LoadTaskEvent> {
        return single<LoadTaskEvent> { subscriber ->
            val rawGeofences = geofenceRepository.loadGeofences()

            if (!subscriber.isUnsubscribed) {
                val editableGeofence = rawGeofences.first { it.id == editableId }
                val uneditableGeofences = rawGeofences.filter { it.id != editableId }
                subscriber.onSuccess(LoadTaskEvent(editableGeofence, uneditableGeofences))
            }
        }.subscribeOn(Schedulers.io())
    }
}