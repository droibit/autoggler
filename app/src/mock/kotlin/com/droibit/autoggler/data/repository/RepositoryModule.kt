package com.droibit.autoggler.data.repository

import android.content.Context
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.GeofenceRepositoryImpl
import com.droibit.autoggler.data.repository.source.RealmProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import io.realm.RealmConfiguration

fun repositoryModule() = Kodein.Module {

    bind<RealmConfiguration>() with singleton {
        val context: Context = instance()
        RealmConfiguration.Builder(context)
                .name(RealmProvider.FILE_NAME)
                .inMemory()
                .build()
    }

    bind<RealmProvider>() with provider { RealmProvider(instance()) }
    bind<GeofenceRepository>() with provider { GeofenceRepositoryImpl(instance()) }
}



