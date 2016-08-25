package com.droibit.autoggler.data.provider.time


interface TimeProvider {

    val currentTimeMillis: Long

    val elapsedRealTimeMillis: Long
}