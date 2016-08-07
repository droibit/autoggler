package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.droibit.autoggler.test
import com.nhaarman.mockito_kotlin.*
import io.realm.exceptions.RealmError
import io.realm.exceptions.RealmException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.Single
import rx.subscriptions.CompositeSubscription

class GeofencesPresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var view: GeofencesContract.View

    @Mock
    lateinit var navigator: GeofencesContract.Navigator

    @Mock
    lateinit var loadTask: GeofencesContract.LoadTask

    lateinit var subscriptions: CompositeSubscription

    lateinit var presenter: GeofencesPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = GeofencesPresenter(view, navigator, loadTask, subscriptions)
    }

    //@Test
    fun subscribe() {
    }

    @Test
    fun unsubscribe() {
        presenter.unsubscribe()

        assertThat(subscriptions.hasSubscriptions()).isFalse()
    }

    @Test
    fun onMenuItemSelected_navToSettings() {
        val handled = presenter.onMenuItemSelected(NavItem.SETTINGS)

        verify(navigator).navigateSettings()
        assertThat(handled).isTrue()
    }

    @Test
    fun onGeofenceAddButtonClicked_navToAddGeofence() {
        presenter.onGeofenceAddButtonClicked()

        verify(navigator).navigateAddGeofence()
    }

    @Test
    fun onGeofenceSelected_navToUpdateGeofence() {
        val geofence: Geofence = mock()
        presenter.onGeofenceSelected(geofence)

        verify(navigator).navigateUpdateGeofence(geofence.id)
    }

    @Test
    fun loadGeofences_showGeofences() {
        // Returned geofences
        test {
            val mockList: List<Geofence> = mock()
            doReturn(false).whenever(mockList).isEmpty()
            doReturn(Single.just(mockList)).whenever(loadTask).loadGeofences()

            val subscription = presenter.loadGeofences()
            assertThat(subscription).isNotNull()

            verify(view).showGeofences(mockList)
            verify(view, never()).showNoGeofences()
        }

        reset(view)

        // Returned empty geofences
        test {
            val mockList: List<Geofence> = mock()
            doReturn(true).whenever(mockList).isEmpty()
            doReturn(Single.just(mockList)).whenever(loadTask).loadGeofences()

            val subscription = presenter.loadGeofences()
            assertThat(subscription).isNotNull()

            verify(view).showNoGeofences()
            verify(view, never()).showGeofences(anyList())
        }

        reset(view)

        // error occur
        test {
            val error: Single<Geofence> = Single.error(RealmException(""))
            doReturn(error).whenever(loadTask).loadGeofences()

            val subscription = presenter.loadGeofences()
            assertThat(subscription).isNotNull()

            verify(view).showNoGeofences()
            verify(view, never()).showGeofences(anyList())
        }
    }
}