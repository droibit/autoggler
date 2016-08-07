package com.droibit.autoggler.data.repository.geofence

import com.droibit.autoggler.data.repository.source.*
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_NAME
import rx.Observable
import rx.Single

class GeofenceRepositoryImpl(
        private val realmProvider: RealmProvider,
        private val autoIncrementor: AutoIncrementor) : GeofenceRepository {

    override fun loadGeofences(): Single<List<Geofence>> {
        return Single.defer {
            realmProvider.get().use { realm ->
                val managedGeofences = realm.where<Geofence>().findAllSorted(COLUMN_NAME)
                Single.just(realm.copyFromRealm(managedGeofences))
            }
        }
    }

    override fun addGeofence(name: String, circle: Circle, trigger: Trigger): Single<Geofence> {
        return Single.defer {
            realmProvider.get().use { realm ->
                val managedGeofence = realm.useTransaction {
                    realm.createObject<Geofence>().apply {
                        this.id = autoIncrementor.newId<Geofence>(realm)
                        this.name = name
                        this.circle = realm.copyToRealm(circle)
                        this.trigger = realm.copyToRealm(trigger)
                        this.enabled = true
                    }
                }
                Single.just(realm.copyFromRealm(managedGeofence))
            }
        }
    }

    internal fun _deleteGeofences() {
        realmProvider.get().use { realm ->
            realm.executeTransaction {
                realm.delete<Geofence>()
            }
        }
    }
}
