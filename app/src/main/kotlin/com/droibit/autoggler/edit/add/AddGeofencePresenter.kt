package com.droibit.autoggler.edit.add

import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import rx.subscriptions.CompositeSubscription

class AddGeofencePresenter(
        private val view: AddGeofenceContract.View,
        private val navigator: AddGeofenceContract.Navigator,
        private val getCurrentLocationTask: AddGeofenceContract.GetCurrentLocationTask,
        private val permissionChecker: RuntimePermissionChecker,
        private val subscriptions: CompositeSubscription) : AddGeofenceContract.Presenter {

    override fun onCreate() {
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.unsubscribe()
    }

    override fun onUpNavigationButtonClicked() = navigator.navigationToUp()
}
