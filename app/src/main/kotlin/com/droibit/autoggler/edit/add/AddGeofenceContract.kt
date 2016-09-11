package com.droibit.autoggler.edit.add

import android.location.Location
import android.support.annotation.StringRes
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import rx.Observable
import rx.Single


interface AddGeofenceContract {

    interface View {

        fun canRegisterGeofencing(): Boolean

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

        fun finish(result: Geofence)
    }

    interface RuntimePermissions {

        enum class Usage(val requestCode: Int) {
            GET_LOCATION(requestCode = 1),
            GEOFENCING(requestCode = 2)
        }

        fun requestLocationPermission(usage: RuntimePermissions.Usage)
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

        fun onPrepareDragMode(marker: Marker)

        fun onFinishedDragMode(marker: Marker)

        fun onGeofenceUpdated(updated: Geofence)

        fun onDoneButtonClicked()

        // Navigator

        fun onUpNavigationButtonClicked()

        fun onLocationResolutionResult(resolved: Boolean)

        // RuntimePermissions

        fun onLocationPermissionsResult(usage: RuntimePermissions.Usage, granted: Boolean)
    }

    interface GetCurrentLocationTask {

        sealed class GetCurrentLocationEvent {
            class OnSuccess(val location: Location?) : GetCurrentLocationEvent()
            class OnError(val exception: UnavailableLocationException) : GetCurrentLocationEvent()
            object Nothing : GetCurrentLocationEvent()
        }

        fun requestLocation()

        fun asObservable(): Observable<GetCurrentLocationEvent>
    }

    interface RegisterGeofencingTask {

        fun register(geofence: Geofence): Single<Boolean>
    }
}
