package ch.qscqlmpa.dwitchgame.gamediscovery

import android.os.Parcelable
import ch.qscqlmpa.dwitchgame.gameadvertising.GameCommonIdParceler
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import org.joda.time.LocalTime

@Parcelize
data class AdvertisedGame(
    val isNew: Boolean,
    val gameName: String,
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,
    val gameIpAddress: String,
    val gamePort: Int,
    val discoveryTime: LocalTime = LocalTime.now()
) : Parcelable {

    fun discoveryTimeAsString(): String {
        return discoveryTime.toString("HH:mm:ss")
    }
}
