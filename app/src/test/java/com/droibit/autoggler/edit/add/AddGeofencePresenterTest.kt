package com.droibit.autoggler.edit.add

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import com.droibit.autoggler.data.checker.permission.RuntimePermissionChecker
import com.droibit.autoggler.data.repository.location.LocationAvailableStatus
import com.droibit.autoggler.edit.add.AddGeofenceContract.GetCurrentLocationTask.Event
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException
import com.droibit.autoggler.edit.add.AddGeofenceContract.UnavailableLocationException.ErrorStatus.*
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
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
    lateinit var permissionChecker: RuntimePermissionChecker

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
                permissionChecker,
                subscriptions
        )
    }

    @Test
    fun onCreate_requestLocation() {
        presenter.onCreate()

        verify(getCurrentLocationTask).requestLocation()
    }

    @Test
    fun onCreate_enableMyLocationButton() {
        whenever(permissionChecker.isRuntimePermissionsGranted(ACCESS_FINE_LOCATION)).thenReturn(true)
        presenter.onCreate()

        verify(view).enableMyLocationButton(true)
    }

    @Test
    fun subscribe_onSuccess() {
        val mockLocation: Location = mock()
        val locationObservable = Event.OnSuccess(mockLocation)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(view).showLocation(mockLocation)
    }

    @Test
    fun subscribe_onPermissionDenied() {
        val mockStatus: LocationAvailableStatus = mock()
        whenever(mockStatus.isEnabled).thenReturn(false)

        val throwable = UnavailableLocationException(PERMISSION_DENIED)
        val locationObservable = Event.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(permissions).requestPermissions(ACCESS_FINE_LOCATION)
    }

    @Test
    fun subscribe_onResolutionRequired() {
        val mockStatus: LocationAvailableStatus = mock()
        whenever(mockStatus.isEnabled).thenReturn(false)
        whenever(mockStatus.isResolutionRequired).thenReturn(true)

        val throwable = UnavailableLocationException(RESOLUTION_REQUIRED, option = mockStatus)
        val locationObservable = Event.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(navigator).showLocationResolutionDialog(mockStatus)
    }

    @Test
    fun subscribe_onError() {
        val mockStatus: LocationAvailableStatus = mock()
        whenever(mockStatus.isEnabled).thenReturn(false)

        val throwable = UnavailableLocationException(ERROR)
        val locationObservable = Event.OnError(throwable)
        whenever(getCurrentLocationTask.asObservable()).thenReturn(locationObservable.toSingletonObservable())

        presenter.subscribe()

        verify(getCurrentLocationTask).asObservable()
        verify(view).showCurrentLocationErrorToast(0)
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
        verify(view, never()).showCurrentLocationErrorToast(0)
    }

    @Test
    fun onLocationResolutionResult_showCurrentLocationErrorToast() {
        presenter.onLocationResolutionResult(resolved = false)

        verify(view).showCurrentLocationErrorToast(0)
        verify(getCurrentLocationTask, never()).requestLocation()
    }

    // RuntimePermission

    @Test
    fun onRequestPermissionsResult_requestLocation() {
        val grantResults = intArrayOf(1)
        whenever(permissionChecker.isRuntimePermissionsGranted(*grantResults)).thenReturn(true)

        presenter.onRequestPermissionsResult(grantResults)

        verify(getCurrentLocationTask).requestLocation()
        verify(view, never()).showCurrentLocationErrorToast(0)
    }

    @Test
    fun onRequestPermissionsResult_showErrorToast() {
        val grantResults = intArrayOf(0)
        whenever(permissionChecker.isRuntimePermissionsGranted(*grantResults)).thenReturn(false)

        presenter.onRequestPermissionsResult(grantResults)


        verify(view).showCurrentLocationErrorToast(0)
        verify(getCurrentLocationTask, never()).requestLocation()
    }
}