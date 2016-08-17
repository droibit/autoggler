package com.droibit.autoggler.edit.add

import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker

class AddGeofencePresenter(
        private val view: AddGeofenceContract.View,
        private val navigator: AddGeofenceContract.Navigator,
        private val permissionChecker: RuntimePermissionChecker) : AddGeofenceContract.Presenter {

    override fun onUpNavigationButtonClicked() = navigator.navigationToUp()
}
