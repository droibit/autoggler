package com.droibit.autoggler.data.provider.time

import android.os.SystemClock

object TimeProviderImpl : TimeProvider {

    override val currentTimeMillis = System.currentTimeMillis()

    override val elapsedRealTimeMillis = SystemClock.elapsedRealtime()

    override val elapsedRealTimeNanos = SystemClock.elapsedRealtimeNanos()
}