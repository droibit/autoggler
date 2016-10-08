package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.geofences.GeofencesContract.GeofenceMenuItem
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class GeofencesPresenter(
        private val view: GeofencesContract.View,
        private val navigator: GeofencesContract.Navigator,
        private val loadTask: GeofencesContract.LoadTask,
        private val deleteTask: GeofencesContract.DeleteTask,
        private val subscriptions: CompositeSubscription) : GeofencesContract.Presenter {

    override fun onCreate() {
        loadGeofences()
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun onMenuItemSelected(navItem: NavItem): Boolean {
        return when (navItem) {
            NavItem.SETTINGS -> {
                navigator.navigateSettings(); true
            }
            else -> false
        }
    }

    override fun onGeofenceAddButtonClicked() {
        navigator.navigateAddGeofence()
    }

    override fun onGeofenceSelected(geofence: Geofence) {
        navigator.navigateUpdateGeofence(geofence)
    }

    override fun onGeofenceMenuItemSelected(menuItem: GeofenceMenuItem, targetId: Long) {
        when (menuItem) {
            GeofenceMenuItem.DELETE -> view.showDeleteConfirmDialog(targetId)
        }
    }

    override fun onDeleteConfirmDialogOkClicked(targetId: Long) {
        deleteTask.deleteGeofence(targetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { geofence ->
                            Timber.d("Delete successful: ${geofence.id}")
                            view.hideGeofence(geofence)
                        },
                        { e ->
                            Timber.e(e, "Delete Geofence:")
                            view.showGeofenceErrorToast()
                        }
                )
        // TODO: need addTo(subscriptions) ...?
    }

    override fun onAddGeofenceResult(newGeofence: Geofence) {
        Timber.d("onAddGeofenceResult($newGeofence)")
        view.showGeofence(newGeofence)
    }

    private fun loadGeofences() {
        loadTask.loadGeofences()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { v -> onLoadGeofencesSuccess(geofences = v) },
                        { e -> onLoadGeofenceError(error = e) }
                )
    }

    private fun onLoadGeofencesSuccess(geofences: List<Geofence>) {
        if (geofences.isNotEmpty()) {
            view.showGeofences(geofences)
        } else {
            view.showNoGeofences()
        }
        Timber.d("Loaded geofence count: ${geofences.size}")
    }

    private fun onLoadGeofenceError(error: Throwable) {
        Timber.d(error, "Load geofence:")
        view.showNoGeofences()
    }
}