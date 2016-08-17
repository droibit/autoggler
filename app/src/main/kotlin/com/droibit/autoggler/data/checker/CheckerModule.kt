package com.droibit.autoggler.data.checker

import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.checker.permission.RuntimePermissionCheckerImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun checkerModule() = Kodein.Module {

    bind<RuntimePermissionChecker>() with singleton { RuntimePermissionCheckerImpl(instance()) }
}