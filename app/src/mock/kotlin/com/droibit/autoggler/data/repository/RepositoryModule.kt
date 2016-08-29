package com.droibit.autoggler.data.repository

import android.content.Context
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.GeofenceRepositoryImpl
import com.droibit.autoggler.data.repository.location.LocationRepository
import com.droibit.autoggler.data.repository.location.Mock
import com.droibit.autoggler.data.repository.source.db.AutoIncrementor
import com.droibit.autoggler.data.repository.source.db.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.db.RealmProvider
import com.droibit.autoggler.data.repository.source.db.RealmProvider.Companion.FILE_NAME
import com.droibit.autoggler.data.repository.source.db.RealmProviderImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import io.realm.RealmConfiguration
import rx.subscriptions.CompositeSubscription

fun repositoryModule() = Kodein.Module {

    bind<RealmConfiguration>() with singleton {
        val context: Context = instance()
        RealmConfiguration.Builder(context)
                .name(FILE_NAME)
                .build()
    }

    bind<RealmProvider>() with singleton { RealmProviderImpl(instance()) }

    bind<AutoIncrementor>("geofence") with singleton { AutoIncrementor(COLUMN_ID) }

    bind<GeofenceRepository>() with singleton {
        GeofenceRepositoryImpl(instance(), instance("geofence")).apply {
            _deleteGeofences()
        }
    }

    bind<LocationRepository>() with singleton { Mock.LocationRepositoryImpl() }

    bind<CompositeSubscription>() with provider { CompositeSubscription() }
}