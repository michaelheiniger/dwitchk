package ch.qscqlmpa.dwitchgame.gameadvertising

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.joda.time.LocalDateTime

@Parcelize
data class AdvertisedGame(

    /**
     * [isNew] == true means that the advertised game is a new one, by opposition to an existing game [isNew] == false that can be resumed.
     */
    val isNew: Boolean,

    val gameName: String,

    /**
     * Identifier of the game that is shared by all participants (as opposed to a local ID that can differ across players).
     */
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,

    val gameIpAddress: String,

    /**
     * Destination port to be used by the guests to connect to the host.
     */
    val gamePort: Int,

    /**
     * Date and time when advertisement was received.
     */
    val discoveryTime: LocalDateTime = LocalDateTime.now()

) : Parcelable {

    fun discoveryTimeAsString(): String {
        return discoveryTime.toString("dd.MM.yyyy HH:mm:ss")
    }
}
