package com.droibit.autoggler.data.repository.source.api

import android.location.Location
import com.droibit.autoggler.data.repository.source.SyncValueHolder
import com.google.android.gms.location.LocationListener

class SyncLocationHolder(): SyncValueHolder<Location>(), LocationListener {

    override fun onLocationChanged(location: Location) {
        this.value = location
        this.release()
    }
}