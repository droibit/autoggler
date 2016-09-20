package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import rx.Single

interface UpdateGeofenceContract {

    interface View {

        fun showEditableGeofence(geofence: Geofence)

        fun showUneditableGeofences(geofences: List<Geofence>)
    }

    interface Navigator {

        fun navigationToUp()

        fun finish(result: Geofence)
    }

    interface RuntimePermissions {

        enum class Usage(val requestCode: Int) {
            GEOFENCING(requestCode = 1)
        }

        fun requestLocationPermission(usage: Usage)
    }

    interface Presenter {

        fun onCreate()
    }

    interface LoadTask {

        class Event(val editableGeofence: Geofence, val uneditableGeofences: List<Geofence>)

        fun loadGeofence(id: Long): Single<Event>
    }
}