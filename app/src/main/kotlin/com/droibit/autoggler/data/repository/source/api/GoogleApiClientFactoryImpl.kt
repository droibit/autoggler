package com.droibit.autoggler.data.repository.source.api

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

class GoogleApiClientFactoryImpl(private val context: Context) : GoogleApiClientFactory {

    override fun newLocationClient(): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build()
    }
}