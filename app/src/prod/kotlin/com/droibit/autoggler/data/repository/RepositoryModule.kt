package com.droibit.autoggler.data.repository

import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.data.repository.geofence.GeofenceRepositoryImpl
import com.droibit.autoggler.data.repository.geofence.GeofencingRepository
import com.droibit.autoggler.data.repository.geofence.GeofencingRepositoryImpl
import com.droibit.autoggler.data.repository.location.LocationRepository
import com.droibit.autoggler.data.repository.location.LocationRepositoryImpl
import com.droibit.autoggler.data.repository.source.api.GoogleApiProvider
import com.droibit.autoggler.data.repository.source.api.GoogleApiProviderImpl
import com.droibit.autoggler.data.repository.source.db.AutoIncrementor
import com.droibit.autoggler.data.repository.source.db.GeofencePersistenceContract.COLUMN_ID
import com.droibit.autoggler.data.repository.source.db.RealmProvider
import com.droibit.autoggler.data.repository.source.db.RealmProvider.Companion.FILE_NAME
import com.droibit.autoggler.data.repository.source.db.RealmProviderImpl
import com.droibit.autoggler.geofencing.GeofencingIntentService
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import io.realm.RealmConfiguration
import rx.subscriptions.CompositeSubscription

fun repositoryModule() = Kodein.Module {

    bind<RealmConfiguration>() with singleton {
        RealmConfiguration.Builder()
                .name(FILE_NAME)
                .build()
    }

    bind<RealmProvider>() with singleton { RealmProviderImpl(instance()) }

    bind<AutoIncrementor>("geofence") with singleton { AutoIncrementor(COLUMN_ID) }

    bind<GeofenceRepository>() with singleton {
        GeofenceRepositoryImpl(
                realmProvider = instance(),
                autoIncrementor = instance("geofence"),
                timeProvider = instance()).apply {
            // FIXME:
            _deleteGeofences()
        }
    }

    bind<GeofencingRepository>() with singleton {
        GeofencingRepositoryImpl(
                config = instance(),
                googleApiProvider = instance(),
                intentCreator = { GeofencingIntentService.createIntent(context = instance(), id = it) }
        )
    }

    bind<GoogleApiProvider>() with singleton { GoogleApiProviderImpl(instance()) }

    bind<LocationRepository>() with singleton {
        LocationRepositoryImpl(
                looper = instance("main"),
                config = instance(),
                timeProvider = instance(),
                googleApiProvider = instance()
        )
    }

    bind<CompositeSubscription>() with provider { CompositeSubscription() }
}