package com.droibit.autoggler.data.checker.permission

interface RuntimePermissionChecker {

    fun isRuntimePermissionsGranted(vararg permissions: String): Boolean

    fun isRuntimePermissionsGranted(vararg grantResults: Int): Boolean
}