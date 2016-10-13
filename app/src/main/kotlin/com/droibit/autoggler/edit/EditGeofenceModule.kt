package com.droibit.autoggler.edit

import android.app.Activity
import com.droibit.autoggler.data.provider.rx.RxBus
import com.github.droibit.rxactivitylauncher.PendingLaunchAction
import com.github.droibit.rxactivitylauncher.RxActivityLauncher
import com.github.droibit.rxruntimepermissions.PendingRequestPermissionsAction
import com.github.droibit.rxruntimepermissions.RxRuntimePermissions
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton

fun editGeofenceModule(activity: Activity, dragCallback: DragActionMode.Callback) = Kodein.Module {

    bind<Activity>() with instance(activity)

    bind<RxActivityLauncher>() with provider { RxActivityLauncher() }

    bind<RxRuntimePermissions>() with provider { RxRuntimePermissions() }

    bind<PendingRequestPermissionsAction>() with provider { PendingRequestPermissionsAction(instance()) }

    bind<DragActionMode>() with provider { DragActionMode(activity = instance(), callback = dragCallback) }

    bind<PendingLaunchAction>() with provider { PendingLaunchAction() }

    bind<RxBus>() with singleton { RxBus() }
}
