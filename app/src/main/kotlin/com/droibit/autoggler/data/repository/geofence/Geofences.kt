package com.droibit.autoggler.data.repository.geofence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

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
        @Required open var name: String = "",
        open var circle: Circle = Circle(),
        open var trigger: Trigger = Trigger()
) : RealmObject()