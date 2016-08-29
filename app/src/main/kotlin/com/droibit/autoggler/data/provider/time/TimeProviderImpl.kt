package com.droibit.autoggler.data.provider.time

import android.os.SystemClock

object TimeProviderImpl : TimeProvider {

    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()

    override val elapsedRealTimeMillis: Long
        get() = SystemClock.elapsedRealtime()

    override val elapsedRealTimeNanos: Long
        get() = SystemClock.elapsedRealtimeNanos()
}