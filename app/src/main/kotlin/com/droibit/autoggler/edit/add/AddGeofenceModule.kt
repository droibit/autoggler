package com.droibit.autoggler.edit.add

import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.Event
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.jakewharton.rxrelay.BehaviorRelay


fun addGeofenceModule(view: AddGeofenceContract.View, navigator: AddGeofenceContract.Navigator) = Kodein.Module {

    bind<AddGeofenceContract.View>() with instance(view)

    bind<AddGeofenceContract.Navigator>() with instance(navigator)

    bind<BehaviorRelay<Event>>() with provider { BehaviorRelay.create<Event>() }

    bind<AddGeofenceContract.GetCurrentLocationTask>() with provider {
        GetCurrentLocationTask(
                relay = instance(),
                locationRepository = instance(),
                permissionChecker = instance()
        )
    }

    bind<AddGeofenceContract.Presenter>() with provider {
        AddGeofencePresenter(
                view = instance(),
                navigator = instance(),
                getCurrentLocationTask = instance(),
                permissionChecker = instance(),
                subscriptions = instance()
        )
    }
}