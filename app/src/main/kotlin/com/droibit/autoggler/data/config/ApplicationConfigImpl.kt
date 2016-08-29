package com.droibit.autoggler.data.config

import java.util.concurrent.TimeUnit


object ApplicationConfigImpl : ApplicationConfig {

    // 1sec
    override val googleApiTimeoutMillis = TimeUnit.SECONDS.toMillis(1)

    // 5min
    override val maxLastLocationElapsedTimeMillis = TimeUnit.MINUTES.toMillis(5)

    // 30sec
    override val currentLocationTimeoutMillis = TimeUnit.SECONDS.toMillis(30)

    // 1.5sec
    override val bounceDropAnimateDurationMillis = 1500L
}