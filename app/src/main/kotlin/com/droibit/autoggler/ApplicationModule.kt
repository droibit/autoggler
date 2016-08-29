package com.droibit.autoggler

import android.content.Context
import android.os.Looper
import com.droibit.autoggler.utils.EmptyTimberTree
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import timber.log.Timber

fun applicationModule(context: Context, debuggable: Boolean) = Kodein.Module {

    bind<Context>() with instance(context)

    bind<Timber.Tree>() with singleton { if (debuggable) Timber.DebugTree() else EmptyTimberTree }

    bind<Looper>("main") with singleton { Looper.getMainLooper() }
}