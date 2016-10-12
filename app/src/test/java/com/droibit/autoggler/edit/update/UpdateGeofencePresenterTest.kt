package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.internal.zzf
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.Single
import rx.subscriptions.CompositeSubscription


class UpdateGeofencePresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var view: UpdateGeofenceContract.View

    @Mock
    lateinit var navigator: UpdateGeofenceContract.Navigator

    @Mock
    lateinit var permissions: UpdateGeofenceContract.RuntimePermissions

    @Mock
    lateinit var loadTask: UpdateGeofenceContract.LoadTask

    @Mock
    lateinit var updateGeofencingTask: UpdateGeofenceContract.UpdateGeofencingTask

    lateinit var subscriptions: CompositeSubscription

    lateinit var editableGeofence: Geofence

    lateinit var presenter: UpdateGeofencePresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        editableGeofence = Geofence(id = 1L)
        presenter = UpdateGeofencePresenter(
                view,
                navigator,
                permissions,
                loadTask,
                updateGeofencingTask,
                subscriptions,
                editableGeofence
        )
    }

    @Test
    fun onSavedInstanceState_saveInstanceState() {
        val mockWrapper: () -> Bundle = mock()
        presenter.onSavedInstanceState(mockWrapper)

        verify(view).saveInstanceState(editableGeofence, mockWrapper)
    }

    @Test
    fun onMapReady_showEditableGeofence() {
        whenever(loadTask.loadGeofences(ignoreId = any())).thenReturn(Single.just(mock()))

        run {
            presenter.onMapReady(isRestoredGeometory = true)
            verify(view, never()).showEditableGeofence(editableGeofence)
        }
        reset(view)

        run {
            presenter.onMapReady(isRestoredGeometory = false)
            verify(view).showEditableGeofence(any())
        }
    }

    @Test
    fun onMapReady_loadGeofences() {
        val mockGeofences: List<Geofence> = mock()
        whenever(loadTask.loadGeofences(ignoreId = any())).thenReturn(Single.just(mockGeofences))

        run {
            presenter.onMapReady(isRestoredGeometory = true)
            verify(view).showUneditableGeofences(mockGeofences)
        }
        reset(view)

        run {
            presenter.onMapReady(isRestoredGeometory = false)
            verify(view).showUneditableGeofences(mockGeofences)
        }
    }

    @Test
    fun onMarkerClicked_showEditDialog() {
        // show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)
            whenever(view.isEditableMarker(any())).thenReturn(true)

            presenter.onMarkerClicked(Marker(mock()))

            verify(view).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)
            whenever(view.isEditableMarker(any())).thenReturn(true)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)
            whenever(view.isEditableMarker(any())).thenReturn(false)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)
            whenever(view.isEditableMarker(any())).thenReturn(false)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
    }

    @Test
    fun onMarkerClicked_showMarkerInfoWindow() {
        whenever(view.isDragActionModeShown()).thenReturn(false)
        whenever(view.isEditableMarker(any())).thenReturn(true)

        // show
        run {
            val mockInternal: zzf = mock()
            whenever(mockInternal.isInfoWindowShown).thenReturn(false)
            val marker = Marker(mock())

            presenter.onMarkerClicked(marker)
            verify(view).showMarkerInfoWindow(marker)
        }
        reset(view)

        // already show
        run {
            val mockInternal: zzf = mock()
            whenever(mockInternal.isInfoWindowShown).thenReturn(true)
            val marker = Marker(mockInternal)

            presenter.onMarkerClicked(marker)
            verify(view, never()).showMarkerInfoWindow(any())
        }
    }

    @Test
    fun onMarkerInfoWindowClicked_showEditDialog() {
        // show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)
            whenever(view.isEditableMarker(any())).thenReturn(true)

            presenter.onMarkerClicked(Marker(mock()))

            verify(view).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)
            whenever(view.isEditableMarker(any())).thenReturn(true)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)
            whenever(view.isEditableMarker(any())).thenReturn(false)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
        reset(view)

        // not show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)
            whenever(view.isEditableMarker(any())).thenReturn(false)

            presenter.onMarkerClicked(Marker(mock()))
            verify(view, never()).showEditDialog(editableGeofence)
        }
    }

    @Test
    fun onMarkerDragStart_isDragActionModeShown() {
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)

            presenter.onMarkerDragStart(Marker(mock()))
            verify(view).startMarkerDragMode()
        }
        reset(view)

        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)

            presenter.onMarkerDragStart(Marker(mock()))
            verify(view, never()).startMarkerDragMode()
        }
    }

    @Test
    fun onMarkerDragStart_hideInfoWindow() {
        // is not shown
        run {
            val mockInternal: zzf = mock() {
                on { isInfoWindowShown } doReturn true
            }
            val marker = Marker(mockInternal)
            presenter.onMarkerDragStart(marker)

            verify(view).hideMarkerInfoWindow(marker)
        }
        reset(view)

        // shown
        run {
            val mockInternal: zzf = mock() {
                on { isInfoWindowShown } doReturn false
            }
            val marker = Marker(mockInternal)
            presenter.onMarkerDragStart(marker)

            verify(view, never()).hideMarkerInfoWindow(marker)
        }
    }

    @Test
    fun onMarkerDragStart_hideGeofenceCircle() {
        presenter.onMarkerDragStart(marker = Marker(mock()))

        verify(view).hideGeofenceCircle()
        verify(view, never()).showGeofenceCircle()
    }
}