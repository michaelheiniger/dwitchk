package ch.qscqlmpa.dwitchgame.appevent

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameCreatedInfo(
    val gameLocalId: Long,
    val gameCommonId: GameCommonId,
    val gameName: String,
    val localPlayerLocalId: Long,
    val gamePort: Int
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, gamePort: Int) :
            this(
                insertGameResult.gameLocalId,
                insertGameResult.gameCommonId,
                insertGameResult.gameName,
                insertGameResult.localPlayerLocalId,
                gamePort
            )
}