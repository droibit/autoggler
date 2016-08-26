package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.Event
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException.ErrorStatus.*
import rx.subscriptions.CompositeSubscription

class AddGeofencePresenter(
        private val view: AddGeofenceContract.View,
        private val permissions: AddGeofenceContract.RuntimePermissions,
        private val navigator: AddGeofenceContract.Navigator,
        private val getCurrentLocationTask: AddGeofenceContract.GetCurrentLocationTask,
        private val permissionChecker: RuntimePermissionChecker,
        private val subscriptions: CompositeSubscription) : AddGeofenceContract.Presenter {

    override fun onCreate() {
        val locationPermissionGranted = permissionChecker.isRuntimePermissionsGranted(ACCESS_FINE_LOCATION)
        view.enableMyLocationButton(locationPermissionGranted)

        getCurrentLocationTask.requestLocation()
    }

    override fun subscribe() {
        subscribeCurrentLocation()
    }

    override fun unsubscribe() {
        subscriptions.unsubscribe()
    }

    // Navigator

    override fun onUpNavigationButtonClicked() {
        navigator.navigationToUp()
    }

    override fun onLocationResolutionResult(resolved: Boolean) {
        TODO()
    }

    // RuntimePermissions

    override fun onRequestPermissionsResult(grantResults: IntArray) {
        TODO()
    }

    private fun subscribeCurrentLocation() {
        getCurrentLocationTask.asObservable()
            .subscribe { event ->
                when (event) {
                    is Event.OnSuccess -> onGotCurrentLocation(event.location)
                    is Event.OnError -> onCurrentLocationError(event.exception)
                }
            }
    }

    private fun onGotCurrentLocation(location: Location?) {
        if (location != null) {
            view.showLocation(location)
        } else {
            view.showCurrentLocationErrorToast(0)
        }
    }

    private fun onCurrentLocationError(e: UnavailableLocationException) {
        when (e.status) {
            PERMISSION_DENIED -> permissions.requestPermissions(ACCESS_FINE_LOCATION)
            RESOLUTION_REQUIRED -> navigator.showLocationResolutionDialog(checkNotNull(e.option))
            ERROR -> view.showCurrentLocationErrorToast(0)
        }
    }
}
