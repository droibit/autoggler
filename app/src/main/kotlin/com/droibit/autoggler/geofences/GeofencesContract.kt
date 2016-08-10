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

    enum class GeofenceMenuItem {
        DELETE
    }

    interface View {

        fun showGeofences(geofences: List<Geofence>)

        fun showNoGeofences()

        fun hideGeofence(geofence: Geofence): Int

        fun showDeleteConfirmDialog(targetId: Long)

        // TODO: showProgressDialog
    }

    interface Navigator {

        fun navigateSettings()

        fun navigateAddGeofence()

        fun navigateUpdateGeofence(id: Long)
    }

    interface Presenter {

        fun subscribe()

        fun unsubscribe()

        fun onMenuItemSelected(navItem: NavItem): Boolean

        fun onGeofenceAddButtonClicked()

        fun onGeofenceSelected(geofence: Geofence)

        fun onGeofenceMenuItemSelected(menuItem: GeofenceMenuItem)

        fun onDeleteConfirmDialogOkClicked(targetId: Long)
    }

    interface LoadTask {

        fun loadGeofences(): Single<List<Geofence>>
    }

    interface DeleteTask {

        fun deleteGeofence(targetId: Long): Single<Geofence?>
    }
}