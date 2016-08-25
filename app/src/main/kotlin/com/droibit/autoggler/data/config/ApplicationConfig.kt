package com.droibit.autoggler.data.config


interface ApplicationConfig {

    val googleApiTimeoutMillis: Long

    val maxLastLocationElapsedTimeMillis: Long

    val currentLocationTimeoutMillis: Long
}