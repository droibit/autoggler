package com.droibit.autoggler.edit.add

import android.location.Location
import android.support.annotation.IdRes
import com.droibit.autoggler.data.repository.location.LocationAvailableStatus
import com.google.android.gms.common.api.Status
import rx.Observable


interface AddGeofenceContract {

    interface View {

        fun enableMyLocationButton(enable: Boolean)

        fun showLocation(location: Location)

        fun showCurrentLocationErrorToast(@IdRes resId: Int)
    }

    interface Navigator {

        fun showLocationResolutionDialog(status: LocationAvailableStatus)

        fun navigationToUp()
    }

    interface RuntimePermissions {

        fun requestPermissions(vararg permissions: String)
    }

    interface Presenter {

        fun onCreate()

        fun subscribe()

        fun unsubscribe()

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
            val option: LocationAvailableStatus? = null) : Exception() {

        enum class ErrorStatus {
            PERMISSION_DENIED,
            RESOLUTION_REQUIRED,
            ERROR,
        }
    }
}
