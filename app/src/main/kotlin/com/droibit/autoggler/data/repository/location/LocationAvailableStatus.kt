package com.droibit.autoggler.data.repository.location

import android.app.Activity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class LocationAvailableStatus(private val status: Status) {

    val isEnabled = status.statusCode == CommonStatusCodes.SUCCESS

    val isResolutionRequired = status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED

    fun startResolutionForResult(activity: Activity, requestCode: Int) = status.startResolutionForResult(activity, requestCode)

    override fun toString() = status.toString()
}
