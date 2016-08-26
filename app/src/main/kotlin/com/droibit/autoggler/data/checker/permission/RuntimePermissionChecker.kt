package com.droibit.autoggler.data.checker.permission

interface RuntimePermissionChecker {

    fun isPermissionsGranted(vararg permissions: String): Boolean

    fun isPermissionsGranted(vararg grantResults: Int): Boolean
}