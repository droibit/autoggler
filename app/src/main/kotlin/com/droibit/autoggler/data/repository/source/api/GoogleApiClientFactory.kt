package com.droibit.autoggler.data.repository.source.api

import com.google.android.gms.common.api.GoogleApiClient

interface GoogleApiClientFactory {

    fun newLocationClient(): GoogleApiClient
}
