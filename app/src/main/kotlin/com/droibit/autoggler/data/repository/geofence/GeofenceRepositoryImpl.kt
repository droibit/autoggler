package com.droibit.autoggler.data.repository.geofence

import android.support.annotation.WorkerThread
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.droibit.autoggler.data.repository.source.db.*
import com.droibit.autoggler.data.repository.source.db.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.db.GeofencePersistenceContract.CREATED_AT
import io.realm.RealmObject

class GeofenceRepositoryImpl(
        private val realmProvider: RealmProvider,
        private val autoIncrementor: AutoIncrementor,
        private val timeProvider: TimeProvider) : GeofenceRepository {

    @WorkerThread
    override fun loadGeofences(): List<Geofence> {
        return realmProvider.use { realm ->
            val managedGeofences = realm.where<Geofence>().findAllSorted(CREATED_AT)
            realm.copyFromRealm(managedGeofences)
        }
    }

    @WorkerThread
    override fun loadGeofence(targetId: Long): Geofence? {
        return realmProvider.use { realm ->
            val managedGeofence = realm.where<Geofence>()
                    .equalTo(COLUMN_ID, targetId)
                    .findFirst() ?: return@use null

            realm.copyFromRealm(managedGeofence)
        }
    }

    @WorkerThread
    override fun addGeofence(name: String, circle: Circle, toggle: Toggle): Geofence {
        return realmProvider.use { realm ->
            val managedGeofence = realm.runTransaction {
                realm.createObject<Geofence>().apply {
                    this.id = autoIncrementor.newId<Geofence>(realm)
                    this.name = name
                    this.circle = realm.copyToRealm(circle)
                    this.toggle = realm.copyToRealm(toggle)
                    this.enabled = true
                    this.createdAt = timeProvider.currentTimeMillis
                }
            }
            realm.copyFromRealm(managedGeofence)
        }
    }

    @WorkerThread
    override fun updateGeofence(srcGeofence: Geofence): Geofence {
        return realmProvider.use { realm ->
            val managedGeofence = realm.runTransaction {
                realm.copyToRealmOrUpdate(srcGeofence)
            }
            realm.copyFromRealm(managedGeofence)
        }
    }

    @WorkerThread
    override fun deleteGeofence(targetId: Long): Geofence {
        return realmProvider.use { realm ->
            val managedGeofence = realm.where<Geofence>()
                    .equalTo(COLUMN_ID, targetId)
                    .findFirst() ?: throw IllegalArgumentException("Geofence of the specified id($targetId) does not exist.")
            val deletedGeofence = realm.copyFromRealm(managedGeofence)

            realm.executeTransaction {
                RealmObject.deleteFromRealm(managedGeofence)
            }
            deletedGeofence
        }
    }

    internal fun _deleteGeofences() {
        realmProvider.use { realm ->
            realm.executeTransaction {
                realm.delete<Geofence>()
                realm.delete<Circle>()
                realm.delete<Toggle>()
            }
        }
    }
}
