package com.droibit.autoggler.stetho

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun stethoModule() = Kodein.Module {
    bind<Stetho>() with singleton { Stetho(instance()) }
}
