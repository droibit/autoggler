package com.droibit.autoggler.data.repository.source.api

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.GeofencingApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsApi

class GoogleApiProviderImpl(private val context: Context) : GoogleApiProvider {

    override val locationSettingsApi = LocationServices.SettingsApi

    override val fusedLocationProviderApi = LocationServices.FusedLocationApi

    override val geofencingApi = LocationServices.GeofencingApi

    override fun newLocationClient(): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build()
    }

    override fun newSyncLocationHolder(): SyncLocationHolder {
        return SyncLocationHolder()
    }
}