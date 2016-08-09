package com.droibit.autoggler.data.geometory

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun geometryModule() = Kodein.Module {

    bind<GeometryProvider>() with singleton { GeometryProvider(instance()) }
}