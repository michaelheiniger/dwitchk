package ch.qscqlmpa.dwitchgame.gamediscovery

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

@Parcelize
data class AdvertisedGame(
    val isNew: Boolean,
    val gameName: String,
    val gameCommonId: GameCommonId,
    val gameIpAddress: String,
    val gamePort: Int,
    val discoveryTime: LocalTime = LocalTime.now()
) : Parcelable {

    fun discoveryTimeAsString(): String {
        return discoveryTime.toString("HH:mm:ss")
    }
}
