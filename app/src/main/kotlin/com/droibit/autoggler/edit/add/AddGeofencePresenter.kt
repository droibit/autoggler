package com.droibit.autoggler.edit.add

import android.location.Location
import android.support.annotation.VisibleForTesting
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.droibit.autoggler.data.repository.location.UnavailableLocationException.ErrorStatus.*
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.GetCurrentLocationEvent
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GET_LOCATION
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
        private val registerGeofencingTask: AddGeofenceContract.RegisterGeofencingTask,
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

        geofence.latlng(marker.position)
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

        geofence.latlng(marker.position)
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

    override fun onDoneButtonClicked() {
        when {
            !view.hasGeofenceGeometory() -> view.showErrorToast(R.string.add_geofence_not_yet_add_marker)
            geofence.name.isEmpty() -> view.showErrorToast(R.string.add_geofence_not_entered_name)
            else -> {
                view.setDoneButtonEnabled(false)
                subscribeRegisterGeofencing()
            }
        }
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

    override fun onLocationPermissionsResult(usage: RuntimePermissions.Usage, granted: Boolean) {
        when (usage) {
            GET_LOCATION -> onLocationPermissionsResultForGetLocation(granted)
            GEOFENCING -> onLocationPermissionsResultForGeofencing(granted)
        }
    }

    private fun onLocationPermissionsResultForGetLocation(granted: Boolean) {
        Timber.d("onLocationPermissionsResultForGetLocation($granted)")

        if (granted) {
            view.enableMyLocationButton(true)
            getCurrentLocationTask.requestLocation()
        } else {
            view.showErrorToast(R.string.add_geofence_get_current_location_error)
        }
    }

    fun onLocationPermissionsResultForGeofencing(granted: Boolean) {
        Timber.d("onLocationPermissionsResultForGeofencing($granted)")

        if (granted) {
            subscribeRegisterGeofencing()
        } else {
            view.setDoneButtonEnabled(true)
            view.showLocationPermissionRationaleSnackbar()
        }
    }

    // Private

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
            PERMISSION_DENIED -> permissions.requestLocationPermission(usage = GET_LOCATION)
            RESOLUTION_REQUIRED -> navigator.showLocationResolutionDialog(checkNotNull(e.option))
            ERROR -> view.showErrorToast(msgId = R.string.add_geofence_get_current_location_error)
        }
    }

    @VisibleForTesting
    internal fun subscribeRegisterGeofencing() {
        registerGeofencingTask.register(geofence)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { v -> onRegisterGeofencingResult(registered = v) },
                        { e -> onRegisterGeofencingError(e as UnavailableLocationException) }
                )
    }

    private fun onRegisterGeofencingResult(registered: Boolean) {
        Timber.d("onRegisterGeofencingResult($registered)")

        if (registered) {
            navigator.finish(result = geofence)
        } else {
            view.setDoneButtonEnabled(true)
            view.showErrorToast(R.string.add_geofence_failed_register_geofence)
        }
    }

    // TODO: Add validation error
    private fun onRegisterGeofencingError(e: UnavailableLocationException) {
        Timber.d("onRegisterGeofencingError(${e.status})")

        when (e.status) {
            PERMISSION_DENIED -> permissions.requestLocationPermission(usage = GEOFENCING)
            else -> IllegalArgumentException("Unknown error: ${e.status}")
        }
        view.setDoneButtonEnabled(true)
    }
}
