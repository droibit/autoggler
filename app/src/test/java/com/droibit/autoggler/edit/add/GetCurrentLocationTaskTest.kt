package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.repository.location.LocationAvailableStatus
import com.droibit.autoggler.data.repository.location.LocationRepository
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.GetCurrentLocationEvent as Event
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException.ErrorStatus.*
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.jakewharton.rxrelay.BehaviorRelay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber

class GetCurrentLocationTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    lateinit var relay: BehaviorRelay<Event>

    @Mock
    lateinit var locationRepository: LocationRepository

    @Mock
    lateinit var permissionChecker: RuntimePermissionChecker

    @Mock
    lateinit var config: ApplicationConfig

    lateinit var task: GetCurrentLocationTask

    @Before
    fun setUp() {
        relay = BehaviorRelay.create()
        task = GetCurrentLocationTask(
                relay,
                locationRepository,
                permissionChecker,
                config
        )
    }

    @Test
    fun asObservable_subscribeAfterCall() {
        val onSuccessEvent = Event.OnSuccess(mock())

        val testSubscriber = TestSubscriber.create<Event>()
        task.asObservable().subscribe(testSubscriber)

        relay.call(onSuccessEvent)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValue(onSuccessEvent)
    }

    @Test
    fun asObservable_subscribeBeforeCall() {
        val onSuccessEvent = Event.OnSuccess(mock())
        relay.call(onSuccessEvent)

        val testSubscriber = TestSubscriber.create<Event>()
        task.asObservable().subscribe(testSubscriber)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValue(onSuccessEvent)
    }

    @Test
    fun asObservable_ignoreSameEvent() {
        val onSuccessEvent = Event.OnSuccess(mock())
        val onErrorEvent = Event.OnError(AddGeofenceContract.UnavailableLocationException(ERROR))

        // First time
        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            relay.call(onSuccessEvent)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValue(onSuccessEvent)
        }

        // Second time (Same event)
        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertNoValues()
        }

        // Third time(Different event)
        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            relay.call(onErrorEvent)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValue(onErrorEvent)
        }
    }

    @Test
    fun asObservable_resubscribe() {
        val onSuccessEvent = Event.OnSuccess(mock())

        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.unsubscribe()

            relay.call(onSuccessEvent)

            testSubscriber.assertNoValues()
            testSubscriber.assertUnsubscribed()
        }

        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValue(onSuccessEvent)
        }

        run {
            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertNoValues()
        }
    }

    @Test
    fun getLocation_onSuccess() {
        whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

        val status = Status(CommonStatusCodes.SUCCESS)
        val availableStatus = LocationAvailableStatus(status)
        whenever(locationRepository.getLocationAvailableStatus()).thenReturn(availableStatus)

        val mockLocation: Location = mock()
        whenever(locationRepository.getCurrentLocation(any(), any())).thenReturn(mockLocation)

        task.requestLocation()

        val testSubscriber = TestSubscriber.create<Event>()
        task.asObservable().subscribe(testSubscriber)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val event = testSubscriber.onNextEvents.first()
        assertThat(event).isExactlyInstanceOf(Event.OnSuccess::class.java)

        val onSuccessEvent = event as Event.OnSuccess
        assertThat(onSuccessEvent.location).isSameAs(mockLocation)
    }

    @Test
    fun getLocation_onError() {
        // Permission denied
        run {
            whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(false)
            task.requestLocation()

            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValueCount(1)

            val event = testSubscriber.onNextEvents.first()
            assertThat(event).isExactlyInstanceOf(Event.OnError::class.java)

            val onErrorEvent = event as Event.OnError
            assertThat(onErrorEvent.exception.status).isEqualTo(PERMISSION_DENIED)
            assertThat(onErrorEvent.exception.option).isNull()

            testSubscriber.unsubscribe()
        }

        // Location unavailable
        run {
            whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

            val status = Status(CommonStatusCodes.RESOLUTION_REQUIRED)
            val availableStatus = LocationAvailableStatus(status)
            whenever(locationRepository.getLocationAvailableStatus()).thenReturn(availableStatus)

            task.requestLocation()

            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValueCount(1)

            val event = testSubscriber.onNextEvents.first()
            assertThat(event).isExactlyInstanceOf(Event.OnError::class.java)

            val onErrorEvent = event as Event.OnError
            assertThat(onErrorEvent.exception.status).isEqualTo(RESOLUTION_REQUIRED)
            assertThat(onErrorEvent.exception.option).isSameAs(availableStatus)

            testSubscriber.unsubscribe()
        }

        // Unknown error
        run {
            whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

            val status = Status(CommonStatusCodes.ERROR)
            val availableStatus = LocationAvailableStatus(status)
            whenever(locationRepository.getLocationAvailableStatus()).thenReturn(availableStatus)

            task.requestLocation()

            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValueCount(1)

            val event = testSubscriber.onNextEvents.first()
            assertThat(event).isExactlyInstanceOf(Event.OnError::class.java)

            val onErrorEvent = event as Event.OnError
            assertThat(onErrorEvent.exception.status).isEqualTo(ERROR)
            assertThat(onErrorEvent.exception.option).isNull()

            testSubscriber.unsubscribe()
        }
    }

    @Test
    fun getLocation_onErrorAndSuccess() {
        whenever(permissionChecker.isPermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)

        // Location unavailable
        run {
            val status = Status(CommonStatusCodes.RESOLUTION_REQUIRED)
            val availableStatus = LocationAvailableStatus(status)
            whenever(locationRepository.getLocationAvailableStatus()).thenReturn(availableStatus)

            task.requestLocation()

            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValueCount(1)

            val event = testSubscriber.onNextEvents.first()
            assertThat(event).isExactlyInstanceOf(Event.OnError::class.java)

            val onErrorEvent = event as Event.OnError
            assertThat(onErrorEvent.exception.status).isEqualTo(RESOLUTION_REQUIRED)
            assertThat(onErrorEvent.exception.option).isSameAs(availableStatus)

            testSubscriber.unsubscribe()
        }

        // getLocation
        run {
            val status = Status(CommonStatusCodes.SUCCESS)
            val availableStatus = LocationAvailableStatus(status)
            whenever(locationRepository.getLocationAvailableStatus()).thenReturn(availableStatus)

            val mockLocation: Location = mock()
            whenever(locationRepository.getCurrentLocation(any(), any())).thenReturn(mockLocation)

            task.requestLocation()

            val testSubscriber = TestSubscriber.create<Event>()
            task.asObservable().subscribe(testSubscriber)

            testSubscriber.assertNoTerminalEvent()
            testSubscriber.assertValueCount(1)

            val event = testSubscriber.onNextEvents.first()
            assertThat(event).isExactlyInstanceOf(Event.OnSuccess::class.java)

            val onSuccessEvent = event as Event.OnSuccess
            assertThat(onSuccessEvent.location).isSameAs(mockLocation)
        }
    }
}