package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import com.droibit.autoggler.R
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.droibit.autoggler.data.repository.location.UnavailableLocationException.ErrorStatus.*
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.GetCurrentLocationEvent
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class AddGeofencePresenter(
        private val view: AddGeofenceContract.View,
        private val permissions: AddGeofenceContract.RuntimePermissions,
        private val navigator: AddGeofenceContract.Navigator,
        private val getCurrentLocationTask: AddGeofenceContract.GetCurrentLocationTask,
        private val permissionChecker: RuntimePermissionChecker,
        private val subscriptions: CompositeSubscription,
        private val geofence: Geofence) : AddGeofenceContract.Presenter {

    override fun onCreate() {
        getCurrentLocationTask.requestLocation()
    }

    override fun subscribe() {
        subscribeCurrentLocation()
    }

    override fun unsubscribe() {
        Timber.d("unsubscribe")
        subscriptions.clear()
    }

    // View

    override fun onMapLongClicked(point: LatLng) {
        if (view.canDropMarker()) {
            view.dropMarker(point)
        }
    }

    override fun onMarkerInfoWindowClicked() {
        if (!view.isDragActionModeShown()) {
            view.showEditDialog(target = geofence)
        }
    }

    override fun onMarkerDropped(marker: Marker) {
        view.showMarkerInfoWindow(marker)
        view.setLocation(marker.position)
    }

    override fun onMarkerClicked(marker: Marker) {
        if (view.isDragActionModeShown()) {
            return
        }

        if (!marker.isInfoWindowShown) {
            view.showMarkerInfoWindow(marker)
        }
        view.showEditDialog(target = geofence)
    }

    override fun onMarkerDragStart(marker: Marker) {
        if (!view.isDragActionModeShown()) {
            view.startMarkerDragMode()
        }

        if (marker.isInfoWindowShown) {
            view.hideMarkerInfoWindow(marker)
        }
        view.hideGeofenceCircle()
    }

    override fun onMarkerDragEnd() {
        view.showGeofenceCircle()
    }

    override fun onPrepareDragMode(marker: Marker) {
        view.hideDoneButton()
    }

    override fun onFinishedDragMode(marker: Marker) {
        view.showDoneButton()
        view.setLocation(marker.position)
    }

    override fun onDoneButtonClicked() {
        TODO()
    }

    override fun onGeofenceUpdated(updated: Geofence) {
        geofence.apply {
            name = updated.name
            circle = updated.circle.clone()
            toggle = updated.toggle.clone()
        }
        view.setMarkerInfoWindow(title = updated.name, snippet = null)
        view.setGeofenceRadius(updated.radius)
    }

    // Navigator

    override fun onUpNavigationButtonClicked() {
        navigator.navigationToUp()
    }

    override fun onLocationResolutionResult(resolved: Boolean) {
        if (resolved) {
            getCurrentLocationTask.requestLocation()
        } else {
            view.showErrorToast(R.string.add_geofence_get_current_location_error)
        }
    }

    // RuntimePermissions

    override fun onRequestPermissionsResult(grantResults: IntArray) {
        if (permissionChecker.isPermissionsGranted(*grantResults)) {
            view.enableMyLocationButton(true)
            getCurrentLocationTask.requestLocation()
        } else {
            view.showErrorToast(R.string.add_geofence_get_current_location_error)
        }
    }

    private fun subscribeCurrentLocation() {
        Timber.d("subscribeCurrentLocation")

        getCurrentLocationTask.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    when (event) {
                        is GetCurrentLocationEvent.OnSuccess -> onCurrentLocationSuccess(event.location)
                        is GetCurrentLocationEvent.OnError -> onCurrentLocationError(event.exception)
                    }
                }.addTo(subscriptions)
    }

    private fun onCurrentLocationSuccess(location: Location?) {
        Timber.d("onCurrentLocationSuccess($location)")

        if (location != null) {
            view.setLocation(location)
        } else {
            view.showErrorToast(msgId = R.string.add_geofence_get_current_location_failed)
        }
    }

    private fun onCurrentLocationError(e: UnavailableLocationException) {
        Timber.d("onCurrentLocationError(${e.status})")

        when (e.status) {
            PERMISSION_DENIED -> permissions.requestPermissions(ACCESS_FINE_LOCATION)
            RESOLUTION_REQUIRED -> navigator.showLocationResolutionDialog(checkNotNull(e.option))
            ERROR -> view.showErrorToast(msgId = R.string.add_geofence_get_current_location_error)
        }
    }
}
