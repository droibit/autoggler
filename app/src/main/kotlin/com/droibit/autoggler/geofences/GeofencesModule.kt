package com.droibit.autoggler.geofences

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider


fun geofencesModule(view: GeofencesContract.View) = Kodein.Module {

    bind<GeofencesContract.View>() with instance(view)

    bind<GeofencesContract.Presenter>() with provider { GeofencesPresenter(instance()) }
}