package com.droibit.autoggler.data.repository

import android.content.Context
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.GeofenceRepositoryImpl
import com.droibit.autoggler.data.repository.source.RealmProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import io.realm.RealmConfiguration

fun repositoryModule() = Kodein.Module {

    bind<RealmProvider>() with singleton {
        val context: Context = instance()
        val config = RealmConfiguration.Builder(context)
                .name(RealmProvider.FILE_NAME)
                .inMemory()
                .build()
        RealmProvider(config)
    }

    bind<GeofenceRepository>() with singleton { GeofenceRepositoryImpl(instance()) }
}



