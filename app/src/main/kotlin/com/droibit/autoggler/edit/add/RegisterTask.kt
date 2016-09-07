package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofencingRepository
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.droibit.autoggler.data.repository.location.UnavailableLocationException.ErrorStatus
import rx.Single
import rx.lang.kotlin.single

class RegisterTask(
        private val geofencingRepository: GeofencingRepository,
        private val permissionChecker: RuntimePermissionChecker) : AddGeofenceContract.RegisterTask {

    override fun register(geofence: Geofence): Single<Boolean> {
        return single { subscriber ->
            if (!permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)) {
                subscriber.onError(UnavailableLocationException(status = ErrorStatus.PERMISSION_DENIED))
                return@single
            }

            val registered = geofencingRepository.register(geofence)
            subscriber.onSuccess(registered)
        }
    }
}