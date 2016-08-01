package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.UiThread
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_NAME
import com.droibit.autoggler.data.repository.source.RealmProvider
import com.droibit.autoggler.data.repository.source.where
import io.realm.RealmResults
import rx.Observable

class GeofenceRepositoryImpl(val realmProvider: RealmProvider) : GeofenceRepository {

    @UiThread
    override fun loadGeofences(): Observable<RealmResults<Geofence>> {
        val realm = realmProvider.getRealm()
        return realm.where<Geofence>()
                .findAllSortedAsync(COLUMN_NAME)
                .asObservable()
    }
}