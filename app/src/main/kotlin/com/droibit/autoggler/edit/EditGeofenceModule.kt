package com.droibit.autoggler.edit

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.provider

fun editGeofenceModule() = Kodein.Module {

    bind<GoogleMapView>() with provider { GoogleMapView() }
}
