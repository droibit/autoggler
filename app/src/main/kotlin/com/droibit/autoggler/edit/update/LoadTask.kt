package com.droibit.autoggler.edit.update

import com.droibit.autoggler.edit.update.UpdateGeofenceContract.LoadTask.Event as LoadTaskEvent

import rx.Single

class LoadTask : UpdateGeofenceContract.LoadTask {

    override fun loadGeofence(id: Long): Single<LoadTaskEvent> {
        TODO()
    }
}