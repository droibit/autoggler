package com.droibit.autoggler.edit.add

import android.location.Location
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Circle
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.data.repository.geofence.Toggle
import com.droibit.autoggler.data.repository.location.AvailableStatus
import com.droibit.autoggler.data.repository.location.UnavailableLocationException
import com.droibit.autoggler.data.repository.location.UnavailableLocationException.ErrorStatus.*
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.GetCurrentLocationEvent
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GEOFENCING
import com.droibit.autoggler.edit.add.AddGeofenceContract.RuntimePermissions.Usage.GET_LOCATION
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
import rx.lang.kotlin.toSingletonObservable
import rx.subscriptions.CompositeSubscription

class AddGeofencePresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    lateinit var view: AddGeofenceContract.View

    @Mock
    lateinit var permissions: AddGeofenceContract.RuntimePermissions

    @Mock
    lateinit var navigator: AddGeofenceContract.Navigator

    @Mock
    lateinit var getCurrentLocationTask: AddGeofenceContract.GetCurrentLocationTask

    @Mock
    lateinit var registerGeofencingTask: AddGeofenceContract.RegisterGeofencingTask

    @Mock
    lateinit var geofence: Geofence

    lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: AddGeofencePresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = AddGeofencePresenter(
                view,
                permissions,
                navigator,
                getCurrentLocationTask,
                registerGeofencingTask,
                subscriptions,
                geofence
        )
    }

    @Test
    fun onCreate_requestLocation() {
        presenter.onCreate()

        verify(getCurrentLocationTask).requestLocation()
    }

    @Test
    fun subscribe_onGotLocation() {
        val mockLocation: Location = mock()
        val locationObservable = GetCurrentLocationEvent.OnSuccess(mockLocation)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(view).setLocation(mockLocation)
        verify(view, never()).showErrorToast(0)
    }

    @Test
    fun subscribe_onGotNullableLocation() {
        val locationObservable = GetCurrentLocationEvent.OnSuccess(null)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(view).showErrorToast(R.string.add_geofence_get_current_location_failed)
        verify(view, never()).setLocation(any<LatLng>())
    }

    @Test
    fun subscribe_onPermissionDenied() {
        val throwable = UnavailableLocationException(PERMISSION_DENIED)
        val locationObservable = GetCurrentLocationEvent.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(permissions).requestLocationPermission(usage = GET_LOCATION)
    }

    @Test
    fun subscribe_onResolutionRequired() {
        val mockStatus: AvailableStatus = mock()
        whenever(mockStatus.isAvailable).thenReturn(false)
        whenever(mockStatus.isResolutionRequired).thenReturn(true)

        val throwable = UnavailableLocationException(RESOLUTION_REQUIRED, option = mockStatus)
        val locationObservable = GetCurrentLocationEvent.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(navigator).showLocationResolutionDialog(mockStatus)
    }

    @Test
    fun subscribe_onUnknownError() {
        val mockStatus: AvailableStatus = mock()
        whenever(mockStatus.isAvailable).thenReturn(false)

        val throwable = UnavailableLocationException(ERROR)
        val locationObservable = GetCurrentLocationEvent.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(view).showErrorToast(R.string.add_geofence_get_current_location_error)
    }

    // View

    @Test
    fun onMapLongClicked_dropMarker() {
        // can drop
        run {
            whenever(view.canDropMarker()).thenReturn(true)

            val latLng = LatLng(1.0, 2.0)
            presenter.onMapLongClicked(latLng)

            verify(view).dropMarker(latLng)
        }

        reset(view)

        // can't drop
        run {
            whenever(view.canDropMarker()).thenReturn(false)

            val latLng = LatLng(0.0, 0.0)
            presenter.onMapLongClicked(latLng)

            verify(view, never()).dropMarker(any())
        }
    }

    @Test
    fun onMarkerInfoWindowClicked_showEditDialog() {
        // can show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)

            presenter.onMarkerInfoWindowClicked()
            verify(view).showEditDialog(geofence)
        }

        reset(view)

        // can't show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)

            presenter.onMarkerInfoWindowClicked()
            verify(view, never()).showEditDialog(any())
        }
    }

    @Test
    fun onMarkerDropped_showInfoWindow() {
        val mockInternal: zzf = mock()
        whenever(mockInternal.position).thenReturn(LatLng(1.0, 2.0))
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onMarkerDropped(marker)

        verify(view).showMarkerInfoWindow(marker)
    }

    @Test
    fun onMarkerDropped_updateGeofenceLocation() {
        val mockInternal: zzf = mock()
        val expectLatLng = LatLng(1.0, 2.0)
        whenever(mockInternal.position).thenReturn(expectLatLng)
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onMarkerDropped(marker)

        verify(geofence).latlng(expectLatLng)
    }

    @Test
    fun onMarkerDropped_shouldMoveMarkerPosition() {
        val mockInternal: zzf = mock()
        whenever(mockInternal.position).thenReturn(LatLng(1.0, 2.0))
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onMarkerDropped(marker)

        verify(view).setLocation(marker.position)
    }

    @Test
    fun onMarkerClicked_showEditDialog() {
        val marker = Marker(mock())
        // can show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)

            presenter.onMarkerClicked(marker)
            verify(view).showEditDialog(geofence)
        }

        reset(view)

        // can't show
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)

            presenter.onMarkerClicked(marker)
            verify(view, never()).showEditDialog(any())
        }
    }

    @Test
    fun onMarkerClicked_showMarkerInfoWindow() {

        whenever(view.isDragActionModeShown()).thenReturn(false)

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
    fun onMarkerDragStart_startMarkerDragMode() {
        val marker = Marker(mock())

        run {
            whenever(view.isDragActionModeShown()).thenReturn(false)

            presenter.onMarkerDragStart(marker)
            verify(view).startMarkerDragMode()
        }

        reset(view)

        // now, shown
        run {
            whenever(view.isDragActionModeShown()).thenReturn(true)

            presenter.onMarkerDragStart(marker)
            verify(view, never()).startMarkerDragMode()
        }
    }

    @Test
    fun onMarkerDragStart_hideInfoWindow() {
        // is not shown
        run {
            val mockInternal: zzf = mock()
            whenever(mockInternal.isInfoWindowShown).thenReturn(true)

            val marker = Marker(mockInternal)
            presenter.onMarkerDragStart(marker)

            verify(view).hideMarkerInfoWindow(marker)
        }

        reset(view)

        // shown
        run {
            val mockInternal: zzf = mock()
            whenever(mockInternal.isInfoWindowShown).thenReturn(false)

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

    @Test
    fun onMarkerDragEnd_hideGeofenceCircle() {
        presenter.onMarkerDragEnd()

        verify(view).showGeofenceCircle()
        verify(view, never()).hideGeofenceCircle()
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
        val mockInternal: zzf = mock()
        whenever(mockInternal.position).thenReturn(LatLng(1.0, 2.0))
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onFinishedDragMode(marker)

        verify(view).showDoneButton()
        verify(view, never()).hideDoneButton()
    }

    @Test
    fun onFinishedDragMode_updateGeofenceLocation() {
        val mockInternal: zzf = mock()
        val expectLatLng = LatLng(1.0, 2.0)
        whenever(mockInternal.position).thenReturn(expectLatLng)
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onMarkerDropped(marker)

        verify(geofence).latlng(expectLatLng)
    }

    @Test
    fun onFinishedDragMode_shouldMoveMarkerPosition() {
        val mockInternal: zzf = mock()
        whenever(mockInternal.position).thenReturn(LatLng(1.0, 2.0))
        doNothing().whenever(geofence).latlng(any())

        val marker = Marker(mockInternal)
        presenter.onFinishedDragMode(marker)

        verify(view).setLocation(marker.position)
    }

    @Test
    fun onUpdateGeofence_shouldUpdateGeofence() {
        val updatedGeofence = Geofence().apply {
            name = "updated"
            circle = Circle(1.0, 2.0, 3.0)
            toggle = Toggle(true, false)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(geofence).name = updatedGeofence.name
        verify(geofence).circle = updatedGeofence.circle
        verify(geofence).toggle = updatedGeofence.toggle
    }

    @Test
    fun onUpdateGeofence_updateMarkerInfoWindow() {
        val updatedGeofence = Geofence().apply {
            name = "updated"
            circle = Circle(1.0, 2.0, 3.0)
            toggle = Toggle(true, false)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(view).setMarkerInfoWindow(title = updatedGeofence.name, snippet = null)
    }

    @Test
    fun onUpdateGeofence_setGeofenceRadius() {
        val updatedGeofence = Geofence().apply {
            circle = Circle(1.0, 2.0, 3.0)
        }
        presenter.onGeofenceUpdated(updatedGeofence)

        verify(view).setGeofenceRadius(updatedGeofence.radius)
    }

    @Test
    fun onDoneButtonClicked_registerGeofencing() {
        whenever(view.hasGeofenceGeometory()).thenReturn(true)
        whenever(geofence.name).thenReturn("test-1")
        whenever(registerGeofencingTask.register(any())).thenReturn(Single.just(true))

        presenter.onDoneButtonClicked()

        verify(registerGeofencingTask).register(geofence)
        verify(view).setDoneButtonEnabled(false)
        verify(view, never()).showErrorToast(any())
    }

    @Test
    fun onDoneButtonClicked_showErrorToast() {
        run {
            whenever(view.hasGeofenceGeometory()).thenReturn(false)

            presenter.onDoneButtonClicked()

            verify(view).showErrorToast(R.string.add_geofence_not_yet_add_marker)
            verify(view, never()).showErrorToast(R.string.add_geofence_not_entered_name)
            verify(registerGeofencingTask, never()).register(any())
        }

        reset(view, registerGeofencingTask)

        run {
            whenever(view.hasGeofenceGeometory()).thenReturn(true)
            whenever(geofence.name).thenReturn("")

            presenter.onDoneButtonClicked()

            verify(view).showErrorToast(R.string.add_geofence_not_entered_name)
            verify(view, never()).showErrorToast(R.string.add_geofence_not_yet_add_marker)
            verify(registerGeofencingTask, never()).register(any())
        }
    }

    // Navigator

    @Test
    fun onUpNavigationButtonClicked_navigationToUp() {
        presenter.onUpNavigationButtonClicked()

        verify(navigator).navigationToUp()
    }

    @Test
    fun onLocationResolutionResult_requestLocation() {
        presenter.onLocationResolutionResult(resolved = true)

        verify(getCurrentLocationTask).requestLocation()
        verify(view, never()).showErrorToast(0)
    }

    @Test
    fun onLocationResolutionResult_showCurrentLocationErrorToast() {
        presenter.onLocationResolutionResult(resolved = false)

        verify(view).showErrorToast(R.string.add_geofence_get_current_location_error)
        verify(getCurrentLocationTask, never()).requestLocation()
    }

    // RuntimePermission

    @Test
    fun onLocationPermissionsResult_requestLocation() {
        presenter.onLocationPermissionsResult(usage = GET_LOCATION, granted = true)

        verify(getCurrentLocationTask).requestLocation()
        verify(view).enableMyLocationButton(true)
        verify(view, never()).showErrorToast(0)
    }

    @Test
    fun onLocationPermissionsResult_showErrorToast() {
        presenter.onLocationPermissionsResult(usage = GET_LOCATION, granted = false)

        verify(view).showErrorToast(R.string.add_geofence_get_current_location_error)
        verify(view, never()).enableMyLocationButton(true)
        verify(getCurrentLocationTask, never()).requestLocation()
    }

    @Test
    fun onLocationPermissionsResult_registerGeofencing() {
        whenever(view.hasGeofenceGeometory()).thenReturn(true)
        whenever(registerGeofencingTask.register(any())).thenReturn(Single.just(true))

        presenter.onLocationPermissionsResult(usage = GEOFENCING, granted = true)

        verify(registerGeofencingTask).register(geofence)
        verify(view, never()).showLocationPermissionRationaleSnackbar()
    }

    @Test
    fun onLocationPermissionsResult_showLocationPermissionRationaleSnackbar() {
        presenter.onLocationPermissionsResult(usage = GEOFENCING, granted = false)

        verify(view).showLocationPermissionRationaleSnackbar()
        verify(view).setDoneButtonEnabled(true)
        verify(registerGeofencingTask, never()).register(geofence)
    }

    // internal

    @Test
    fun subscribeRegisterGeofencing_onSuccess() {
        // register successful
        run {
            whenever(registerGeofencingTask.register(any())).thenReturn(Single.just(true))
            presenter.subscribeRegisterGeofencing()

            verify(navigator).finish(geofence)
            verify(view, never()).showErrorToast(R.string.add_geofence_failed_register_geofence)
        }

        reset(view, navigator)

        // register failed
        run {
            whenever(registerGeofencingTask.register(any())).thenReturn(Single.just(false))
            presenter.subscribeRegisterGeofencing()

            verify(view).showErrorToast(R.string.add_geofence_failed_register_geofence)
            verify(view).setDoneButtonEnabled(true)
            verify(navigator, never()).finish(any())
        }
    }

    @Test
    fun subscribeRegisterGeofencing_onError() {
        // permission denied
        val error: Single<Boolean> = Single.error(UnavailableLocationException(status = PERMISSION_DENIED))
        whenever(registerGeofencingTask.register(any())).thenReturn(error)
        presenter.subscribeRegisterGeofencing()

        verify(permissions).requestLocationPermission(usage = GEOFENCING)
        verify(view).setDoneButtonEnabled(true)
    }
}