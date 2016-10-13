package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions
import com.google.android.gms.maps.model.Marker
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription


class UpdateGeofencePresenter(
        private val view: UpdateGeofenceContract.View,
        private val navigator: UpdateGeofenceContract.Navigator,
        private val permissions: UpdateGeofenceContract.RuntimePermissions,
        private val loadTask: UpdateGeofenceContract.LoadTask,
        private val updateGeofencingTask: UpdateGeofenceContract.UpdateGeofencingTask,
        private val subscriptions: CompositeSubscription,
        private val editableGeofence: Geofence) : UpdateGeofenceContract.Presenter {

    override fun onSavedInstanceState(outStateWrapper: () -> Bundle) {
        view.saveInstanceState(target = editableGeofence, outStateWrapper = outStateWrapper)
    }

    // View

    override fun onMapReady(isRestoredGeometory: Boolean) {
        if (!isRestoredGeometory) {
            view.showEditableGeofence(editableGeofence)
        }
        loadTask.loadGeofences(ignoreId = editableGeofence.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.showUneditableGeofences(geofences = it)
                }
    }

    override fun onMarkerClicked(marker: Marker) {
        if (view.isDragActionModeShown() || !view.isEditableMarker(marker)) {
            return
        }

        if (!marker.isInfoWindowShown) {
           view.showMarkerInfoWindow(marker)
        }
        view.showEditDialog(target = editableGeofence)
    }

    override fun onMarkerInfoWindowClicked(marker: Marker) {
        if (view.isDragActionModeShown() || !view.isEditableMarker(marker)) {
            return
        }
        view.showEditDialog(target = editableGeofence)
    }

    override fun onMarkerDragStart(marker: Marker) {
        if (!view.isDragActionModeShown()) {
            view.startMarkerDragMode()
        }

        if (marker.isInfoWindowShown) {
            view.hideMarkerInfoWindow(marker)
        }
        view.hideEditableGeofenceCircle()
    }

    override fun onMarkerDragEnd() {
        view.showEditableGeofenceCircle()
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

    override fun onDoneButtonClicked() {
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