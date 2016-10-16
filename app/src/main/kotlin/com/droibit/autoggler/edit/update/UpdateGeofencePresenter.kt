package com.droibit.autoggler.edit.update

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofencingException
import com.droibit.autoggler.data.repository.geofence.GeofencingException.ErrorStatus.PERMISSION_DENIED
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions.Usage
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.google.android.gms.maps.model.Marker
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber


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
        view.setLocation(editableGeofence.latLong)

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
        view.hideDoneButton()
    }

    override fun onFinishedDragMode(marker: Marker) {
        view.showDoneButton()
        view.setLocation(marker.position)

        editableGeofence.latlng(marker.position)
    }

    override fun onGeofenceUpdated(updated: Geofence) {
        editableGeofence.apply {
            name = updated.name
            circle = updated.circle.clone()
            toggle = updated.toggle.clone()
        }
        view.setMarkerInfoWindow(title = updated.name, snippet = null)
        view.setGeofenceRadius(updated.radius)

    }

    override fun onDoneButtonClicked() {
        view.setDoneButtonEnabled(false)
        subscribeUpdateGeofencing()
    }

    // Navigator

    override fun onUpNavigationButtonClicked() {
        navigator.navigateToUp()
    }

    // RuntimePermissions

    override fun onLocationPermissionsResult(usage: Usage, granted: Boolean) {
        Timber.d("onLocationPermissionsResultForGeofencing($granted)")

        if (granted) {
            subscribeUpdateGeofencing()
        } else {
            view.setDoneButtonEnabled(true)
            //view.showLocationPermissionRationaleSnackbar()
        }
    }

    // Private

    @VisibleForTesting
    internal fun subscribeUpdateGeofencing() {
        updateGeofencingTask.update(editableGeofence)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { v -> onUpdateGeofencingResult(updated = v) },
                        { e -> onUpdateGeofencingError(e as GeofencingException) }
                )
    }

    private fun onUpdateGeofencingResult(updated: Boolean) {
        Timber.d("onUpdateGeofencingResult($updated)")

        if (updated) {
            navigator.finish(result = editableGeofence)
        } else {
            view.setDoneButtonEnabled(true)
            view.showErrorToast(R.string.update_geofence_failed_update_geofence)
        }
    }

    private fun onUpdateGeofencingError(e: GeofencingException) {
        Timber.d("onUpdateGeofencingError(${e.status})")

        when (e.status) {
            PERMISSION_DENIED -> permissions.requestLocationPermission(usage = GEOFENCING)
            else -> {
                // TODO
            }
        }
        view.setDoneButtonEnabled(true)
    }
}