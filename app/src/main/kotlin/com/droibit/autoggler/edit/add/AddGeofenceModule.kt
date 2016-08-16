package com.droibit.autoggler.edit.add

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider


fun addGeofenceModule(view: AddGeofenceContract.View, navigator: AddGeofenceContract.Navigator) = Kodein.Module {

    bind<AddGeofenceContract.View>() with instance(view)

    bind<AddGeofenceContract.Navigator>() with instance(navigator)

    bind<AddGeofenceContract.Presenter>() with provider {
        AddGeofencePresenter(
                view = instance(),
                navigator = instance())
    }
}