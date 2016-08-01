package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.UiThread
import io.realm.RealmResults
import rx.Observable

interface GeofenceRepository {

    @UiThread
    fun loadGeofences(): Observable<RealmResults<Geofence>>
}