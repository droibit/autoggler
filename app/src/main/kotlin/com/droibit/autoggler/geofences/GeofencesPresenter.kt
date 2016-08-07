package com.droibit.autoggler.geofences

import android.support.annotation.VisibleForTesting
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class GeofencesPresenter(
        private val view: GeofencesContract.View,
        private val navigator: GeofencesContract.Navigator,
        private val loadTask: GeofencesContract.LoadTask,
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
                            view.showNoGeofences()
                            Timber.d(e.message)
                        }
                ).addTo(subscriptions)
    }
}