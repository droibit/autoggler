package com.droibit.autoggler.edit.update

import android.os.Bundle
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.GeofencingException
import com.droibit.autoggler.data.repository.geofence.GeofencingException.ErrorStatus.PERMISSION_DENIED
import com.droibit.autoggler.data.repository.geofence.Toggle
import com.droibit.autoggler.edit.update.UpdateGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.google.android.gms.maps.model.LatLng
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

    @Mock
    lateinit var editableGeofence: Geofence

    lateinit var subscriptions: CompositeSubscription

    lateinit var presenter: UpdateGeofencePresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
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

        val location = LatLng(1.0, 2.0)
        whenever(editableGeofence.latLong).thenReturn(location)

        run {
            presenter.onMapReady(isRestoredGeometory = true)
            verify(view).setLocation(location)
            verify(view).showUneditableGeofences(mockGeofences)
        }
        reset(view)

        run {
            presenter.onMapReady(isRestoredGeometory = false)
            verify(view).setLocation(location)
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
    fun onMarkerDragStart_hideEditableGeofenceCircle() {
        presenter.onMarkerDragStart(marker = Marker(mock()))

        verify(view).hideEditableGeofenceCircle()
        verify(view, never()).showEditableGeofenceCircle()
    }

    @Test
    fun onMarkerDragEnd_showGeofenceCircle() {
        presenter.onMarkerDragEnd()

        verify(view).showEditableGeofenceCircle()
        verify(view, never()).hideEditableGeofenceCircle()
    }

    @Test
    fun onPrepareDragMode_hideDoneButton() {
        val marker = Marker(mock())
        presenter.onPrepareDragMode(marker)

        verify(view).hideDoneButton()
        verify(view, never()).showDoneButton()
    }

    @Test
    fun onFinishedDragMode_showDoneButton() {
        val mockInternal: zzf = mock() {
            on { position } doReturn LatLng(1.0, 2.0)
        }
        doNothing().whenever(editableGeofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onFinishedDragMode(marker)

        verify(view).showDoneButton()
        verify(view, never()).hideDoneButton()
    }

    @Test
    fun onFinishedDragMode_updateGeofenceLocation() {
        val expectLatLng = LatLng(1.0, 2.0)
        val mockInternal: zzf = mock() {
            on { position } doReturn expectLatLng
        }
        doNothing().whenever(editableGeofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onFinishedDragMode(marker)

        verify(editableGeofence).latlng(expectLatLng)
    }

    @Test
    fun onFinishedDragMode_shouldMoveMarkerPosition() {
        val mockInternal: zzf = mock() {
            on { position } doReturn LatLng(1.0, 2.0)
        }
        doNothing().whenever(editableGeofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onFinishedDragMode(marker)

        verify(view).setLocation(marker.position)
    }

    @Test
    fun onGeofenceUpdated_shouldUpdateGeofence() {
        val updatedGeofence = Geofence().apply {
            name = "updated"
            circle = Circle(1.0, 2.0, 3.0)
            toggle = Toggle(true, false)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(editableGeofence).name = updatedGeofence.name
        verify(editableGeofence).circle = updatedGeofence.circle
        verify(editableGeofence).toggle = updatedGeofence.toggle
    }

    @Test
    fun onGeofenceUpdated_updateMarkerInfoWindow() {
        val updatedGeofence = Geofence().apply {
            name = "updated"
            circle = Circle(1.0, 2.0, 3.0)
            toggle = Toggle(true, false)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(view).setMarkerInfoWindow(title = updatedGeofence.name, snippet = null)
    }

    @Test
    fun onGeofenceUpdated_setGeofenceRadius() {
        val updatedGeofence = Geofence().apply {
            circle = Circle(1.0, 2.0, 3.0)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(view).setGeofenceRadius(updatedGeofence.radius)
    }

    @Test
    fun onDoneButtonClicked_setDoneButtonEnabled() {
        whenever(updateGeofencingTask.update(any())).thenReturn(Single.just(true))

        presenter.onDoneButtonClicked()
        verify(view).setDoneButtonEnabled(false)
    }

    @Test
    fun onDoneButtonClicked_updateGeofencing() {
        whenever(updateGeofencingTask.update(any())).thenReturn(Single.just(true))

        presenter.onDoneButtonClicked()
        verify(updateGeofencingTask).update(editableGeofence)
    }

    // Navigator

    @Test
    fun onUpNavigationButtonClicked_navigationToUp() {
        presenter.onUpNavigationButtonClicked()

        verify(navigator).navigateToUp()
    }

    // RuntimePermissions

    @Test
    fun onLocationPermissionsResult_granted() {
        whenever(updateGeofencingTask.update(any())).thenReturn(Single.just(true))

        presenter.onLocationPermissionsResult(GEOFENCING, true)
        verify(updateGeofencingTask).update(editableGeofence)
        verify(view, never()).setDoneButtonEnabled(any())
    }

    @Test
    fun onLocationPermissionsResult_denied() {
        presenter.onLocationPermissionsResult(GEOFENCING, false)
        verify(view).setDoneButtonEnabled(any())
        verify(updateGeofencingTask, never()).update(editableGeofence)
    }

    // Private

    @Test
    fun subscribeUpdateGeofencing_onUpdateGeofencingResult() {
        // successful
        run {
            whenever(updateGeofencingTask.update(any())).thenReturn(Single.just(true))
            presenter.subscribeUpdateGeofencing()

            verify(navigator).finish(editableGeofence)
        }

        // failed
        run {
            whenever(updateGeofencingTask.update(any())).thenReturn(Single.just(false))
            presenter.subscribeUpdateGeofencing()

            verify(view).setDoneButtonEnabled(true)
            verify(view).showErrorToast(R.string.update_geofence_failed_update_geofence)
        }
    }

    @Test
    fun subscribeUpdateGeofencing_onUpdateGeofencingError() {
        // PERMISSION_DENIED
        run {
            whenever(updateGeofencingTask.update(any()))
                    .thenReturn(Single.error(GeofencingException(PERMISSION_DENIED)))
            presenter.subscribeUpdateGeofencing()

            verify(permissions).requestLocationPermission(GEOFENCING)
        }
    }
}