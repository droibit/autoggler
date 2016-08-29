package com.droibit.autoggler.edit

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun editGeofenceModule(interactionListener: GoogleMapView.Listener, dragCallback: DragActionMode.Callback) = Kodein.Module {

    bind<GoogleMapView.Listener>() with instance(interactionListener)

    bind<GoogleMapView>() with provider {
        GoogleMapView(interactionListener = instance(), bounceDropAnimator = instance(), permissionChecker = instance())
    }

    bind<LocationResolutionSource>() with provider { LocationResolutionSource() }

    bind<BounceDropAnimator>() with provider { BounceDropAnimator(config = instance(), timeProvider = instance()) }

    bind<DragActionMode>() with provider { DragActionMode(context = instance(), callback = dragCallback) }
}
