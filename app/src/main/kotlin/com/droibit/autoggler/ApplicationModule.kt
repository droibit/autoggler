package com.droibit.autoggler

import android.content.Context
import com.droibit.autoggler.utils.EmptyTimberTree
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import timber.log.Timber

fun applicationModule(context: Context, debuggable: Boolean) = Kodein.Module {

    bind<Context>() with instance(context)

    if (debuggable) {
        bind<Timber.Tree>() with singleton { Timber.DebugTree() }
    } else {
        bind<Timber.Tree>() with singleton { EmptyTimberTree() }
    }
}