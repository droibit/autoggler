package com.droibit.autoggler.data.repository.location

import android.location.Location
import com.google.android.gms.common.api.CommonStatusCodes.*
import com.google.android.gms.common.api.Status

object Mock {

    class LocationRepositoryImpl : LocationRepository {
        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, locationTimeoutMillis: Long): Location? {
            return Location("mock").apply {
                latitude = 35.681298
                longitude = 139.7640582
            }
        }

        override fun getLocationAvailableStatus(): LocationAvailableStatus {
            return LocationAvailableStatus(Status(SUCCESS))
        }
    }

    class NullLocationRepository : LocationRepository {

        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, locationTimeoutMillis: Long): Location? = null

        override fun getLocationAvailableStatus(): LocationAvailableStatus {
            return LocationAvailableStatus(Status(SUCCESS))
        }
    }

    class NotAvailableLocationRepository : LocationRepository {

        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, locationTimeoutMillis: Long): Location? = null

        override fun getLocationAvailableStatus(): LocationAvailableStatus {
            return LocationAvailableStatus(Status(ERROR))
        }
    }
}
