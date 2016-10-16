package com.droibit.autoggler.data.provider.geometory

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

data class CompositeGeometry(val marker: Marker, val circle: Circle) {

    class Options(var marker: MarkerOptions? = null, var circle: CircleOptions? = null) : Parcelable {
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Options> = object : Parcelable.Creator<Options> {
                override fun createFromParcel(source: Parcel): Options = Options(source)
                override fun newArray(size: Int): Array<Options?> = arrayOfNulls(size)
            }
        }

        constructor(source: Parcel) : this(source.readParcelable(), source.readParcelable())

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeParcelable(marker, flags)
            dest?.writeParcelable(circle, flags)
        }

        operator fun component1() = marker

        operator fun component2() = circle
    }
}

private inline fun <reified T : Parcelable> Parcel.readParcelable(): T? = readParcelable(T::class.java.classLoader)
