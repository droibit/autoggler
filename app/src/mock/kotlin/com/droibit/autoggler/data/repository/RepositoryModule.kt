package com.droibit.autoggler.data.repository

import com.droibit.autoggler.data.repository.geofence.*
import com.droibit.autoggler.data.repository.location.LocationRepository
import com.droibit.autoggler.data.repository.location.MockLocationRepository
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
        RealmConfiguration.Builder()
                .name(FILE_NAME)
                .build()
    }

    bind<RealmProvider>() with singleton { RealmProviderImpl(config = instance()) }

    bind<AutoIncrementor>("geofence") with singleton { AutoIncrementor(COLUMN_ID) }

    bind<GeofenceRepository>() with singleton {
        GeofenceRepositoryImpl(
                realmProvider = instance(),
                autoIncrementor = instance("geofence"),
                timeProvider = instance()).apply {
            _deleteGeofences()
            listOf(
                    Geofence(name = "テスト1",
                            enabled = true,
                            circle = Circle(35.7121228, 139.7740507, 150.0),
                            toggle = Toggle()
                    ),
                    Geofence(name = "テスト2",
                            enabled = false,
                            circle = Circle(35.3121228, 139.7740507, 150.0),
                            toggle = Toggle()
                    ),
                    Geofence(name = "テスト3",
                            enabled = true,
                            circle = Circle(35.4121228, 139.7740507, 150.0),
                            toggle = Toggle()
                    ),
                    Geofence(name = "テスト4",
                            enabled = true,
                            circle = Circle(35.5121228, 139.7740507, 150.0),
                            toggle = Toggle()
                    ),
                    Geofence(name = "テスト5",
                            enabled = false,
                            circle = Circle(35.6121228, 139.7740507, 150.0),
                            toggle = Toggle()
                    )
            ).forEach { addGeofence(name = it.name, circle = it.circle, toggle = it.toggle) }
        }
    }

    bind<GeofencingRepository>() with singleton { MockGeofencingRepository.Success() }

    bind<LocationRepository>() with singleton { MockLocationRepository.Success() }

    bind<CompositeSubscription>() with provider { CompositeSubscription() }
}