package com.droibit.autoggler.edit

import android.support.annotation.VisibleForTesting
import com.jakewharton.rxrelay.PublishRelay

class LocationResolutionSource {

    @VisibleForTesting
    internal var startResolutionAction: (()->Unit)? = null

    val trigger: PublishRelay<Unit> = PublishRelay.create()

    fun prepareStartResolution(action: ()->Unit) {
        startResolutionAction = action
        trigger.call(null)
    }

    fun startResolutionForResult() {
        checkNotNull(startResolutionAction).invoke()
        startResolutionAction = null
    }
}