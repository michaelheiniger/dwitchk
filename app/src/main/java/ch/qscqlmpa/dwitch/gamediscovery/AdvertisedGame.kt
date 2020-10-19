package ch.qscqlmpa.dwitch.gamediscovery

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalTime

data class AdvertisedGame(val name: String,
                          val ipAddress: String,
                          val port: Int,
                          val discoveryTime: LocalTime = LocalTime.now()
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "not-set",
            parcel.readString() ?: "not-set",
            parcel.readInt(),
            LocalTime.parse(parcel.readString()))

    fun discoveryTimeAsString(): String {
        return discoveryTime.toString("HH:mm:ss")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(ipAddress)
        parcel.writeInt(port)
        parcel.writeString(discoveryTime.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdvertisedGame> {
        override fun createFromParcel(parcel: Parcel): AdvertisedGame {
            return AdvertisedGame(parcel)
        }

        override fun newArray(size: Int): Array<AdvertisedGame?> {
            return arrayOfNulls(size)
        }
    }
}
