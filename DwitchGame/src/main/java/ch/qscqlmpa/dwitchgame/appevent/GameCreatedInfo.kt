package ch.qscqlmpa.dwitchgame.appevent

import android.os.Parcelable
import ch.qscqlmpa.dwitchgame.gameadvertising.GameCommonIdParceler
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith

@Parcelize
data class GameCreatedInfo(
    val isNew: Boolean,
    val gameLocalId: Long,
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,
    val gameName: String,
    val localPlayerLocalId: Long,
    val gamePort: Int
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, gamePort: Int) :
        this(
            true,
            insertGameResult.gameLocalId,
            insertGameResult.gameCommonId,
            insertGameResult.gameName,
            insertGameResult.localPlayerLocalId,
            gamePort
        )
}
