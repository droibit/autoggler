package com.droibit.autoggler.data.repository.geofence

import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_NAME
import com.droibit.autoggler.data.repository.source.RealmProvider
import com.droibit.autoggler.data.repository.source.where
import rx.Observable

class GeofenceRepositoryImpl(private val realmProvider: RealmProvider) : GeofenceRepository {

    override fun loadGeofences(): Observable<Geofence> {
        return realmProvider.get().use { realm ->
            realm.where<Geofence>()
                .findAllSortedAsync(COLUMN_NAME)
                .asObservable()
                .flatMap { Observable.from(it.toList()) }
        }
    }
}