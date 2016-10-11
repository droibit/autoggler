package com.droibit.autoggler.edit.update

import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.rule.RxSchedulersOverrideRule
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
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

}