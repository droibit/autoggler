package com.droibit.autoggler.data.checker.permission

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.content.PermissionChecker.checkSelfPermission

class RuntimePermissionCheckerImpl(private val context: Context) : RuntimePermissionChecker {

    override fun isRuntimePermissionsGranted(vararg permissions: String): Boolean {
        return permissions.all { checkSelfPermission(context, it) == PERMISSION_GRANTED }
    }

    override fun isRuntimePermissionsGranted(vararg grantResults: Int): Boolean {
        return grantResults.all { it == PERMISSION_GRANTED }
    }
}