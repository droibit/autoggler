package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions
import com.google.android.gms.maps.model.Marker
import rx.subscriptions.CompositeSubscription


class UpdateGeofencePresenter(
        private val view: UpdateGeofenceContract.View,
        private val navigator: UpdateGeofenceContract.Navigator,
        private val permissions: RuntimePermissions,
        private val loadTask: UpdateGeofenceContract.LoadTask,
        private val subscriptions: CompositeSubscription,
        private val editableGeofence: Geofence) : UpdateGeofenceContract.Presenter {

    override fun onCreate() {
        TODO()
    }

    // View

    override fun onMapReady() {
        TODO()
    }

    override fun onMarkerInfoWindowClicked() {
        TODO()
    }

    override fun onMarkerClicked(marker: Marker) {
        TODO()
    }

    override fun onMarkerDragStart(marker: Marker) {
        TODO()
    }

    override fun onMarkerDragEnd() {
        TODO()
    }

    override fun onPrepareDragMode(marker: Marker) {
        TODO()
    }

    override fun onFinishedDragMode(marker: Marker) {
        TODO()
    }

    override fun onGeofenceUpdated(updated: Geofence) {
        TODO()
    }

    override fun onDoneButtonClicked(initial: Geofence) {
        TODO()
    }

    // Navigator

    override fun onUpNavigationButtonClicked() {
        TODO()
    }

    // RuntimePermissions

    override fun onLocationPermissionsResult(usage: RuntimePermissions.Usage, granted: Boolean) {
        TODO()
    }
}