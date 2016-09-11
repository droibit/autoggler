package com.droibit.autoggler.edit

import android.app.Activity
import com.jakewharton.rxrelay.PublishRelay

class PendingRuntimePermissions(val source: Activity) {

    val trigger: PublishRelay<Unit> = PublishRelay.create()
}
