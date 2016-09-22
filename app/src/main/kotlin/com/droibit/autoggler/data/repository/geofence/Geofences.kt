package com.droibit.autoggler.data.repository.geofence

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.io.Serializable

@RealmClass
open class Circle(
        open var lat: Double = 0.0,
        open var lng: Double = 0.0,
        open var radius: Double = 0.0
) : RealmModel, Serializable, Cloneable {

    val latLng: LatLng
        get() = LatLng(lat, lng)

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

    public override fun clone(): Circle {
        try {
            return super.clone() as Circle
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }
}

@RealmClass
open class Toggle(
        open var wifi: Boolean = false,
        open var vibration: Boolean = false
) : RealmModel, Serializable, Cloneable {

    override fun toString(): String {
        return "Toggle(wifi=$wifi, vibration=$vibration)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Toggle) return false

        if (wifi != other.wifi) return false
        if (vibration != other.vibration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = wifi.hashCode()
        result = 31 * result + vibration.hashCode()
        return result
    }

    public override fun clone(): Toggle {
        try {
            return super.clone() as Toggle
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }
}

@RealmClass
open class Geofence(
        @PrimaryKey open var id: Long = 0L,
        @Required open var name: String = "",
        open var enabled: Boolean = true,
        open var circle: Circle = Circle(),
        open var toggle: Toggle = Toggle(),
        open var createdAt: Long = 0L
) : RealmModel, Serializable, Cloneable {

    val radius: Double
        get() = circle.radius

    val latLong: LatLng
        get() = circle.latLng

    // TODO: setter
    open fun latlng(latLng: LatLng) {
        circle.apply {
            lat = latLng.latitude
            lng = latLng.longitude
        }
    }

    override fun toString(): String{
        return "Geofence(id=$id, name='$name', enabled=$enabled, circle=$circle, toggle=$toggle, createdAt=$createdAt)"
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is Geofence) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (enabled != other.enabled) return false
        if (circle != other.circle) return false
        if (toggle != other.toggle) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int{
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + circle.hashCode()
        result = 31 * result + toggle.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    public override fun clone(): Geofence {
        try {
            val v = super.clone() as Geofence
            return v.apply {
                circle = this@Geofence.circle.clone()
                toggle = this@Geofence.toggle.clone()
            }
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }
}