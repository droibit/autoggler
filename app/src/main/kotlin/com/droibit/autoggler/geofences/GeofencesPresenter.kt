package com.droibit.autoggler.geofences

import android.support.annotation.VisibleForTesting
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.geofences.GeofencesContract.GeofenceMenuItem
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class GeofencesPresenter(
        private val view: GeofencesContract.View,
        private val navigator: GeofencesContract.Navigator,
        private val loadTask: GeofencesContract.LoadTask,
        private val deleteTask: GeofencesContract.DeleteTask,
        private val subscriptions: CompositeSubscription) : GeofencesContract.Presenter {

    override fun subscribe() {
        loadGeofences()
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
        navigator.navigateUpdateGeofence(geofence.id)
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
                            view.hideGeofence(geofence)
                        },
                        { e ->
                            Timber.e(e, "Delete Geofence:")
                            view.showGeofenceErrorToast()
                        }
                )
    }

    @VisibleForTesting
    internal fun loadGeofences() {
        loadTask.loadGeofences()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { geofences ->
                            if (geofences.isNotEmpty()) {
                                view.showGeofences(geofences)
                            } else {
                                view.showNoGeofences()
                            }
                        },
                        { e ->
                            Timber.d(e, "Load Gefence:")
                            view.showNoGeofences()
                        }
                ).addTo(subscriptions)
    }
}