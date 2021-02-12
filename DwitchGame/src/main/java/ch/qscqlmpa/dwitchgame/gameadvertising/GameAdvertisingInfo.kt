package ch.qscqlmpa.dwitchgame.gameadvertising

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GameAdvertisingInfo(
    val isNew: Boolean,
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,
    val gameName: String,
    val gamePort: Int
) : Parcelable
