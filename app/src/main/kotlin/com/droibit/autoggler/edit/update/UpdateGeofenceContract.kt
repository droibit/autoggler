package com.droibit.autoggler.edit.update

import android.support.annotation.StringRes
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.google.android.gms.maps.model.Marker
import rx.Single

interface UpdateGeofenceContract {

    interface View {

        fun showEditableGeofence(geofence: Geofence)

        fun showUneditableGeofences(geofences: List<Geofence>)

        fun showMarkerInfoWindow(marker: Marker)

        fun hideMarkerInfoWindow(marker: Marker)

        fun isDragActionModeShown(): Boolean

        fun showEditDialog(target: Geofence)

        fun startMarkerDragMode()

        fun setDoneButtonEnabled(enabled: Boolean)

        fun showDoneButton()

        fun hideDoneButton()

        fun showGeofenceCircle()

        fun hideGeofenceCircle()

        fun setGeofenceRadius(radius: Double)

        fun showErrorToast(@StringRes msgId: Int)
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

        // View

        fun onMapReady()

        fun onMarkerInfoWindowClicked()

        fun onMarkerClicked(marker: Marker)

        fun onMarkerDragStart(marker: Marker)

        fun onMarkerDragEnd()

        fun onPrepareDragMode(marker: Marker)

        fun onFinishedDragMode(marker: Marker)

        fun onGeofenceUpdated(updated: Geofence)

        fun onDoneButtonClicked(initial: Geofence)

        // Navigator

        fun onUpNavigationButtonClicked()


        // RuntimePermissions

        fun onLocationPermissionsResult(usage: RuntimePermissions.Usage, granted: Boolean)
    }

    interface LoadTask {

        class Event(val editableGeofence: Geofence, val uneditableGeofences: List<Geofence>)

        fun loadGeofences(editableId: Long): Single<Event>
    }
}