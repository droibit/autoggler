package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber

class LoadTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var geofenceRepository: GeofenceRepository

    lateinit var task: LoadTask

    @Before
    fun setUp() {
        task = LoadTask(geofenceRepository)
    }

    @Test
    fun loadGeofences() {
        val expect: List<Geofence> = mock()
        whenever(geofenceRepository.loadGeofences()).thenReturn(expect)

        val subscriber = TestSubscriber<List<Geofence>>()
        task.loadGeofences()
                .subscribe(subscriber)

        subscriber.run {
            assertNoErrors()
            assertValueCount(1)
            assertValue(expect)
            assertCompleted()
        }
    }

}