package com.droibit.autoggler.data.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton

fun configModule() = Kodein.Module {

    bind<ApplicationConfig>() with singleton { ApplicationConfigImpl }
}