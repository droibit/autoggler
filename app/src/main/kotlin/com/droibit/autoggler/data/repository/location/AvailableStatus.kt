package com.droibit.autoggler.data.repository.location

import android.app.Activity


interface AvailableStatus {

    val isAvailable: Boolean

    val isResolutionRequired: Boolean

    fun startResolutionForResult(activity: Activity, requestCode: Int)
}