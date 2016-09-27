package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider


fun updateGeofenceModule(
        view: UpdateGeofenceContract.View,
        navigator: UpdateGeofenceContract.Navigator,
        permissions: UpdateGeofenceContract.RuntimePermissions,
        interactionCallback: GoogleMapView.Callback,
        initialGeofence: Geofence) = Kodein.Module {

    bind<UpdateGeofenceContract.View>() with instance(view)

    bind<UpdateGeofenceContract.Navigator>() with instance(navigator)

    bind<UpdateGeofenceContract.RuntimePermissions>() with instance(permissions)

    bind<Geofence>("initialGeofence") with instance(initialGeofence)

    bind<Geofence>("editableGeofence") with provider { initialGeofence.clone() }

    bind<GoogleMapView>() with provider { GoogleMapView(callback = interactionCallback) }

    bind<UpdateGeofenceContract.Presenter>() with provider {
        UpdateGeofencePresenter(
                view = instance(),
                navigator = instance(),
                permissions = instance(),
                loadTask = instance(),
                subscriptions = instance(),
                editableGeofence = instance("editableGeofence")
        )
    }

    bind<UpdateGeofenceContract.LoadTask>() with provider { LoadTask(geofenceRepository = instance()) }
}