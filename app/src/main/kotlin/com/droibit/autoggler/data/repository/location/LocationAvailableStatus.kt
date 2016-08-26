package com.droibit.autoggler.data.repository.location

import android.app.Activity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class LocationAvailableStatus(private val status: Status) : AvailableStatus {

    override val isAvailable = status.statusCode == CommonStatusCodes.SUCCESS

    override val isResolutionRequired = status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED

    override fun startResolutionForResult(activity: Activity, requestCode: Int) = status.startResolutionForResult(activity, requestCode)

    override fun toString() = status.toString()
}
