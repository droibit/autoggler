package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber


class DeleteTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var geofenceRepository: GeofenceRepository

    lateinit var task: DeleteTask

    @Before
    fun setUp() {
        task = DeleteTask(geofenceRepository)
    }

    @Test
    fun deleteGeofence() {
        // TODO: unregisterGeofencing

        run {
            val id = 1L
            val expect: Geofence = mock()
            doReturn(expect).whenever(geofenceRepository).deleteGeofence(id)

            val subscriber = TestSubscriber<Geofence>()
            task.deleteGeofence(targetId = id)
                .subscribe(subscriber)

            subscriber.run {
                assertNoErrors()
                assertValueCount(1)
                assertValue(expect)
                assertCompleted()
            }
        }

        // Not exist
        run {
            val id = -1L
            val throwable = IllegalArgumentException("")
            doThrow(throwable).whenever(geofenceRepository).deleteGeofence(id)

            val subscriber = TestSubscriber<Geofence>()
            task.deleteGeofence(targetId = id)
                    .subscribe(subscriber)

            subscriber.run {
                assertError(throwable)
                assertNoValues()
                assertNotCompleted()
            }
        }
    }
}