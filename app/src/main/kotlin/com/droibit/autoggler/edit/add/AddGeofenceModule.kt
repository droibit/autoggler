package com.droibit.autoggler.edit.add

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.BounceDropAnimator
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.GetCurrentLocationEvent
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.jakewharton.rxrelay.BehaviorRelay


fun addGeofenceModule(
        view: AddGeofenceContract.View,
        navigator: AddGeofenceContract.Navigator,
        permissions: AddGeofenceContract.RuntimePermissions,
        interactionCallback: GoogleMapView.Callback,
        initialGeofence: Geofence) = Kodein.Module {

    bind<AddGeofenceContract.View>() with instance(view)

    bind<AddGeofenceContract.Navigator>() with instance(navigator)

    bind<AddGeofenceContract.RuntimePermissions>() with instance(permissions)

    bind<Geofence>() with instance(initialGeofence)

    bind<BehaviorRelay<GetCurrentLocationEvent>>() with provider { BehaviorRelay.create<GetCurrentLocationEvent>() }

    bind<AddGeofenceContract.GetCurrentLocationTask>() with provider {
        GetCurrentLocationTask(
                relay = instance(),
                locationRepository = instance(),
                permissionChecker = instance(),
                config = instance()
        )
    }

    bind<AddGeofenceContract.RegisterGeofencingTask>() with provider {
        RegisterGeofencingTask(
                geofencingRepository = instance(),
                permissionChecker = instance()
        )
    }

    bind<AddGeofenceContract.Presenter>() with provider {
        AddGeofencePresenter(
                view = instance(),
                permissions = instance(),
                navigator = instance(),
                getCurrentLocationTask = instance(),
                registerGeofencingTask = instance(),
                subscriptions = instance(),
                geofence = instance()
        )
    }

    bind<GoogleMapView>() with provider {
        GoogleMapView(interactionCallback,
                bounceDropAnimator = instance(),
                restorer = instance(),
                permissionChecker = instance()
        )
    }

    bind<BounceDropAnimator>() with provider { BounceDropAnimator(config = instance(), timeProvider = instance()) }

    bind<GoogleMapView.Restorer>() with provider { GoogleMapView.Restorer() }
}