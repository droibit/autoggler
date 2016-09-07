package com.droibit.autoggler.data.repository.location

import android.location.Location
import com.google.android.gms.common.api.CommonStatusCodes.*
import com.google.android.gms.common.api.Status

object MockLocationRepository {

    class Success : LocationRepository {
        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location? {
            return Location("mock").apply {
                latitude = 35.681298
                longitude = 139.7640582
            }
        }

        override fun getLocationAvailableStatus(): AvailableStatus {
            return LocationAvailableStatus(Status(SUCCESS))
        }
    }

    class NullLocation : LocationRepository {

        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location? = null

        override fun getLocationAvailableStatus(): AvailableStatus {
            return LocationAvailableStatus(Status(SUCCESS))
        }
    }

    class NotAvailableLocation : LocationRepository {

        override fun getCurrentLocation(maxLastLocationElapsedTimeMillis: Long, timeoutMillis: Long): Location? = null

        override fun getLocationAvailableStatus(): AvailableStatus {
            return LocationAvailableStatus(Status(ERROR))
        }
    }
}
