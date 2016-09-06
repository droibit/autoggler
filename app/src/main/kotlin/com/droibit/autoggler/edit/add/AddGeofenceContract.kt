package com.droibit.autoggler.edit.add

import android.location.Location
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import rx.Observable


interface AddGeofenceContract {

    interface View {

        fun canDropMarker(): Boolean

        fun dropMarker(point: LatLng)

        fun showMarkerInfoWindow(marker: Marker)

        fun hideMarkerInfoWindow(marker: Marker)

        fun setMarkerInfoWindow(title: String, snippet: String?)

        fun isDragActionModeShown(): Boolean

        fun showEditDialog(target: Geofence)

        fun startMarkerDragMode()

        fun enableMyLocationButton(enabled: Boolean)

        fun setLocation(location: Location)

        fun setLocation(location: LatLng)

        fun showDoneButton()

        fun hideDoneButton()

        fun showGeofenceCircle()

        fun hideGeofenceCircle()

        fun setGeofenceRadius(radius: Double)

        fun showErrorToast(@StringRes msgId: Int)
    }

    interface Navigator {

        fun showLocationResolutionDialog(status: AvailableStatus)

        fun navigationToUp()
    }

    interface RuntimePermissions {

        fun requestPermissions(vararg permissions: String)
    }

    interface Presenter {

        fun onCreate()

        fun subscribe()

        fun unsubscribe()

        // View

        fun onMapLongClicked(point: LatLng)

        fun onMarkerInfoWindowClicked()

        fun onMarkerDropped(marker: Marker)

        fun onMarkerClicked(marker: Marker)

        fun onMarkerDragStart(marker: Marker)

        fun onMarkerDragEnd()

        fun onPrepareDragMode()

        fun onFinishedDragMode()

        fun onDoneButtonClicked()

        fun onGeofenceUpdated(updated: Geofence)

        // Navigator

        fun onUpNavigationButtonClicked()

        fun onLocationResolutionResult(resolved: Boolean)

        // RuntimePermissions

        fun onRequestPermissionsResult(grantResults: IntArray)
    }

    interface GetCurrentLocationTask {

        sealed class Event {
            class OnSuccess(val location: Location?) : Event()
            class OnError(val exception: UnavailableLocationException) : Event()
            object OnCompleted : Event()
        }

        fun requestLocation()

        fun asObservable(): Observable<Event>
    }

    class UnavailableLocationException(
            val status: ErrorStatus,
            val option: AvailableStatus? = null) : Exception() {

        enum class ErrorStatus {
            PERMISSION_DENIED,
            RESOLUTION_REQUIRED,
            ERROR,
        }
    }
}
