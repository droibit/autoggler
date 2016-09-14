package com.droibit.autoggler.data.repository.location

import android.location.Location
import android.os.Looper
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.droibit.autoggler.data.repository.source.api.GoogleApiProvider
import com.droibit.autoggler.data.repository.source.api.SyncLocationHolder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

class LocationRepositoryImplTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    private val looper: Looper? = null

    @Mock
    lateinit var config: ApplicationConfig

    @Mock
    lateinit var timeProvider: TimeProvider

    @Mock
    lateinit var googleApiProvider: GoogleApiProvider

    lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        repository = LocationRepositoryImpl(
                looper,
                config,
                timeProvider,
                googleApiProvider
        )
    }

    @Test
    fun getCurrentLocation_connectGoogleApiFailed() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.ERROR)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val location = repository.getCurrentLocation(
                config.maxLastLocationElapsedTimeMillis,
                config.currentLocationTimeoutMillis
        )

        assertThat(location).isNull()
        verify(googleApiProvider, never()).fusedLocationProviderApi
    }

    @Test
    fun getCurrentLocation_shouldUseLastLocation() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockFusedLocationProviderApi: FusedLocationProviderApi = mock()
        whenever(googleApiProvider.fusedLocationProviderApi).thenReturn(mockFusedLocationProviderApi)
        whenever(googleApiProvider.newSyncLocationHolder()).thenReturn(SyncLocationHolder())

        whenever(timeProvider.elapsedRealTimeNanos).thenReturn(5L.toNanos())

        whenever(config.maxLastLocationElapsedTimeMillis).thenReturn(2L)
        whenever(config.currentLocationTimeoutMillis).thenReturn(1L)

        // elapsedTimeNanos == maxLastLocationElapsedTimeNanos
        run {
            val elapsedRealTimeNanos = 3L.toNanos()
            val mockLastLocation: Location = mock()
            whenever(mockLastLocation.elapsedRealtimeNanos).thenReturn(elapsedRealTimeNanos)

            whenever(mockFusedLocationProviderApi.getLastLocation(mockClient)).thenReturn(mockLastLocation)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )
            assertThat(location).isSameAs(mockLastLocation)
        }

        // elapsedTimeNanos < maxLastLocationElapsedTimeNanos
        run {
            val elapsedRealTimeNanos = 4L.toNanos()
            val mockLastLocation: Location = mock()
            whenever(mockLastLocation.elapsedRealtimeNanos).thenReturn(elapsedRealTimeNanos)

            whenever(mockFusedLocationProviderApi.getLastLocation(mockClient)).thenReturn(mockLastLocation)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )
            assertThat(location).isSameAs(mockLastLocation)
        }

        // elapsedTimeNanos > maxLastLocationElapsedTimeNanos
        run {
            val elapsedRealTimeNanos = 2L.toNanos()
            val mockLastLocation: Location = mock()
            whenever(mockLastLocation.elapsedRealtimeNanos).thenReturn(elapsedRealTimeNanos)

            whenever(mockFusedLocationProviderApi.getLastLocation(mockClient)).thenReturn(mockLastLocation)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )
            assertThat(location).isNull()
        }

        // getLastLocation == null
        run {
            whenever(mockFusedLocationProviderApi.getLastLocation(mockClient)).thenReturn(null)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )
            assertThat(location).isNull()
        }
    }

    @Test
    fun getCurrentLocation_requestCurrentLocation() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockFusedLocationProviderApi: FusedLocationProviderApi = mock()
        whenever(googleApiProvider.fusedLocationProviderApi).thenReturn(mockFusedLocationProviderApi)
        whenever(googleApiProvider.newSyncLocationHolder()).thenReturn(SyncLocationHolder())

        whenever(config.currentLocationTimeoutMillis).thenReturn(1L)

        // Success
        run {
            val mockLocation: Location = mock()
            val locationHolder = SyncLocationHolder().apply {
                value = mockLocation
            }
            whenever(googleApiProvider.newSyncLocationHolder()).thenReturn(locationHolder)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )

            assertThat(location).isSameAs(mockLocation)
            verify(googleApiProvider.fusedLocationProviderApi).removeLocationUpdates(mockClient, locationHolder)
        }

        reset(googleApiProvider.fusedLocationProviderApi)

        // Timeout
        run {
            val locationHolder = SyncLocationHolder()
            whenever(googleApiProvider.newSyncLocationHolder()).thenReturn(locationHolder)

            val location = repository.getCurrentLocation(
                    config.maxLastLocationElapsedTimeMillis,
                    config.currentLocationTimeoutMillis
            )

            assertThat(location).isNull()
            verify(googleApiProvider.fusedLocationProviderApi).removeLocationUpdates(mockClient, locationHolder)
        }
    }

    private fun Long.toNanos(): Long {
        return TimeUnit.MILLISECONDS.toNanos(this)
    }
}