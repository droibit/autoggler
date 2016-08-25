package com.droibit.autoggler.edit.add

import android.location.Location
import com.droibit.autoggler.data.repository.location.LocationAvailableStatus
import com.google.android.gms.common.api.Status
import rx.Observable


interface AddGeofenceContract {

    interface View {

        fun enableMyLocationButton(enable: Boolean)

        fun showLocation(location: Location)
    }

    interface Navigator {

        fun showLocationResolutionDialog(status: Status)

        fun navigationToUp()
    }

    interface Presenter {

        fun onCreate()

        fun subscribe()

        fun unsubscribe()

        fun onUpNavigationButtonClicked()
    }

    interface GetCurrentLocationTask {

        sealed class Event {
            class OnSuccess(val location: Location?) : Event()
            class OnError(val exception: UnavailableLocationException) : Event()
            object OnCompleted : Event()
        }

        fun getLocation()

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
