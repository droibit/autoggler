package com.droibit.autoggler.data.repository.geofence

import com.droibit.autoggler.data.repository.source.*
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_NAME
import io.realm.RealmObject
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

    override fun deleteGeofence(targetId: Long): Single<Geofence> {
        return Single.defer {
            realmProvider.get().use { realm ->
                val managedGeofence = realm.where<Geofence>()
                        .equalTo(COLUMN_ID, targetId)
                        .findFirst() ?: throw IllegalArgumentException("Geofence of the specified id($targetId) does not exist.")
                val deletedGeofence = realm.copyFromRealm(managedGeofence)

                realm.executeTransaction {
                    RealmObject.deleteFromRealm(managedGeofence)
                }
                Single.just(deletedGeofence)
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
