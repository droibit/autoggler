package com.droibit.autoggler.edit.add

import android.location.Location
import com.droibit.autoggler.data.repository.location.LocationAvailableStatus
import rx.Observable


interface AddGeofenceContract {

    interface View {

    }

    interface Navigator {

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
