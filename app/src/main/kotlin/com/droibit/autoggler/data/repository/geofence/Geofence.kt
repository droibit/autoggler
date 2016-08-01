package com.droibit.autoggler.data.repository.geofence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Circle(
        open var lat: Double = 0.0,
        open var lng: Double = 0.0,
        open var radius: Double = 0.0
) : RealmObject()

open class Trigger(
        open var toggleWifi: Boolean = false,
        open var toggleVibration: Boolean = false
) : RealmObject()

open class Geofence(
        @PrimaryKey open var id: Long = 0L,
        open var name: String = "",
        open var circle: Circle,
        open var trigger: Trigger
) : RealmObject()