package ch.qscqlmpa.dwitch.gamediscovery

import android.os.Parcelable
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

@Parcelize
data class AdvertisedGame(
    val gameName: String,
    val gameCommonId: GameCommonId,
    val gameIpAddress: String,
    val gamePort: Int,
    val discoveryTime: LocalTime = LocalTime.now()
) : Parcelable {

//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "not-set",
//        parcel.readParcelable<GameCommonId>(GameCommonId::class.java.classLoader)!!,
//        parcel.readString() ?: "not-set",
//        parcel.readInt(),
//        LocalTime.parse(parcel.readString())
//    )

    fun discoveryTimeAsString(): String {
        return discoveryTime.toString("HH:mm:ss")
    }
}
