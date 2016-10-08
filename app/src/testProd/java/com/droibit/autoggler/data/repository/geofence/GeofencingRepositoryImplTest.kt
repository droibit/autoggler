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
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.ERROR)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val registered = repository.register(mock())
        assertThat(registered).isFalse()

        verify(googleApiProvider, never()).geofencingApi
    }

    @Test
    fun register_addGeofenceSuccessful() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { addGeofences(any(), any<GeofencingRequest>(), anyOrNull()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        val registered = repository.register(geofence)
        assertThat(registered).isTrue()
    }

    @Test(expected = GeofencingException::class)
    fun register_addGeofenceFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.ERROR)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { addGeofences(any(), any<GeofencingRequest>(), anyOrNull()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        repository.register(geofence)
    }

    @Test
    fun unregister_connectGoogleApiFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.ERROR)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val unregistered = repository.unregister(mock())
        assertThat(unregistered).isFalse()

        verify(googleApiProvider, never()).geofencingApi
    }

    @Test
    fun unregister_removeGeofenceSuccessful() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { removeGeofences(any(), any<List<String>>()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        val registered = repository.unregister(geofence)
        assertThat(registered).isTrue()
    }

    @Test(expected = GeofencingException::class)
    fun unregister_removeGeofenceFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.ERROR)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { removeGeofences(any(), any<List<String>>()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        repository.unregister(geofence)
    }

    @Test
    fun createGeofencingRequest_shouldCreateFromGeofence() {
        val expectGeofence = Geofence(id = 1234, circle = Circle(1.0, 2.0, 3.0))
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
    fun update_connectGoogleApiFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.ERROR)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val registered = repository.update(mock())
        assertThat(registered).isFalse()

        verify(googleApiProvider, never()).geofencingApi
    }

    @Test
    fun update_successful() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { removeGeofences(any(), any<List<String>>()) } doReturn mockPendingResult
            on { addGeofences(any(), any<GeofencingRequest>(), anyOrNull()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        val registered = repository.update(geofence)
        assertThat(registered).isTrue()
    }

    @Test(expected = GeofencingException::class)
    fun update_removeGeofenceFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.ERROR)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { removeGeofences(any(), any<List<String>>()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        repository.unregister(geofence)
    }

    @Test(expected = GeofencingException::class)
    fun update_addGeofenceFailed() {
        val mockClient: GoogleApiClient = mock() {
            on { blockingConnect(any(), any()) } doReturn ConnectionResult(CommonStatusCodes.SUCCESS)
        }
        whenever(googleApiProvider.newClient()).thenReturn(mockClient)

        val mockPendingResult: PendingResult<Status> = mock() {
            on { await() } doReturn Status(CommonStatusCodes.ERROR)
        }
        val geofencingApi: GeofencingApi = mock() {
            on { addGeofences(any(), any<GeofencingRequest>(), anyOrNull()) } doReturn mockPendingResult
        }
        whenever(googleApiProvider.geofencingApi).thenReturn(geofencingApi)

        val geofence = Geofence(id = 1, circle = Circle(1.0, 2.0, 3.0))
        repository.register(geofence)
    }
}