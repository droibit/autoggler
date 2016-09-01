package com.droibit.autoggler.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber

class LocationResolutionSourceTest {

    private lateinit var resolutionSource: LocationResolutionSource

    @Before
    fun setUp() {
        resolutionSource = LocationResolutionSource()
    }

    @Test
    fun prepareStartResolution_triggerCalled() {
        val testSubscriber = TestSubscriber.create<Unit>()

        resolutionSource.trigger.subscribe(testSubscriber)
        resolutionSource.prepareStartResolution(mock())

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(null)
    }

    @Test
    fun startResolutionForResult_actionInvoked() {
        val mockAction: () -> Unit = mock()
        resolutionSource.prepareStartResolution(mockAction)
        assertThat(resolutionSource.startResolutionAction).isSameAs(mockAction)

        resolutionSource.startResolutionForResult()
        verify(mockAction).invoke()
        assertThat(resolutionSource.startResolutionAction).isNull()
    }
}