package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofencingRepository
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.droibit.autoggler.data.repository.location.UnavailableLocationException.ErrorStatus
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber

class RegisterGeofencingTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var geofencingRepository: GeofencingRepository

    @Mock
    lateinit var permissionChecker: RuntimePermissionChecker

    lateinit var task: RegisterGeofencingTask

    @Before
    fun setUp() {
        task = RegisterGeofencingTask(
                geofencingRepository,
                permissionChecker
        )
    }

    @Test
    fun register_success() {
        whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

        val success = true
        whenever(geofencingRepository.register(any())).thenReturn(success)

        val testSubscriber = TestSubscriber.create<Boolean>()
        task.register(mock()).subscribe(testSubscriber)

        testSubscriber.run {
            assertNoErrors()
            assertCompleted()
            assertValueCount(1)
            assertValue(success)
        }
    }

    @Test
    fun register_failed() {
        whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

        val failed = false
        whenever(geofencingRepository.register(any())).thenReturn(failed)

        val testSubscriber = TestSubscriber.create<Boolean>()
        task.register(mock()).subscribe(testSubscriber)

        testSubscriber.run {
            assertNoErrors()
            assertCompleted()
            assertValueCount(1)
            assertValue(failed)
        }
    }

    @Test
    fun register_hasBeenDeniedPermission() {
        whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(false)

        val testSubscriber = TestSubscriber.create<Boolean>()
        task.register(mock()).subscribe(testSubscriber)

        testSubscriber.run {
            assertError(UnavailableLocationException::class.java)

            val exception = onErrorEvents.first() as UnavailableLocationException
            assertThat(exception.status).isEqualTo(ErrorStatus.PERMISSION_DENIED)
            assertThat(exception.option).isNull()
        }

        verify(geofencingRepository, never()).register(any())
    }
}