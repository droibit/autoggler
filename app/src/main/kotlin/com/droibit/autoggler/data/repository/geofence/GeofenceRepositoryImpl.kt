package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.WorkerThread
import com.droibit.autoggler.data.repository.source.*
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_NAME
import io.realm.RealmObject
import rx.Single

class GeofenceRepositoryImpl(
        private val realmProvider: RealmProvider,
        private val autoIncrementor: AutoIncrementor) : GeofenceRepository {

    @WorkerThread
    override fun loadGeofences(): Single<List<Geofence>> {
        return Single.defer {
            realmProvider.use { realm ->
                val managedGeofences = realm.where<Geofence>().findAllSorted(COLUMN_NAME)
                Single.just(realm.copyFromRealm(managedGeofences))
            }
        }
    }

    @WorkerThread
    override fun loadGeofence(targetId: Long): Single<Geofence?> {
        return Single.defer {
            realmProvider.use { realm ->
                val managedGeofence = realm.where<Geofence>()
                        .equalTo(COLUMN_ID, targetId)
                        .findFirst()

                Single.just(if (managedGeofence != null) realm.copyFromRealm(managedGeofence) else null)
            }
        }
    }

    @WorkerThread
    override fun addGeofence(name: String, circle: Circle, trigger: Trigger): Single<Geofence> {
        return Single.defer {
            realmProvider.use { realm ->
                val managedGeofence = realm.runTransaction {
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

    @WorkerThread
    override fun updateGeofence(srcGeofence: Geofence): Single<Geofence> {
        return Single.defer {
            realmProvider.use { realm ->
                val managedGeofence = realm.runTransaction {
                    realm.copyToRealmOrUpdate(srcGeofence)
                }
                Single.just(realm.copyFromRealm(managedGeofence))
            }
        }
    }

    @WorkerThread
    override fun deleteGeofence(targetId: Long): Single<Geofence> {
        return Single.defer {
            realmProvider.use { realm ->
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
        realmProvider.use { realm ->
            realm.executeTransaction {
                realm.delete<Geofence>()
            }
        }
    }
}
