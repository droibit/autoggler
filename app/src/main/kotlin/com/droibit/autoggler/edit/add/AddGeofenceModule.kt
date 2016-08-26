package com.droibit.autoggler.edit.add

import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.Event
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.jakewharton.rxrelay.BehaviorRelay


fun addGeofenceModule(
        view: AddGeofenceContract.View,
        navigator: AddGeofenceContract.Navigator,
        permissions: AddGeofenceContract.RuntimePermissions) = Kodein.Module {

    bind<AddGeofenceContract.View>() with instance(view)

    bind<AddGeofenceContract.Navigator>() with instance(navigator)

    bind<AddGeofenceContract.RuntimePermissions>() with instance(permissions)

    bind<RxActivityLauncher>() with provider { RxActivityLauncher() }

    bind<BehaviorRelay<Event>>() with provider { BehaviorRelay.create<Event>() }

    bind<AddGeofenceContract.GetCurrentLocationTask>() with provider {
        GetCurrentLocationTask(
                relay = instance(),
                locationRepository = instance(),
                permissionChecker = instance(),
                config = instance()
        )
    }

    bind<AddGeofenceContract.Presenter>() with provider {
        AddGeofencePresenter(
                view = instance(),
                permissions = instance(),
                navigator = instance(),
                getCurrentLocationTask = instance(),
                permissionChecker = instance(),
                subscriptions = instance()
        )
    }
}