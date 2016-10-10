package com.droibit.autoggler.edit.update

import android.Manifest.permission.ACCESS_FINE_LOCATION
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofencingException
import com.droibit.autoggler.data.repository.geofence.GeofencingException.ErrorStatus.PERMISSION_DENIED
import com.droibit.autoggler.data.repository.geofence.GeofencingRepository
import rx.Single
import rx.lang.kotlin.single

class UpdateGeofencingTask(
        private val permissionChecker: RuntimePermissionChecker,
        private val geofencingRepository: GeofencingRepository) : UpdateGeofenceContract.UpdateGeofencingTask {

    override fun update(geofence: Geofence): Single<Boolean> {
        return single { subscriber ->
            if (!permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)) {
                subscriber.onError(GeofencingException(status = PERMISSION_DENIED))
                return@single
            }

            try {
                val updated = geofencingRepository.update(geofence)
                subscriber.onSuccess(updated)
            } catch (e: GeofencingException) {
                subscriber.onError(e)
            }
        }
    }
}