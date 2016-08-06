package com.droibit.autoggler.data.repository

import android.content.Context
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.GeofenceRepositoryImpl
import com.droibit.autoggler.data.repository.source.AutoIncrementor
import com.droibit.autoggler.data.repository.source.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.RealmProvider
import com.droibit.autoggler.data.repository.source.RealmProvider.Companion.FILE_NAME
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import io.realm.RealmConfiguration

fun repositoryModule() = Kodein.Module {

    bind<RealmConfiguration>() with singleton {
        val context: Context = instance()
        RealmConfiguration.Builder(context)
                .name(FILE_NAME)
                //.inMemory()
                .build()
    }

    bind<RealmProvider>() with singleton { RealmProvider(instance()) }
    bind<AutoIncrementor>("geofence") with singleton { AutoIncrementor(COLUMN_ID) }
    bind<GeofenceRepository>() with singleton {
        GeofenceRepositoryImpl(instance(), instance("geofence")).apply {
            _deleteGeofences()
        }
    }
}



