package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance


fun updateGeofenceModule(
        view: UpdateGeofenceContract.View,
        navigator: UpdateGeofenceContract.Navigator,
        permissions: UpdateGeofenceContract.RuntimePermissions,
        initialGeofence: Geofence) = Kodein.Module {

    bind<UpdateGeofenceContract.View>() with instance(view)

    bind<UpdateGeofenceContract.Navigator>() with instance(navigator)

    bind<UpdateGeofenceContract.RuntimePermissions>() with instance(permissions)

    bind<Geofence>() with instance(initialGeofence)
}