package com.example.mastersproject

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class Item(
    var name: String? = null,
    var description: String? = null,
    var filter1: String? = null,
    var filter2: String? = null,
    var priceBracket: String? = null,
    var imageurl: String? = null,
    var link: String? = null,
    val location: GeoPoint? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readGeoPoint()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(filter1)
        parcel.writeString(filter2)
        parcel.writeString(priceBracket)
        parcel.writeString(imageurl)
        parcel.writeString(link)
        parcel.writeGeoPoint(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}

// Extension function to write GeoPoint to Parcel
fun Parcel.writeGeoPoint(geoPoint: GeoPoint?) {
    if (geoPoint != null) {
        writeDouble(geoPoint.latitude)
        writeDouble(geoPoint.longitude)
    } else {
        writeDouble(Double.NaN)
        writeDouble(Double.NaN)
    }
}

// Extension function to read GeoPoint from Parcel
fun Parcel.readGeoPoint(): GeoPoint? {
    val latitude = readDouble()
    val longitude = readDouble()
    return if (!latitude.isNaN() && !longitude.isNaN()) GeoPoint(latitude, longitude) else null
}
