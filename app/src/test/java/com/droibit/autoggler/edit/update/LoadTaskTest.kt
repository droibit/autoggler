package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofenceRepository
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.LoadTask.Event as LoadTaskEvent

class LoadTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var repository: GeofenceRepository

    private lateinit var task: LoadTask

    @Before
    fun setUp() {
        task = LoadTask(geofenceRepository = repository)
    }

    @Test
    fun loadGeofences_onlyEditableGeofence() {
        val expectGeofence = Geofence(id = 1L)
        whenever(repository.loadGeofences()).thenReturn(listOf(expectGeofence))

        val id = 1L
        val subscriber = TestSubscriber<LoadTaskEvent>()
        task.loadGeofences(editableId = id).subscribe(subscriber)

        subscriber.run {
            assertNoErrors()
            assertValueCount(1)
            assertCompleted()

            val event = onNextEvents.first()
            assertThat(event.editableGeofence).isSameAs(expectGeofence)
            assertThat(event.uneditableGeofences).isEmpty()
        }
    }

    @Test
    fun loadGeofences_bothGeofences() {
        val editableGeofence = Geofence(id = 1L)
        val uneditableGeofences = arrayOf(
                Geofence(id = 2L),
                Geofence(id = 3L),
                Geofence(id = 4L)
        )
        whenever(repository.loadGeofences()).thenReturn(listOf(editableGeofence, *uneditableGeofences))

        val id = 1L
        val subscriber = TestSubscriber<LoadTaskEvent>()
        task.loadGeofences(editableId = id).subscribe(subscriber)

        subscriber.run {
            assertNoErrors()
            assertValueCount(1)
            assertCompleted()

            val event = onNextEvents.first()
            assertThat(event.editableGeofence).isSameAs(editableGeofence)
            assertThat(event.uneditableGeofences).hasSize(uneditableGeofences.size)
            assertThat(event.uneditableGeofences).containsExactly(*uneditableGeofences)
        }
    }
}