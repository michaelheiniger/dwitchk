package ch.qscqlmpa.dwitchgame.appevent

import android.os.Parcelable
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchstore.InsertGameResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameJoinedInfo(
    val gameLocalId: Long,
    val localPlayerLocalId: Long,
    val gameIpAddress: String,
    val gamePort: Int
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, advertisedGame: AdvertisedGame) :
        this(
            insertGameResult.gameLocalId,
            insertGameResult.localPlayerLocalId,
            advertisedGame.gameIpAddress,
            advertisedGame.gamePort
        )
}
