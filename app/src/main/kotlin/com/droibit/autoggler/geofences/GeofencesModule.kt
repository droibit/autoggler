package com.droibit.autoggler.geofences

import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider


fun geofencesModule(view: GeofencesContract.View, navigator: GeofencesContract.Navigator) = Kodein.Module {

    bind<GeofencesContract.View>() with instance(view)

    bind<GeofencesContract.Navigator>() with instance(navigator)

    bind<RxActivityLauncher>() with provider { RxActivityLauncher() }

    bind<GeofencesContract.LoadTask>() with provider { LoadTask(geofenceRepository = instance()) }

    bind<GeofencesContract.DeleteTask>() with provider { DeleteTask(geofenceRepository = instance()) }

    bind<GeofencesContract.Presenter>() with provider {
        GeofencesPresenter(
                view = instance(),
                navigator = instance(),
                loadTask = instance(),
                deleteTask = instance(),
                subscriptions = instance()
        )
    }
}