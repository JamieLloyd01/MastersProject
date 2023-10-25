package com.example.mastersproject

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class Item(var name: String ?= null, var description: String ?=null, var filter1: String ?=null, var filter2: String?=null, var priceBracket: String ?=null, var imageurl : String?=null, var link : String?=null, val location: GeoPoint?=null)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        // Read properties from the parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        name = parcel.readString()
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
