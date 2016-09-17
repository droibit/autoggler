package com.droibit.autoggler.data.repository.geofence

import android.content.Intent
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.repository.source.api.GoogleApiProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.GeofencingApi
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.internal.ParcelableGeofence
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class GeofencingRepositoryImplTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    lateinit var config: ApplicationConfig

    @Mock
    lateinit var googleApiProvider: GoogleApiProvider

    @Mock
    lateinit var intentCreator: (Long) -> Intent

    private lateinit var repository: GeofencingRepositoryImpl

    @Before
    fun setUp() {
        repository = GeofencingRepositoryImpl(
                config,
                googleApiProvider,
                intentCreator
        )
    }

    @Test
    fun register_connectGoogleApiFailed() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.ERROR)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val registered = repository.register(mock())

        assertThat(registered).isFalse()
        verify(googleApiProvider, never()).geofencingApi
    }

    @Test
    fun register_addGeofenceSuccessful() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockStatus = Status(CommonStatusCodes.SUCCESS)
        val mockPendingResult: PendingResult<Status> = mock()
        whenever(mockPendingResult.await()).thenReturn(mockStatus)

        val geofencingApi: GeofencingApi = mock()
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)
        whenever(geofencingApi.addGeofences(any(), any<GeofencingRequest>(), any()))
                .thenReturn(mockPendingResult)

        val geofence = Geofence(
                id = 1, circle = Circle(1.0, 2.0, 3.0)
        )
        val registered = repository.register(geofence)
        assertThat(registered).isTrue()
    }

    @Test
    fun register_addGeofenceError() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockStatus = Status(CommonStatusCodes.ERROR)
        val mockPendingResult: PendingResult<Status> = mock()
        whenever(mockPendingResult.await()).thenReturn(mockStatus)

        val geofencingApi: GeofencingApi = mock()
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)
        whenever(geofencingApi.addGeofences(any(), any<GeofencingRequest>(), any()))
                .thenReturn(mockPendingResult)

        val geofence = Geofence(
                id = 1, circle = Circle(1.0, 2.0, 3.0)
        )
        val registered = repository.register(geofence)
        assertThat(registered).isFalse()
    }

    @Test
    fun unregister_connectGoogleApiFailed() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.ERROR)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val unregistered = repository.unregister(mock())

        assertThat(unregistered).isFalse()
        verify(googleApiProvider, never()).geofencingApi
    }

    @Test
    fun unregister_removeGeofenceSuccessful() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockStatus = Status(CommonStatusCodes.SUCCESS)
        val mockPendingResult: PendingResult<Status> = mock()
        whenever(mockPendingResult.await()).thenReturn(mockStatus)

        val geofencingApi: GeofencingApi = mock()
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)
        whenever(geofencingApi.removeGeofences(any(), anyList())).thenReturn(mockPendingResult)

        val geofence = Geofence(
                id = 1, circle = Circle(1.0, 2.0, 3.0)
        )
        val registered = repository.unregister(geofence)
        assertThat(registered).isTrue()
    }

    @Test
    fun unregister_removeGeofenceError() {
        val mockClient: GoogleApiClient = mock()
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val connectionResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(mockClient.blockingConnect(any(), any())).thenReturn(connectionResult)

        val mockStatus = Status(CommonStatusCodes.ERROR)
        val mockPendingResult: PendingResult<Status> = mock()
        whenever(mockPendingResult.await()).thenReturn(mockStatus)

        val geofencingApi: GeofencingApi = mock()
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)
        whenever(geofencingApi.removeGeofences(any(), anyList())).thenReturn(mockPendingResult)

        val geofence = Geofence(
                id = 1, circle = Circle(1.0, 2.0, 3.0)
        )
        val registered = repository.unregister(geofence)
        assertThat(registered).isFalse()
    }

    @Test
    fun createGeofencingRequest_shouldCreateFromGeofence() {
        val expectGeofence = Geofence(
                id = 1234,
                circle = Circle(1.0, 2.0, 3.0)
        )
        val request = repository.createGeofencingRequest(expectGeofence)
        assertThat(request.geofences).hasSize(1)

        val actualGeofence = request.geofences.first()
        assertThat(actualGeofence.requestId).isEqualTo("${expectGeofence.id}")

        (actualGeofence as ParcelableGeofence).run {
            assertThat(latitude).isEqualTo(expectGeofence.circle.lat)
            assertThat(longitude).isEqualTo(expectGeofence.circle.lng)
        }
    }

    @Test
    fun update() {
        TODO()
    }
}