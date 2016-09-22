package com.droibit.autoggler.data.repository.geofence

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GeofencesTest {

    @Test
    fun clone_geofence() {
        val geofence = Geofence(
                id = 1L,
                name = "geofence",
                enabled = true,
                circle = Circle(lat = 1.0, lng = 2.0, radius = 3.0),
                toggle = Toggle(wifi = true, vibration = false)
        )
        assertThat(geofence.clone()).isEqualTo(geofence)
    }
}