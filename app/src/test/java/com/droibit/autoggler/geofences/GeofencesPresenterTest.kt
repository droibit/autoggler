package com.droibit.autoggler.geofences

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.geofences.GeofencesContract.GeofenceMenuItem
import com.droibit.autoggler.geofences.GeofencesContract.NavItem
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.*
import io.realm.exceptions.RealmException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

    @Mock
    lateinit var deleteTask: GeofencesContract.DeleteTask

    lateinit var subscriptions: CompositeSubscription

    lateinit var presenter: GeofencesPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = GeofencesPresenter(view, navigator, loadTask, deleteTask, subscriptions)
    }

    @Test
    fun onCreate_loadGeofence() {
        // Returned geofences
        run {
            val mockList: List<Geofence> = mock() {
                on { isEmpty() } doReturn false
            }
            whenever(loadTask.loadGeofences()).thenReturn(Single.just(mockList))

            presenter.onCreate()

            verify(view).showGeofences(mockList)
            verify(view, never()).showNoGeofences()
        }

        reset(view)

        // Returned empty geofences
        run {
            val mockList: List<Geofence> = mock() {
                on { isEmpty() } doReturn true
            }
            whenever(loadTask.loadGeofences()).thenReturn(Single.just(mockList))

            presenter.onCreate()

            verify(view).showNoGeofences()
            verify(view, never()).showGeofences(any())
        }

        reset(view)

        // error occur
        run {
            val error = Single.error<List<Geofence>>(RealmException(""))
            whenever(loadTask.loadGeofences()).thenReturn(error)

            presenter.onCreate()

            verify(view).showNoGeofences()
            verify(view, never()).showGeofences(any())
        }
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

        verify(navigator).navigateUpdateGeofence(geofence)
    }

    @Test
    fun onGeofenceMenuItemSelected_showDeleteConfirmDialog() {
        val expectId = 1L
        presenter.onGeofenceMenuItemSelected(GeofenceMenuItem.DELETE, targetId = expectId)

        verify(view).showDeleteConfirmDialog(expectId)
    }

    @Test
    fun onDeleteConfirmDialogOkClicked_deleteGeofence() {

        run {
            val expectId = 1L
            val expectGeofence: Geofence = mock()
            val single = Single.just(expectGeofence)
            whenever(deleteTask.deleteGeofence(targetId = expectId)).thenReturn(single)

            presenter.onDeleteConfirmDialogOkClicked(targetId = expectId)

            verify(view).hideGeofence(expectGeofence)
            verify(view, never()).showGeofenceErrorToast()
        }

        reset(view)

        run {
            val expectId = 1L
            val error = Single.error<Geofence>(IllegalArgumentException(""))
            whenever(deleteTask.deleteGeofence(targetId = expectId)).thenReturn(error)

            presenter.onDeleteConfirmDialogOkClicked(targetId = expectId)

            verify(view).showGeofenceErrorToast()
            verify(view, never()).hideGeofence(any())
        }
    }

    @Test
    fun onAddGeofenceResult_showGeofence() {
        val geofence: Geofence = mock()
        presenter.onAddGeofenceResult(geofence)

        verify(view).showGeofence(geofence)
    }

    @Test
    fun onUpdateGeofenceResult_updateGeofence() {
        val geofence: Geofence = mock()
        presenter.onUpdateGeofenceResult(geofence)

        verify(view).updateGeofence(geofence)
    }
}