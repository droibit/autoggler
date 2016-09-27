package com.droibit.autoggler.data.repository.geofence

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.droibit.autoggler.data.repository.source.TestRealmProvider
import com.droibit.autoggler.data.repository.source.db.AutoIncrementor
import com.droibit.autoggler.data.repository.source.db.GeofencePersistenceContract.COLUMN_ID
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.realm.Realm
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

/**
 * If the database can not be created to refer to the following link.
 * * [StackOverflow](http://stackoverflow.com/questions/14317793/android-instrumentationtestcase-getfilesdir-returns-null)
 */
@RunWith(AndroidJUnit4::class)
class GeofenceRepositoryImplTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun initRealm() {
            Realm.init(InstrumentationRegistry.getContext())
        }
    }

    private class ExpectGeofence(val circle: Circle, val toggle: Toggle) {
    }

    private lateinit var realmProvider: TestRealmProvider

    private lateinit var autoIncrementor: AutoIncrementor

    private lateinit var repository: GeofenceRepositoryImpl

    private lateinit var timeProvider: TimeProvider

    @Before
    fun setUp() {
        realmProvider = TestRealmProvider()
        autoIncrementor = AutoIncrementor(COLUMN_ID)
        timeProvider = mock()
        repository = GeofenceRepositoryImpl(realmProvider, autoIncrementor, timeProvider)
    }

    @After
    fun tearDown() {
        realmProvider.close()
    }

    @Test
    fun addGeofence() {
        val expect = ExpectGeofence(
                circle = Circle(1.0, 2.0, 3.0),
                toggle = Toggle(wifi = true, vibration = false)
        )
        val expectName = "test"
        val expectCreatedAt = 1L
        whenever(timeProvider.currentTimeMillis).thenReturn(expectCreatedAt)

        val actual = repository.addGeofence(expectName, expect.circle, expect.toggle)
        assertThat(actual.id).isEqualTo(1L)
        assertThat(actual.name).isEqualTo(expectName)
        assertThat(actual.circle).isEqualTo(expect.circle)
        assertThat(actual.toggle).isEqualTo(expect.toggle)
        assertThat(actual.createdAt).isEqualTo(expectCreatedAt)
    }

    @Test
    fun loadGeofence_success() {
        val newGeofence = ExpectGeofence(
                circle = Circle(1.0, 2.0, 3.0),
                toggle = Toggle(wifi = true, vibration = false)
        )
        whenever(timeProvider.currentTimeMillis).thenReturn(1L)

        val addedGeofence = repository.addGeofence("test", newGeofence.circle, newGeofence.toggle)
        assertThat(addedGeofence).isNotNull()

        val loadGeofence = repository.loadGeofence(addedGeofence.id)
        assertThat(loadGeofence).isEqualTo(addedGeofence)
    }

    @Test
    fun loadGeofence_notExist() {
        val loadGeofence = repository.loadGeofence(0)
        assertThat(loadGeofence).isNull()
    }

    @Test
    fun loadGeofences_success() {
        val expectGeofences = listOf(
                ExpectGeofence(
                        circle = Circle(1.0, 2.0, 3.0),
                        toggle = Toggle(true, false)
                ),
                ExpectGeofence(
                        circle = Circle(4.0, 5.0, 6.0),
                        toggle = Toggle(false, true)
                ),
                ExpectGeofence(
                        circle = Circle(7.0, 8.0, 9.0),
                        toggle = Toggle(true, false)
                )
        )

        expectGeofences.forEachIndexed { i, expect ->
            whenever(timeProvider.currentTimeMillis).thenReturn((i + 1).toLong())

            val actual = repository.addGeofence("test-$i", expect.circle, expect.toggle)
            assertThat(actual).isNotNull()
        }

        val actualGeofences = repository.loadGeofences()
        assertThat(actualGeofences.size).isEqualTo(expectGeofences.size)

        actualGeofences.forEachIndexed { i, actual ->
            assertThat(actual.id).isEqualTo((i + 1).toLong())
            assertThat(actual.name).isEqualTo("test-$i")
            assertThat(actual.circle).isEqualTo(expectGeofences[i].circle)
            assertThat(actual.toggle).isEqualTo(expectGeofences[i].toggle)
        }
    }

    @Test
    fun loadGeofences_notExist() {
        val geofences = repository.loadGeofences()
        assertThat(geofences).isNotNull()
        assertThat(geofences).isEmpty()
    }

    @Test
    fun deleteGeofence() {
        val newGeofence = ExpectGeofence(
                circle = Circle(1.0, 2.0, 3.0),
                toggle = Toggle(wifi = true, vibration = false)
        )
        whenever(timeProvider.currentTimeMillis).thenReturn(1L)

        val addedGeofence = repository.addGeofence("test", newGeofence.circle, newGeofence.toggle)
        assertThat(addedGeofence).isNotNull()

        val deletedGeofence = repository.deleteGeofence(addedGeofence.id)
        assertThat(deletedGeofence).isEqualTo(addedGeofence)

        val loadGeofence = repository.loadGeofence(addedGeofence.id)
        assertThat(loadGeofence).isNull()
    }
}