package com.droibit.autoggler.edit.add

import android.location.Location
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import com.droibit.autoggler.data.repository.location.AvailableStatus
import rx.Observable


interface AddGeofenceContract {

    interface View {

        fun enableMyLocationButton(enable: Boolean)

        fun showLocation(location: Location)

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
