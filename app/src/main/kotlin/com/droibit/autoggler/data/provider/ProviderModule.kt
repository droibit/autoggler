package com.droibit.autoggler.data.provider

import com.droibit.autoggler.data.provider.geometory.GeometryProvider
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.droibit.autoggler.data.provider.time.TimeProviderImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun providerModule() = Kodein.Module {

    bind<GeometryProvider>() with singleton { GeometryProvider(instance()) }

    bind<TimeProvider>() with singleton { TimeProviderImpl }
}