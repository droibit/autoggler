package com.droibit.autoggler.edit

import com.jakewharton.rxrelay.PublishRelay

class LocationResolutionSource {

    private var startResolutionAction: (()->Unit)? = null

    val trigger = PublishRelay.create<Any>()

    fun prepareStartResolution(action: ()->Unit) {
        startResolutionAction = action
        trigger.call(null)
    }

    fun startResolutionForResult() {
        checkNotNull(startResolutionAction).invoke()
        startResolutionAction = null
    }
}