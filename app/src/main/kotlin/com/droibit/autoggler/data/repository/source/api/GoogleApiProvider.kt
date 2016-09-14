package com.droibit.autoggler.data.repository.source.api

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.GeofencingApi
import com.google.android.gms.location.SettingsApi

interface GoogleApiProvider {

    val locationSettingsApi: SettingsApi

    val fusedLocationProviderApi: FusedLocationProviderApi

    val geofencingApi: GeofencingApi

    fun newClient(): GoogleApiClient

    fun newSyncLocationHolder(): SyncLocationHolder
}
