package com.droibit.autoggler.data.repository.geofence

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class Circle(
        open var lat: Double = 0.0,
        open var lng: Double = 0.0,
        open var radius: Double = 0.0
) : RealmModel {

    override fun toString(): String {
        return "Circle(lat=$lat, lng=$lng, radius=$radius)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Circle) return false

        if (lat != other.lat) return false
        if (lng != other.lng) return false
        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lng.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }
}

@RealmClass
open class Trigger(
        open var toggleWifi: Boolean = false,
        open var toggleVibration: Boolean = false
) : RealmModel {

    override fun toString(): String {
        return "Trigger(toggleWifi=$toggleWifi, toggleVibration=$toggleVibration)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Trigger) return false

        if (toggleWifi != other.toggleWifi) return false
        if (toggleVibration != other.toggleVibration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = toggleWifi.hashCode()
        result = 31 * result + toggleVibration.hashCode()
        return result
    }
}

@RealmClass
open class Geofence(
        @PrimaryKey open var id: Long = 0L,
        @Required open var name: String = "",
        open var enabled: Boolean = true,
        open var circle: Circle = Circle(),
        open var trigger: Trigger = Trigger()
) : RealmModel {

    override fun toString(): String {
        return "Geofence(id=$id, name='$name', enabled=$enabled, circle=$circle, trigger=$trigger)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Geofence) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (enabled != other.enabled) return false
        if (circle != other.circle) return false
        if (trigger != other.trigger) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + circle.hashCode()
        result = 31 * result + trigger.hashCode()
        return result
    }
}