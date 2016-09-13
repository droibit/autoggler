package com.droibit.autoggler.geofences

import android.support.annotation.IdRes
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import rx.Single


interface GeofencesContract {

    enum class NavItem(@IdRes private val id: Int) {
        SETTINGS(R.id.settings);

        companion object {
            fun from(@IdRes id: Int) = values().first { it.id == id }
        }
    }

    enum class GeofenceMenuItem(@IdRes val id: Int) {
        DELETE(R.id.delete);

        companion object {
            fun from(@IdRes id: Int) = values().first { it.id == id }
        }
    }

    interface View {

        fun showGeofences(geofences: List<Geofence>)

        fun showNoGeofences()

        fun showGeofence(geofence: Geofence)

        fun hideGeofence(geofence: Geofence)

        fun showDeleteConfirmDialog(targetId: Long)

        fun showGeofenceErrorToast()

        // TODO: showProgressDialog
    }

    interface Navigator {

        fun navigateSettings()

        fun navigateAddGeofence()

        fun navigateUpdateGeofence(id: Long)
    }

    interface Presenter {

        fun onCreate()

        fun subscribe()

        fun unsubscribe()

        fun onMenuItemSelected(navItem: NavItem): Boolean

        fun onGeofenceAddButtonClicked()

        fun onGeofenceSelected(geofence: Geofence)

        fun onGeofenceMenuItemSelected(menuItem: GeofenceMenuItem, targetId: Long)

        fun onDeleteConfirmDialogOkClicked(targetId: Long)

        // Navigator

        fun onAddGeofenceResult(newGeofence: Geofence)
    }

    interface LoadTask {

        fun loadGeofences(): Single<List<Geofence>>
    }

    interface DeleteTask {

        fun deleteGeofence(targetId: Long): Single<Geofence>
    }

    sealed class EditGeofenceEvent(val geofence: Geofence) {
        class OnAdd(geofence: Geofence) : EditGeofenceEvent(geofence)
        class OnUpdate(geofence: Geofence) : EditGeofenceEvent(geofence)
    }
}