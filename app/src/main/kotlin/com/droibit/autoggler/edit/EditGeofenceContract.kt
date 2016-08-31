package com.droibit.autoggler.edit

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.droibit.autoggler.R


interface EditGeofenceContract {

    enum class ToggleItemRes(@DrawableRes val icon: Int, @StringRes val text: Int) {
        WIFI(
                icon = R.drawable.ic_toggle_wifi_enabled,
                text = R.string.edit_geofence_dialog_toggle_item_wifi
        ),
        VIBRATION(
                icon = R.drawable.ic_toggle_vibration_enabled,
                text = R.string.edit_geofence_dialog_toggle_item_vibration
        )
    }

    class ToggleItem(itemRes: ToggleItemRes, var enabled: Boolean) {
        val iconRes = itemRes.icon
        val textRes = itemRes.text
    }
}