@file:JvmName("GoogleApiClientExtension")
package com.droibit.autoggler.data.repository.source.api

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import com.google.android.gms.common.api.Status
import timber.log.Timber
import java.util.concurrent.TimeUnit

fun GoogleApiClient.blockingConnect(timeoutMillis: Long): Status {
    val connectionResult = blockingConnect(timeoutMillis, TimeUnit.MILLISECONDS)
    Timber.d("GoogleApi blockingConnect(timeoutMillis=$timeoutMillis): $connectionResult")
    return connectionResult.toStatus()
}

fun <T : Result> PendingResult<T>.await(timeoutMillis: Long): T {
    return if (timeoutMillis <= 0L) {
        await()
    } else {
        await(timeoutMillis, TimeUnit.MILLISECONDS)
    }
}

inline fun <T> GoogleApiClient.use(block: GoogleApiClient.() -> T): T {
    try {
        return block()
    } finally {
        if (isConnecting || isConnected) {
            disconnect()
        }
    }
}

fun ConnectionResult.toStatus(): Status {
    return Status(errorCode, errorMessage, resolution)
}
