package com.droibit.autoggler.data.repository.source

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

open class SyncValueHolder<T>() {

    private val latch = CountDownLatch(1)

    var value: T? = null

    fun await(timeoutMillis: Long): Boolean {
        return latch.await(timeoutMillis, TimeUnit.MILLISECONDS)
    }

    protected fun release() {
        latch.countDown()
    }
}