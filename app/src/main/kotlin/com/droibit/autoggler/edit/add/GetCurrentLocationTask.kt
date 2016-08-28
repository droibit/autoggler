package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.repository.location.LocationRepository
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.Event
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException.ErrorStatus.*
import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable
import rx.Single
import rx.lang.kotlin.single
import rx.schedulers.Schedulers
import timber.log.Timber

class GetCurrentLocationTask(
        private val relay: BehaviorRelay<Event>,
        private val locationRepository: LocationRepository,
        private val permissionChecker: RuntimePermissionChecker,
        private val config: ApplicationConfig) : AddGeofenceContract.GetCurrentLocationTask {


    override fun asObservable(): Observable<Event> {
        return relay.filter { it != Event.OnCompleted }
                .doOnNext { relay.call(Event.OnCompleted) }
    }

    override fun requestLocation() {
        Timber.d("requestLocation")

        getLocationAsSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { v -> relay.call(Event.OnSuccess(location = v)) },
                        { e -> relay.call(Event.OnError(exception = e as UnavailableLocationException)) }
                )
    }

    private fun getLocationAsSingle(): Single<Location?> {
        return single { subscriber ->
            if (!permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)) {
                subscriber.onError(UnavailableLocationException(PERMISSION_DENIED))
                return@single
            }

            val availableStatus = locationRepository.getLocationAvailableStatus()
            when {
                availableStatus.isAvailable -> {
                    val location = locationRepository.getCurrentLocation(
                            config.maxLastLocationElapsedTimeMillis,
                            config.currentLocationTimeoutMillis
                    )
                    subscriber.onSuccess(location)
                }
                availableStatus.isResolutionRequired -> {
                    subscriber.onError(UnavailableLocationException(RESOLUTION_REQUIRED, option = availableStatus))
                }
                else -> subscriber.onError(UnavailableLocationException(ERROR))
            }
        }
    }
}